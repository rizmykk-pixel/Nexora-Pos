import { readFile } from 'node:fs/promises';
import { join } from 'node:path';

const migration = join(process.cwd(), 'supabase', 'migrations', '20260920000000_initial_schema.sql');
const sql = await readFile(migration, 'utf8');
const requiredTables = ['tenants', 'outlets', 'devices', 'memberships', 'products', 'orders', 'order_lines', 'sync_cursors', 'audit_events'];
const missingTables = requiredTables.filter((table) => !new RegExp(`create table if not exists ${table}\\b`, 'i').test(sql));
if (missingTables.length > 0) throw new Error(`Migration is missing tables: ${missingTables.join(', ')}`);
if (!/enable row level security/i.test(sql) || !/create policy/i.test(sql)) throw new Error('Migration is missing RLS definitions');
if (/\\binsert\\s+into\\b|\\bseed(?:s|ed)?\\b|\\bfixture(?:s)?\\b/i.test(sql)) throw new Error('Migration must contain schema only, without seed or fixture data');
console.log(`Migration ready: ${migration}`);
console.log(`Validated ${requiredTables.length} required tables and RLS policy definitions.`);
console.log('PostgreSQL execution remains pending: apply with Supabase CLI or psql using SUPABASE_DB_URL.');