// src/pages/LandingPage.tsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Header } from '@/components/Header';
import { Footer } from '@/components/Footer';
import {
  MonitorCheck,
  BellRing,
  Clock,
  ShieldCheck,
  Mail,
  Webhook,
} from 'lucide-react'; // Import Lucide icons

/**
 * A landing page component for unauthenticated users.
 * It features a hero section with an app description and call-to-action buttons,
 * and a new features section.
 */
export const LandingPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="flex flex-col min-h-screen bg-gray-50 dark:bg-gray-950 font-sans antialiased">
      <Header />
      <main className="flex-1">
        {/* Hero Section */}
        <section className="relative isolate px-6 pt-14 lg:px-8 bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-800 dark:to-gray-900 py-24 sm:py-32 lg:py-40">
          <div className="mx-auto max-w-3xl text-center">
            <h1 className="text-5xl font-extrabold tracking-tight text-gray-900 dark:text-gray-50 sm:text-7xl leading-tight">
              Reliable Uptime Monitoring for Your Services
            </h1>
            <p className="mt-8 text-xl leading-8 text-gray-700 dark:text-gray-300 max-w-2xl mx-auto">
              Watchdog provides instant alerts and detailed insights, ensuring your websites and applications are always up and running.
            </p>
            <div className="mt-12 flex items-center justify-center gap-x-6">
              <Button
                onClick={() => navigate('/register')}
                className="px-8 py-3 text-lg font-semibold rounded-full shadow-lg transition-all duration-300 ease-in-out transform hover:scale-105 bg-blue-600 hover:bg-blue-700 text-white"
              >
                Get Started Free
              </Button>
              <Button
                onClick={() => navigate('/login')}
                variant="outline"
                className="px-8 py-3 text-lg font-semibold rounded-full shadow-md transition-all duration-300 ease-in-out transform hover:scale-105 border-blue-600 text-blue-600 hover:bg-blue-50 dark:border-blue-400 dark:text-blue-400 dark:hover:bg-gray-700"
              >
                Log In
              </Button>
            </div>
          </div>
        </section>

        {/* Features Section */}
        <section className="py-24 sm:py-32 bg-white dark:bg-gray-950">
          <div className="mx-auto max-w-7xl px-6 lg:px-8">
            <div className="mx-auto max-w-2xl lg:text-center">
              <h2 className="text-base font-semibold leading-7 text-blue-600 dark:text-blue-400">Monitor Smarter</h2>
              <p className="mt-2 text-3xl font-bold tracking-tight text-gray-900 dark:text-gray-50 sm:text-4xl">
                Everything you need to keep an eye on your digital presence.
              </p>
              <p className="mt-6 text-lg leading-8 text-gray-600 dark:text-gray-400">
                From basic uptime checks to advanced content validation and flexible alerting, Watchdog has you covered.
              </p>
            </div>
            <div className="mx-auto mt-16 max-w-2xl sm:mt-20 lg:mt-24 lg:max-w-4xl">
              <dl className="grid max-w-xl grid-cols-1 gap-x-8 gap-y-10 lg:max-w-none lg:grid-cols-2 lg:gap-y-16">
                <div className="relative pl-16">
                  <dt className="text-base font-semibold leading-7 text-gray-900 dark:text-gray-50">
                    <div className="absolute left-0 top-0 flex h-10 w-10 items-center justify-center rounded-lg bg-blue-600">
                      <MonitorCheck className="h-6 w-6 text-white" aria-hidden="true" />
                    </div>
                    Uptime Monitoring
                  </dt>
                  <dd className="mt-2 text-base leading-7 text-gray-600 dark:text-gray-400">
                    Continuously check the availability of your websites, APIs, and services from multiple locations.
                  </dd>
                </div>
                <div className="relative pl-16">
                  <dt className="text-base font-semibold leading-7 text-gray-900 dark:text-gray-50">
                    <div className="absolute left-0 top-0 flex h-10 w-10 items-center justify-center rounded-lg bg-blue-600">
                      <BellRing className="h-6 w-6 text-white" aria-hidden="true" />
                    </div>
                    Instant Alerts
                  </dt>
                  <dd className="mt-2 text-base leading-7 text-gray-600 dark:text-gray-400">
                    Get notified immediately via email or webhooks the moment an issue is detected.
                  </dd>
                </div>
                <div className="relative pl-16">
                  <dt className="text-base font-semibold leading-7 text-gray-900 dark:text-gray-50">
                    <div className="absolute left-0 top-0 flex h-10 w-10 items-center justify-center rounded-lg bg-blue-600">
                      <Clock className="h-6 w-6 text-white" aria-hidden="true" />
                    </div>
                    Customizable Intervals
                  </dt>
                  <dd className="mt-2 text-base leading-7 text-gray-600 dark:text-gray-400">
                    Set monitoring frequencies that match your needs, from every 10 seconds to several minutes.
                  </dd>
                </div>
                <div className="relative pl-16">
                  <dt className="text-base font-semibold leading-7 text-gray-900 dark:text-gray-50">
                    <div className="absolute left-0 top-0 flex h-10 w-10 items-center justify-center rounded-lg bg-blue-600">
                      <ShieldCheck className="h-6 w-6 text-white" aria-hidden="true" />
                    </div>
                    HTTP/S, Ping, & Port Checks
                  </dt>
                  <dd className="mt-2 text-base leading-7 text-gray-600 dark:text-gray-400">
                    Monitor various types of services, including web servers, databases, and custom ports.
                  </dd>
                </div>
                <div className="relative pl-16">
                  <dt className="text-base font-semibold leading-7 text-gray-900 dark:text-gray-50">
                    <div className="absolute left-0 top-0 flex h-10 w-10 items-center justify-center rounded-lg bg-blue-600">
                      <Mail className="h-6 w-6 text-white" aria-hidden="true" />
                    </div>
                    Email Notifications
                  </dt>
                  <dd className="mt-2 text-base leading-7 text-gray-600 dark:text-gray-400">
                    Receive detailed outage and recovery notifications directly to your inbox.
                  </dd>
                </div>
                <div className="relative pl-16">
                  <dt className="text-base font-semibold leading-7 text-gray-900 dark:text-gray-50">
                    <div className="absolute left-0 top-0 flex h-10 w-10 items-center justify-center rounded-lg bg-blue-600">
                      <Webhook className="h-6 w-6 text-white" aria-hidden="true" />
                    </div>
                    Webhook Integration
                  </dt>
                  <dd className="mt-2 text-base leading-7 text-gray-600 dark:text-gray-400">
                    Connect Watchdog alerts to your custom systems, Slack, Discord, or other tools.
                  </dd>
                </div>
              </dl>
            </div>
          </div>
        </section>

      </main>
      <Footer />
    </div>
  );
};
