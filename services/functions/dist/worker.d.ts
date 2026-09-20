interface Env {
    SUPABASE_URL: string;
    SUPABASE_SERVICE_ROLE_KEY: string;
    MIDTRANS_SERVER_KEY: string;
    MIDTRANS_BASE_URL: string;
    NEXOVA_ENVIRONMENT: string;
    NEXOVA_STORAGE_MODE: string;
}
declare const _default: {
    fetch(request: Request, env: Env): Promise<Response>;
};
export default _default;
