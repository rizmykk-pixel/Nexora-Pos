create extension if not exists pgcrypto;

create table if not exists tenants (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  created_at timestamptz not null default now()
);
create table if not exists outlets (
  id uuid primary key default gen_random_uuid(), tenant_id uuid not null references tenants(id) on delete cascade,
  name text not null, created_at timestamptz not null default now(), unique (tenant_id, name)
);
create table if not exists devices (
  id uuid primary key default gen_random_uuid(), tenant_id uuid not null references tenants(id) on delete cascade,
  outlet_id uuid not null references outlets(id) on delete cascade, device_key text not null unique,
  revoked_at timestamptz, created_at timestamptz not null default now()
);
create table if not exists memberships (
  tenant_id uuid not null references tenants(id) on delete cascade, user_id uuid not null,
  role text not null check (role in (
    'owner', 'hq_admin', 'regional_manager', 'entity_manager', 'outlet_manager',
    'supervisor', 'manager', 'cashier', 'staff', 'accountant', 'warehouse',
    'procurement', 'auditor', 'support', 'custom'
  )), created_at timestamptz not null default now(),
  primary key (tenant_id, user_id)
);
create table if not exists products (
  id uuid primary key default gen_random_uuid(), tenant_id uuid not null references tenants(id) on delete cascade,
  sku text not null, name text not null, price_cents integer not null check (price_cents >= 0),
  active boolean not null default true, created_at timestamptz not null default now(), unique (tenant_id, sku)
);
create table if not exists orders (
  id uuid primary key default gen_random_uuid(), tenant_id uuid not null references tenants(id) on delete cascade,
  outlet_id uuid not null references outlets(id) on delete restrict, device_id uuid references devices(id) on delete set null,
  status text not null check (status in ('pending', 'paid', 'cancelled')), idempotency_key text not null,
  total_cents integer not null check (total_cents >= 0), created_at timestamptz not null default now(),
  unique (tenant_id, idempotency_key)
);
create table if not exists order_lines (
  id uuid primary key default gen_random_uuid(), order_id uuid not null references orders(id) on delete cascade,
  product_id uuid not null references products(id) on delete restrict, quantity integer not null check (quantity > 0),
  unit_price_cents integer not null check (unit_price_cents >= 0)
);
create table if not exists sync_cursors (
  device_id uuid primary key references devices(id) on delete cascade, cursor bigint not null default 0,
  updated_at timestamptz not null default now()
);
create table if not exists audit_events (
  id uuid primary key default gen_random_uuid(), tenant_id uuid not null references tenants(id) on delete cascade,
  actor_user_id uuid, action text not null, entity_type text not null, entity_id uuid, metadata jsonb not null default '{}',
  created_at timestamptz not null default now()
);
create table if not exists payments (
  id uuid primary key default gen_random_uuid(), order_id uuid not null references orders(id) on delete restrict,
  provider text not null check (provider in ('midtrans', 'xendit', 'manual')),
  provider_payment_id text,
  status text not null check (status in ('initiated', 'pending', 'authorized', 'paid', 'failed', 'expired', 'cancelled', 'unknown')),
  amount_cents integer not null check (amount_cents > 0),
  raw_payload jsonb not null default '{}',
  created_at timestamptz not null default now(), updated_at timestamptz not null default now(),
  unique (provider, provider_payment_id)
);

create index if not exists outlets_tenant_idx on outlets(tenant_id);
create index if not exists products_tenant_active_idx on products(tenant_id, active);
create index if not exists orders_tenant_outlet_created_idx on orders(tenant_id, outlet_id, created_at desc);
create index if not exists audit_events_tenant_created_idx on audit_events(tenant_id, created_at desc);
create index if not exists payments_order_idx on payments(order_id);

alter table tenants enable row level security;
alter table outlets enable row level security;
alter table devices enable row level security;
alter table memberships enable row level security;
alter table products enable row level security;
alter table orders enable row level security;
alter table order_lines enable row level security;
alter table sync_cursors enable row level security;
alter table audit_events enable row level security;
alter table payments enable row level security;

create policy tenant_members_can_read on tenants for select using (id in (select tenant_id from memberships where user_id = auth.uid()));
create policy members_can_read_own_membership on memberships for select using (user_id = auth.uid());
create policy tenant_members_can_read_outlets on outlets for select using (tenant_id in (select tenant_id from memberships where user_id = auth.uid()));
create policy tenant_members_can_read_devices on devices for select using (tenant_id in (select tenant_id from memberships where user_id = auth.uid()));
create policy tenant_members_can_read_products on products for select using (tenant_id in (select tenant_id from memberships where user_id = auth.uid()));
create policy tenant_members_can_read_orders on orders for select using (tenant_id in (select tenant_id from memberships where user_id = auth.uid()));
create policy tenant_members_can_write_orders on orders for insert with check (
  tenant_id in (select tenant_id from memberships where user_id = auth.uid())
  and exists (select 1 from outlets where outlets.id = orders.outlet_id and outlets.tenant_id = orders.tenant_id)
  and (orders.device_id is null or exists (
    select 1 from devices where devices.id = orders.device_id
      and devices.tenant_id = orders.tenant_id and devices.outlet_id = orders.outlet_id and devices.revoked_at is null
  ))
);
create policy tenant_members_can_read_order_lines on order_lines for select using (order_id in (select id from orders where tenant_id in (select tenant_id from memberships where user_id = auth.uid())));
create policy tenant_members_can_write_order_lines on order_lines for insert with check (order_id in (select id from orders where tenant_id in (select tenant_id from memberships where user_id = auth.uid())));
create policy device_can_read_own_cursor on sync_cursors for select using (device_id in (select id from devices where tenant_id in (select tenant_id from memberships where user_id = auth.uid())));
create policy tenant_members_can_read_audit_events on audit_events for select using (tenant_id in (select tenant_id from memberships where user_id = auth.uid()));
create policy tenant_members_can_read_payments on payments for select using (order_id in (select id from orders where tenant_id in (select tenant_id from memberships where user_id = auth.uid())));