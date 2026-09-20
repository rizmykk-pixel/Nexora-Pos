import { createServer } from 'node:http';
import { route } from './server.js';
const port = Number(process.env.PORT ?? 8787);
const server = createServer(async (req, res) => {
    try {
        let body;
        if (req.method === 'POST' || req.method === 'PUT' || req.method === 'PATCH') {
            const chunks = [];
            for await (const chunk of req) {
                chunks.push(chunk);
            }
            body = Buffer.concat(chunks);
        }
        const response = await route(new Request(req.url || '/', {
            method: req.method,
            headers: new Headers(Object.entries(req.headers)),
            body,
        }));
        res.writeHead(response.status, Object.fromEntries(response.headers.entries()));
        res.end(await response.text());
    }
    catch (error) {
        res.writeHead(400, { 'content-type': 'application/json' });
        res.end(JSON.stringify({ error: error instanceof Error ? error.message : 'Invalid request' }));
    }
});
server.listen(port, () => console.log(`NEXOVA API listening on http://localhost:${port}`));
//# sourceMappingURL=server-local.js.map