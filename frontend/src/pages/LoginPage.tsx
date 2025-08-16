import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { useState } from 'react';

import { Button } from '@/components/ui/button';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Alert, AlertDescription } from '@/components/ui/alert'; // Import Alert and AlertDescription
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/components/ui/card'; // Import Card components

// Corrected import path for AuthContext
import { useAuth } from '../context/AuthContext';
// Corrected import path for LoginRequest type
import type { LoginRequest } from '../types/LoginRequest';

const formSchema = z.object({
  email: z.string().email({ message: 'Invalid email address.' }),
  password: z.string().min(1, { message: 'Password is required.' }),
});

type LoginFormValues = z.infer<typeof formSchema>;

// Changed to a named export for consistency
export const LoginPage = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [apiError, setApiError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const form = useForm<LoginFormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: '',
      password: '',
    },
  });

  const onSubmit = async (values: LoginFormValues) => {
    setIsLoading(true);
    setApiError(null);

    try {
      const loginRequest: LoginRequest = {
        email: values.email,
        password: values.password,
      };

      await login(loginRequest);
      navigate('/dashboard');
    } catch (error: any) {
      console.error('Login API error:', error);
      // More robust error handling for API responses
      if (error.message) {
        setApiError(error.message);
      } else {
        setApiError('Login failed. Please check your credentials.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-800 dark:to-gray-900 p-4">
      <Card className="w-full max-w-md p-8 space-y-6 shadow-xl rounded-lg dark:bg-gray-850">
        <CardHeader className="text-center">
          <CardTitle className="text-3xl font-bold text-gray-900 dark:text-gray-50">Welcome Back!</CardTitle>
          <CardDescription className="mt-2 text-md text-gray-600 dark:text-gray-400">
            Log in to your Watchdog account.
          </CardDescription>
        </CardHeader>
        <CardContent>
          {apiError && (
            <Alert variant="destructive" className="mb-4">
              <AlertDescription>{apiError}</AlertDescription>
            </Alert>
          )}

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
                        type="email"
                        placeholder="you@example.com"
                        {...field}
                        disabled={isLoading}
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
                        type="password"
                        placeholder="Enter your password"
                        {...field}
                        disabled={isLoading}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <Button
                type="submit"
                className="w-full px-8 py-3 text-lg font-semibold rounded-full shadow-lg transition-all duration-300 ease-in-out transform hover:scale-105 bg-blue-600 hover:bg-blue-700 text-white"
                disabled={isLoading}
              >
                {isLoading ? 'Logging In...' : 'Login'}
              </Button>
            </form>
          </Form>

          <p className="text-sm text-center text-gray-600 dark:text-gray-400">
            Don't have an account?{' '}
            <Link to="/register" className="font-medium text-blue-600 hover:underline dark:text-blue-400 dark:hover:text-blue-300">
              Register here
            </Link>
          </p>
        </CardContent>
      </Card>
    </div>
  );
};
