import { useNavigate, Link } from 'react-router-dom';
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

import { useAuth } from '../context/AuthContext';
import type { UserRegistrationRequest } from '../types/UserRegistrationRequest';

// 1. Define the Zod Schema for validation
const formSchema = z.object({
  email: z.string().email({ message: 'Invalid email address.' }),
  password: z.string().min(8, { message: 'Password must be at least 8 characters.' }),
  confirmPassword: z.string().min(8, { message: 'Confirm password must be at least 8 characters.' }),
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords don't match.",
  path: ['confirmPassword'],
});

// Define the type for form values based on the schema
type RegisterFormValues = z.infer<typeof formSchema>;

// Use a named export for consistency
export const RegisterPage = () => {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [apiError, setApiError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  // 2. Initialize react-hook-form
  const form = useForm<RegisterFormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: '',
      password: '',
      confirmPassword: '',
    },
  });

  // 3. Handle form submission
  const onSubmit = async (values: RegisterFormValues) => {
    setIsLoading(true);
    setApiError(null);

    try {
      const registrationRequest: UserRegistrationRequest = {
        email: values.email,
        password: values.password,
      };

      await register(registrationRequest);
      navigate('/dashboard');
    } catch (error: any) {
      console.error('Registration API error:', error);
      // More robust error handling for API responses
      if (error.message) {
        setApiError(error.message);
      } else {
        setApiError('Registration failed. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-800 dark:to-gray-900 p-4">
      <Card className="w-full max-w-md p-8 space-y-6 shadow-xl rounded-lg dark:bg-gray-850">
        <CardHeader className="text-center">
          <CardTitle className="text-3xl font-bold text-gray-900 dark:text-gray-50">Register for Watchdog</CardTitle>
          <CardDescription className="mt-2 text-md text-gray-600 dark:text-gray-400">
            Create your account to start monitoring your services.
          </CardDescription>
        </CardHeader>
        <CardContent>
          {apiError && (
            <Alert variant="destructive" className="mb-4">
              <AlertDescription>{apiError}</AlertDescription>
            </Alert>
          )}

          {/* 4. Integrate react-hook-form with shadcn/ui Form components */}
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

              <FormField
                control={form.control}
                name="confirmPassword"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Confirm Password</FormLabel>
                    <FormControl>
                      <Input
                        type="password"
                        placeholder="Confirm your password"
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
                {isLoading ? 'Registering...' : 'Register'}
              </Button>
            </form>
          </Form>

          <p className="text-sm text-center text-gray-600 dark:text-gray-400">
            Already have an account?{' '}
            <Link to="/login" className="font-medium text-blue-600 hover:underline dark:text-blue-400 dark:hover:text-blue-300">
              Login here
            </Link>
          </p>
        </CardContent>
      </Card>
    </div>
  );
};
