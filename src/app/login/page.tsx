'use client';

import type { ReactElement } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useForm, type SubmitHandler } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';

import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { useToast } from '@/hooks/use-toast';
import { loginSchema, type LoginInput, type Role } from '@/lib/schemas';
// Note: Assuming loginUser is an action/function that handles the actual backend call.
// You would replace the mock implementation with your actual API call.
// import { loginUser } from '@/actions/auth';

// Mock login function (replace with actual API call)
async function loginUser(data: LoginInput): Promise<{ success: boolean; message: string, role?: Role, token?: string }> {
  console.log('Logging in user:', data);
  // Simulate API call
  await new Promise(resolve => setTimeout(resolve, 1000));

  // Simulate success/failure and role-based redirection
  if (data.email === 'buyer@example.com' && data.password === 'password') {
    return { success: true, message: 'Login successful!', role: 'buyer', token: 'fake-jwt-token-buyer' };
  } else if (data.email === 'supplier@example.com' && data.password === 'password') {
     return { success: true, message: 'Login successful!', role: 'supplier', token: 'fake-jwt-token-supplier' };
  } else if (data.email === 'transporter@example.com' && data.password === 'password') {
     return { success: true, message: 'Login successful!', role: 'transporter', token: 'fake-jwt-token-transporter' };
  } else if (data.email === 'driver@example.com' && data.password === 'password') {
     return { success: true, message: 'Login successful!', role: 'driver', token: 'fake-jwt-token-driver' };
  } else {
    return { success: false, message: 'Login failed. Invalid credentials.' };
  }
}

export default function LoginPage(): ReactElement {
  const { toast } = useToast();
  const router = useRouter();

  const form = useForm<LoginInput>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: '',
      password: '',
    },
    mode: 'onChange',
  });

  const onSubmit: SubmitHandler<LoginInput> = async (data) => {
    try {
      const result = await loginUser(data);
      if (result.success && result.role) {
        toast({
          title: 'Login Successful',
          description: result.message,
        });
        // Store token (e.g., in localStorage or context) - For demo, just logging
        console.log("Received token:", result.token);
        // Redirect to role-specific dashboard
        router.push(`/${result.role}-dashboard`);
      } else {
        toast({
          title: 'Login Failed',
          description: result.message,
          variant: 'destructive',
        });
      }
    } catch (error) {
      console.error('Login error:', error);
      toast({
        title: 'Login Error',
        description: 'An unexpected error occurred. Please try again.',
        variant: 'destructive',
      });
    }
  };

  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-4 sm:p-8 md:p-12 lg:p-24 bg-background">
      <Card className="w-full max-w-md shadow-lg">
        <CardHeader>
          <CardTitle className="text-2xl font-bold text-primary">Login</CardTitle>
          <CardDescription>
            Access your RoleConnector account.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="your.email@example.com"
                        type="email"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Password</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="********"
                        type="password"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <Button
                type="submit"
                className="w-full"
                disabled={form.formState.isSubmitting}
              >
                {form.formState.isSubmitting ? 'Logging in...' : 'Login'}
              </Button>
            </form>
          </Form>
        </CardContent>
        <CardFooter className="flex justify-center">
          <p className="text-sm text-muted-foreground">
            Don't have an account?{' '}
             <Button variant="link" className="p-0 h-auto" asChild>
              <Link href="/register" className="text-primary hover:underline">
                  Register here
              </Link>
            </Button>
          </p>
        </CardFooter>
      </Card>
    </main>
  );
}
