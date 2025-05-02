
import type { NextRequest } from 'next/server';
import { NextResponse } from 'next/server';

// This function can be marked `async` if using `await` inside
export function middleware(request: NextRequest) {
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
  return NextResponse.next();
}

// See "Matching Paths" below to learn more
export const config = {
  // Matcher specifying paths where the middleware should run.
  // Adjust this to cover all relevant pages, including dashboards, login, register.
  matcher: [
     /*
     * Match all request paths except for the ones starting with:
     * - api (API routes)
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico (favicon file)
     */
    '/((?!api|_next/static|_next/image|favicon.ico).*)',
    // Explicitly include dashboards if not covered by the above pattern
    '/buyer-dashboard/:path*',
    '/supplier-dashboard/:path*',
    '/transporter-dashboard/:path*',
    '/driver-dashboard/:path*',
    '/login',
    '/register',
  ],
};
