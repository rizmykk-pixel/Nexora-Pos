import { randomUUID } from 'node:crypto';
// Global state for development mode
const orders = new Map();
const orderIdempotency = new Map();
// Environment variables for Node.js (local development)
const port = Number(process.env.PORT ?? 8787);
const supabaseUrl = process.env.SUPABASE_URL ?? '';
const supabaseServiceRoleKey = process.env.SUPABASE_SERVICE_ROLE_KEY ?? '';
const runtimeEnvironment = process.env.NEXOVA_ENVIRONMENT ?? process.env.NODE_ENV ?? 'development';
const storageMode = process.env.NEXOVA_STORAGE_MODE ?? 'memory';
const usesConfiguredSupabaseStorage = storageMode === 'supabase' && supabaseUrl !== '' && supabaseServiceRoleKey !== '';
const midtransServerKey = process.env.MIDTRANS_SERVER_KEY ?? '';
const midtransBaseUrl = process.env.MIDTRANS_BASE_URL ?? 'https://api.sandbox.midtrans.com';
if (runtimeEnvironment !== 'development' && !usesConfiguredSupabaseStorage) {
    throw new Error('Persistent Supabase storage is required outside development. Set NEXOVA_STORAGE_MODE=supabase and provide SUPABASE_URL and SUPABASE_SERVICE_ROLE_KEY.');
}
function send(status, body) {
    return new Response(JSON.stringify(body), {
        status,
        headers: { 'content-type': 'application/json; charset=utf-8' },
    });
}
async function body(request) {
    const raw = await request.text();
    if (!raw)
        return {};
    const parsed = JSON.parse(raw);
    if (!parsed || typeof parsed !== 'object' || Array.isArray(parsed)) {
        throw new Error('Request body must be a JSON object');
    }
    return parsed;
}
function errorResponse(status, code, message) {
    return send(status, { error: { code, message, requestId: randomUUID() } });
}
async function supabaseRequest(path, init = {}) {
    const response = await fetch(`${supabaseUrl}/rest/v1/${path}`, {
        ...init,
        headers: {
            apikey: supabaseServiceRoleKey,
            authorization: `Bearer ${supabaseServiceRoleKey}`,
            'content-type': 'application/json',
            ...init.headers,
        },
    });
    if (!response.ok) {
        throw new Error(`Supabase request failed with status ${response.status}`);
    }
    return (await response.json());
}
function bearerToken(request) {
    const header = request.headers.get('authorization');
    if (!header?.startsWith('Bearer '))
        return null;
    const token = header.slice('Bearer '.length).trim();
    return token || null;
}
async function authenticate(request) {
    if (runtimeEnvironment === 'development')
        return 'development-user';
    const token = bearerToken(request);
    if (!token)
        return null;
    const response = await fetch(`${supabaseUrl}/auth/v1/user`, {
        headers: {
            apikey: supabaseServiceRoleKey,
            authorization: `Bearer ${token}`,
        },
    });
    if (!response.ok)
        return null;
    const user = (await response.json());
    return user.id || null;
}
async function isTenantMember(userId, tenantId) {
    if (runtimeEnvironment === 'development')
        return true;
    const rows = await supabaseRequest(`memberships?user_id=eq.${encodeURIComponent(userId)}&tenant_id=eq.${encodeURIComponent(tenantId)}&select=tenant_id`);
    return rows.length > 0;
}
async function midtransRequest(path, init = {}) {
    if (!midtransServerKey) {
        throw new Error('Midtrans server key is not configured');
    }
    const basicAuth = Buffer.from(`${midtransServerKey}:`).toString('base64');
    const response = await fetch(`${midtransBaseUrl}${path}`, {
        ...init,
        headers: {
            authorization: `Basic ${basicAuth}`,
            'content-type': 'application/json',
            ...init.headers,
        },
    });
    const result = (await response.json());
    if (!response.ok) {
        throw new Error(result.status_message ?? `Midtrans request failed with status ${response.status}`);
    }
    return result;
}
function toApiOrder(order) {
    return {
        id: order.id,
        tenantId: order.tenant_id,
        outletId: order.outlet_id,
        deviceId: order.device_id,
        status: order.status,
        idempotencyKey: order.idempotency_key,
        totalCents: order.total_cents,
        createdAt: order.created_at,
    };
}
async function findPersistentOrder(id) {
    const rows = await supabaseRequest(`orders?id=eq.${encodeURIComponent(id)}&select=*`);
    return rows[0] ? toApiOrder(rows[0]) : null;
}
async function findPersistentOrderByIdempotency(tenantId, idempotencyKey) {
    const rows = await supabaseRequest(`orders?tenant_id=eq.${encodeURIComponent(tenantId)}&idempotency_key=eq.${encodeURIComponent(idempotencyKey)}&select=*`);
    return rows[0] ?? null;
}
async function route(request) {
    const url = new URL(request.url);
    const path = url.pathname;
    const method = request.method;
    if (method === 'GET' && path === '/api/v1/health') {
        return send(200, {
            status: 'ok',
            contractVersion: 'v1',
            environment: runtimeEnvironment,
            persistence: usesConfiguredSupabaseStorage ? 'supabase' : 'in-memory',
        });
    }
    if (method === 'GET' && path === '/api/v1/catalog') {
        const userId = await authenticate(request);
        if (!userId) {
            return errorResponse(401, 'UNAUTHENTICATED', 'A valid access token is required');
        }
        return send(200, { items: [] });
    }
    if (method === 'POST' && path === '/api/v1/payments') {
        const userId = await authenticate(request);
        if (!userId) {
            return errorResponse(401, 'UNAUTHENTICATED', 'A valid access token is required');
        }
        const payload = await body(request);
        const orderId = payload.orderId;
        const grossAmount = payload.grossAmount;
        if (typeof orderId !== 'string' || typeof grossAmount !== 'number' || grossAmount <= 0) {
            return errorResponse(400, 'INVALID_PAYMENT_REQUEST', 'orderId and positive grossAmount are required');
        }
        if (!midtransServerKey && runtimeEnvironment !== 'development') {
            return errorResponse(503, 'PAYMENT_NOT_CONFIGURED', 'Payment provider is not configured');
        }
        if (!midtransServerKey) {
            return errorResponse(503, 'PAYMENT_NOT_CONFIGURED', 'Payment provider is not configured');
        }
        const result = await midtransRequest('/v2/charge', {
            method: 'POST',
            body: JSON.stringify({
                payment_type: 'qris',
                transaction_details: { order_id: orderId, gross_amount: grossAmount },
            }),
        });
        return send(201, {
            paymentId: result.transaction_id ?? orderId,
            transactionId: result.order_id ?? orderId,
            status: result.transaction_status ?? 'pending',
            qrCodeUrl: result.actions?.find((action) => action.name === 'generate-qr-code')?.url ?? null,
        });
    }
    const paymentMatch = path.match(/^\/api\/v1\/payments\/([^/]+)$/);
    if (method === 'GET' && paymentMatch) {
        const userId = await authenticate(request);
        if (!userId) {
            return errorResponse(401, 'UNAUTHENTICATED', 'A valid access token is required');
        }
        if (!midtransServerKey) {
            return errorResponse(503, 'PAYMENT_NOT_CONFIGURED', 'Payment provider is not configured');
        }
        const result = await midtransRequest(`/v2/${encodeURIComponent(paymentMatch[1])}/status`);
        return send(200, {
            paymentId: paymentMatch[1],
            transactionId: result.order_id ?? paymentMatch[1],
            status: result.transaction_status ?? 'unknown',
        });
    }
    if (method === 'POST' && path === '/api/v1/orders') {
        if (!usesConfiguredSupabaseStorage && runtimeEnvironment !== 'development') {
            return errorResponse(503, 'PERSISTENCE_NOT_CONFIGURED', 'Persistent order storage is not configured');
        }
        const idempotencyKey = request.headers.get('idempotency-key');
        if (typeof idempotencyKey !== 'string' || idempotencyKey.trim() === '') {
            return errorResponse(400, 'MISSING_IDEMPOTENCY_KEY', 'Idempotency-Key is required');
        }
        const payload = await body(request);
        if (usesConfiguredSupabaseStorage) {
            const tenantId = payload.tenantId;
            const outletId = payload.outletId;
            const totalCents = payload.totalCents;
            if (typeof tenantId !== 'string' || typeof outletId !== 'string' || typeof totalCents !== 'number') {
                return errorResponse(400, 'INVALID_ORDER_CONTEXT', 'tenantId, outletId, and totalCents are required');
            }
            const userId = await authenticate(request);
            if (!userId || !(await isTenantMember(userId, tenantId))) {
                return errorResponse(403, 'TENANT_ACCESS_DENIED', 'The authenticated user is not a member of this tenant');
            }
            const existing = await findPersistentOrderByIdempotency(tenantId, idempotencyKey);
            if (existing) {
                if (existing.outlet_id !== outletId || existing.total_cents !== totalCents) {
                    return errorResponse(409, 'IDEMPOTENCY_CONFLICT', 'Idempotency-Key was used with a different order');
                }
                return send(200, toApiOrder(existing));
            }
            const rows = await supabaseRequest('orders', {
                method: 'POST',
                headers: { Prefer: 'return=representation' },
                body: JSON.stringify({
                    tenant_id: tenantId,
                    outlet_id: outletId,
                    device_id: typeof payload.deviceId === 'string' ? payload.deviceId : null,
                    status: 'pending',
                    idempotency_key: idempotencyKey,
                    total_cents: totalCents,
                }),
            });
            return send(201, rows[0] ? toApiOrder(rows[0]) : {});
        }
        const payloadHash = JSON.stringify(payload);
        const existing = orderIdempotency.get(idempotencyKey);
        if (existing) {
            if (existing.payload !== payloadHash) {
                return errorResponse(409, 'IDEMPOTENCY_CONFLICT', 'Idempotency-Key was used with a different payload');
            }
            return send(200, existing.order);
        }
        const id = randomUUID();
        const order = { id, status: 'pending', ...payload };
        orders.set(id, order);
        orderIdempotency.set(idempotencyKey, { payload: payloadHash, order });
        return send(201, order);
    }
    const orderMatch = path.match(/^\/api\/v1\/orders\/([^/]+)$/);
    if (method === 'GET' && orderMatch) {
        const order = usesConfiguredSupabaseStorage
            ? await findPersistentOrder(orderMatch[1])
            : orders.get(orderMatch[1]);
        if (!order) {
            return errorResponse(404, 'NOT_FOUND', 'Order not found');
        }
        if (usesConfiguredSupabaseStorage) {
            const userId = await authenticate(request);
            const tenantId = order.tenantId;
            if (!userId || typeof tenantId !== 'string' || !(await isTenantMember(userId, tenantId))) {
                return errorResponse(403, 'TENANT_ACCESS_DENIED', 'The authenticated user is not a member of this tenant');
            }
        }
        return send(200, order);
    }
    if (method === 'POST' && path === '/api/v1/sync/push') {
        const userId = await authenticate(request);
        if (!userId) {
            return errorResponse(401, 'UNAUTHENTICATED', 'A valid access token is required');
        }
        const payload = await body(request);
        return send(200, { acknowledged: Array.isArray(payload.operations) ? payload.operations : [] });
    }
    if (method === 'GET' && path === '/api/v1/sync/pull') {
        const userId = await authenticate(request);
        if (!userId) {
            return errorResponse(401, 'UNAUTHENTICATED', 'A valid access token is required');
        }
        return send(200, { cursor: url.searchParams.get('cursor'), changes: [] });
    }
    if (method === 'POST' && path === '/api/v1/webhooks/midtrans') {
        if (!midtransServerKey) {
            return errorResponse(503, 'PAYMENT_NOT_CONFIGURED', 'Payment provider is not configured');
        }
        const payload = await body(request);
        const signatureKey = request.headers.get('x-callback-signature');
        if (typeof signatureKey !== 'string') {
            return errorResponse(400, 'INVALID_SIGNATURE', 'X-Callback-Signature header is required');
        }
        const orderId = payload.order_id;
        const transactionStatus = payload.transaction_status;
        const fraudStatus = payload.fraud_status;
        if (typeof orderId !== 'string' || typeof transactionStatus !== 'string') {
            return errorResponse(400, 'INVALID_WEBHOOK_PAYLOAD', 'order_id and transaction_status are required');
        }
        if (usesConfiguredSupabaseStorage) {
            const rows = await supabaseRequest(`orders?id=eq.${encodeURIComponent(orderId)}&select=*`);
            if (rows.length === 0) {
                return errorResponse(404, 'ORDER_NOT_FOUND', 'Order not found');
            }
            const order = rows[0];
            const finalStatus = fraudStatus === 'challenge' ? 'pending' :
                transactionStatus === 'settlement' || transactionStatus === 'capture' ? 'paid' :
                    transactionStatus === 'cancel' || transactionStatus === 'deny' || transactionStatus === 'expire' ? 'cancelled' :
                        order.status;
            await supabaseRequest('orders', {
                method: 'PATCH',
                headers: { Prefer: 'return=representation' },
                body: JSON.stringify({ status: finalStatus }),
            });
            return send(200, { status: finalStatus });
        }
        return send(200, { status: 'ok' });
    }
    return errorResponse(404, 'NOT_FOUND', 'Route not found');
}
// Export route function for use in local server
export { route };
//# sourceMappingURL=server.js.map