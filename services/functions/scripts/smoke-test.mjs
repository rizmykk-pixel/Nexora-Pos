const baseUrl = process.env.API_BASE_URL ?? 'http://127.0.0.1:8787';

async function request(method, path, body, headers = {}) {
  const response = await fetch(`${baseUrl}${path}`, {
    method,
    headers: { 'content-type': 'application/json', ...headers },
    body: body === undefined ? undefined : JSON.stringify(body),
  });
  const text = await response.text();
  let parsed;
  try {
    parsed = JSON.parse(text);
  } catch {
    throw new Error(`${method} ${path} returned non-JSON response: ${text}`);
  }
  return { response, parsed };
}

function expectStatus(name, result, expected) {
  if (result.response.status !== expected) {
    throw new Error(`${name}: expected ${expected}, received ${result.response.status}`);
  }
  console.log(`${name}: ${result.response.status}`);
}

const health = await request('GET', '/api/v1/health');
expectStatus('health', health, 200);
const catalog = await request('GET', '/api/v1/catalog');
expectStatus('catalog', catalog, 200);
const idempotencyKey = `smoke-${Date.now()}`;
const orderPayload = { totalCents: 0 };
const missingKey = await request('POST', '/api/v1/orders', orderPayload);
expectStatus('order-missing-idempotency-key', missingKey, 400);
const created = await request('POST', '/api/v1/orders', orderPayload, { 'idempotency-key': idempotencyKey });
expectStatus('order-create', created, 201);
const repeated = await request('POST', '/api/v1/orders', orderPayload, { 'idempotency-key': idempotencyKey });
expectStatus('order-repeat', repeated, 200);
if (repeated.parsed.id !== created.parsed.id) {
  throw new Error('order-repeat: returned a different order');
}
const conflict = await request('POST', '/api/v1/orders', { totalCents: 1 }, { 'idempotency-key': idempotencyKey });
expectStatus('order-idempotency-conflict', conflict, 409);
const order = await request('GET', `/api/v1/orders/${created.parsed.id}`);
expectStatus('order-read', order, 200);
const missing = await request('GET', '/api/v1/orders/missing');
expectStatus('order-missing', missing, 404);
const push = await request('POST', '/api/v1/sync/push', { operations: [] });
expectStatus('sync-push', push, 200);
const pull = await request('GET', '/api/v1/sync/pull?cursor=0');
expectStatus('sync-pull', pull, 200);
const payment = await request('POST', '/api/v1/payments', { orderId: 'smoke-payment', grossAmount: 1000 });
expectStatus('payment-not-configured', payment, 503);
console.log(`Smoke test passed against ${baseUrl}`);