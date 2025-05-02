(globalThis.TURBOPACK = globalThis.TURBOPACK || []).push(["chunks/[root of the server]__10ae24bd._.js", {

"[externals]/node:async_hooks [external] (node:async_hooks, cjs)": (function(__turbopack_context__) {

var { g: global, __dirname, m: module, e: exports } = __turbopack_context__;
{
const mod = __turbopack_context__.x("node:async_hooks", () => require("node:async_hooks"));

module.exports = mod;
}}),
"[externals]/node:buffer [external] (node:buffer, cjs)": (function(__turbopack_context__) {

var { g: global, __dirname, m: module, e: exports } = __turbopack_context__;
{
const mod = __turbopack_context__.x("node:buffer", () => require("node:buffer"));

module.exports = mod;
}}),
"[project]/middleware.ts [middleware-edge] (ecmascript)": ((__turbopack_context__) => {
"use strict";

var { g: global, __dirname } = __turbopack_context__;
{
__turbopack_context__.s({
    "config": (()=>config),
    "middleware": (()=>middleware)
});
var __TURBOPACK__imported__module__$5b$project$5d2f$node_modules$2f$next$2f$dist$2f$esm$2f$api$2f$server$2e$js__$5b$middleware$2d$edge$5d$__$28$ecmascript$29$__$3c$module__evaluation$3e$__ = __turbopack_context__.i("[project]/node_modules/next/dist/esm/api/server.js [middleware-edge] (ecmascript) <module evaluation>");
var __TURBOPACK__imported__module__$5b$project$5d2f$node_modules$2f$next$2f$dist$2f$esm$2f$server$2f$web$2f$spec$2d$extension$2f$response$2e$js__$5b$middleware$2d$edge$5d$__$28$ecmascript$29$__ = __turbopack_context__.i("[project]/node_modules/next/dist/esm/server/web/spec-extension/response.js [middleware-edge] (ecmascript)");
;
function middleware(request) {
    // console.log('Middleware triggered for:', request.nextUrl.pathname);
    // --- Placeholder for Authentication & Authorization ---
    // In a real application, you would:
    // 1. Get the session token (from cookies, headers, etc.)
    // const token = request.cookies.get('auth_token')?.value;
    //
    // 2. Verify the token (e.g., using JWT library, call an auth service)
    // const { isValid, userRole } = verifyToken(token); // Replace with your verification logic
    //
    // 3. Check if the user is trying to access a protected route
    // const protectedRoutes = ['/buyer-dashboard', '/supplier-dashboard', '/transporter-dashboard', '/driver-dashboard'];
    // const isProtectedRoute = protectedRoutes.some(route => request.nextUrl.pathname.startsWith(route));
    //
    // 4. Redirect unauthenticated users trying to access protected routes
    // if (isProtectedRoute && !isValid) {
    //   return NextResponse.redirect(new URL('/login', request.url));
    // }
    //
    // 5. Check if the authenticated user has the correct role for the dashboard they are accessing
    // if (isValid && isProtectedRoute) {
    //   const requiredRole = request.nextUrl.pathname.split('/')[1].replace('-dashboard', ''); // e.g., 'buyer'
    //   if (userRole !== requiredRole) {
    //     // Redirect to their own dashboard or an unauthorized page
    //      console.warn(`User with role '${userRole}' tried to access '${request.nextUrl.pathname}'. Redirecting.`);
    //      // Example: Redirect to a generic dashboard or home if roles mismatch significantly
    //      // For simplicity, redirecting to login, but a better UX would be needed.
    //      return NextResponse.redirect(new URL('/login', request.url));
    //   }
    // }
    //
    // 6. Prevent authenticated users from accessing login/register pages (optional)
    // if (isValid && (request.nextUrl.pathname === '/login' || request.nextUrl.pathname === '/register')) {
    //    // Redirect to their dashboard based on userRole
    //    return NextResponse.redirect(new URL(`/${userRole}-dashboard`, request.url));
    // }
    // --- End Placeholder ---
    // Allow the request to proceed if none of the above conditions trigger a redirect
    return __TURBOPACK__imported__module__$5b$project$5d2f$node_modules$2f$next$2f$dist$2f$esm$2f$server$2f$web$2f$spec$2d$extension$2f$response$2e$js__$5b$middleware$2d$edge$5d$__$28$ecmascript$29$__["NextResponse"].next();
}
const config = {
    // Matcher specifying paths where the middleware should run.
    // Adjust this to cover all relevant pages, including dashboards, login, register.
    matcher: [
        /*
     * Match all request paths except for the ones starting with:
     * - api (API routes)
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico (favicon file)
     */ '/((?!api|_next/static|_next/image|favicon.ico).*)',
        // Explicitly include dashboards if not covered by the above pattern
        '/buyer-dashboard/:path*',
        '/supplier-dashboard/:path*',
        '/transporter-dashboard/:path*',
        '/driver-dashboard/:path*',
        '/login',
        '/register'
    ]
};
}}),
}]);

//# sourceMappingURL=%5Broot%20of%20the%20server%5D__10ae24bd._.js.map