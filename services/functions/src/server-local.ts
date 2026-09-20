import { createServer, type IncomingMessage, type ServerResponse } from 'node:http';
import { randomUUID } from 'node:crypto';
import { route } from './server.js';

const port = Number(process.env.PORT ?? 8787);

const server = createServer(async (req, res) => {
  try {
    let body: BodyInit | undefined;
    if (req.method === 'POST' || req.method === 'PUT' || req.method === 'PATCH') {
      const chunks: Buffer[] = [];
      for await (const chunk of req) {
        chunks.push(chunk as Buffer);
      }
      body = Buffer.concat(chunks);
    }
    
    const response = await route(new Request(req.url || '/', {
      method: req.method,
      headers: new Headers(Object.entries(req.headers as Record<string, string>)),
      body,
    }));
    
    res.writeHead(response.status, Object.fromEntries(response.headers.entries()));
    res.end(await response.text());
  } catch (error: unknown) {
    res.writeHead(400, { 'content-type': 'application/json' });
    res.end(JSON.stringify({ error: error instanceof Error ? error.message : 'Invalid request' }));
  }
});

server.listen(port, () => console.log(`NEXOVA API listening on http://localhost:${port}`));