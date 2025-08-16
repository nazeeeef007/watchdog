import type { ReactNode } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Button } from './ui/button';
import { Footer } from './Footer';
import { LogOut, LayoutDashboard, PlusCircle, AlertCircle, View } from 'lucide-react';
import watchdogLogo from '../assets/watchdog.png';

interface MainLayoutProps {
  children: ReactNode;
}

export const MainLayout: React.FC<MainLayoutProps> = ({ children }) => {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <div className="flex flex-col min-h-screen bg-gray-100 font-sans antialiased">
      {/* Header for authenticated users */}
      <header className="sticky top-0 z-50 w-full p-4 bg-white shadow-sm">
        <div className="container flex items-center justify-between mx-auto">
          <Link to="/" className="flex items-center space-x-2">
            <img src={watchdogLogo} alt="Watchdog Logo" className="w-8 h-8" />
            <h1 className="text-2xl font-bold text-gray-800">Watchdog</h1>
          </Link>

          <nav className="flex items-center space-x-6 text-sm font-medium">
            <Button
              variant="ghost"
              onClick={() => navigate('/dashboard')}
              className="w-auto"
            >
              <LayoutDashboard className="w-4 h-4 mr-2" />
              Dashboard
            </Button>
            <Button
              variant="ghost"
              onClick={() => navigate('/create-monitor')}
              className="w-auto"
            >
              <PlusCircle className="w-4 h-4 mr-2" />
              Create Monitor
            </Button>
            

            <Button
              variant="ghost"
              onClick={() => navigate('/monitor-check')}
              className="w-auto"
            >
              <View className="w-4 h-4 mr-2" />
              Check History
            </Button>
          </nav>

          <Button
            variant="destructive"
            onClick={handleLogout}
            className="w-auto"
          >
            <LogOut className="w-4 h-4 mr-2" />
            Logout
          </Button>
        </div>
      </header>

      {/* Main content area */}
      <main className="flex-1 p-4">
        <div className="container mx-auto">{children}</div>
      </main>

      {/* Footer */}
      <Footer />
    </div>
  );
};
