// src/main.tsx
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter as Router} from 'react-router-dom'; // Import BrowserRouter
import './index.css'; // Your global CSS, likely including Tailwind base styles
import App from './App.tsx';
import { AuthProvider } from './context/AuthContext'; // Import AuthProvider

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    {/* BrowserRouter provides routing context to the entire app */}
    <Router>
      {/* AuthProvider makes authentication context available to all children */}
      <AuthProvider>
        <App />
      </AuthProvider>
    </Router>
  </StrictMode>,
);
