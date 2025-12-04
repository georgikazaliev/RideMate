import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import type { ReactNode } from 'react';

interface ProtectedRouteProps {
    children: ReactNode;
    allowedRole: 'DRIVER' | 'USER' | 'ADMIN';
}

export const ProtectedRoute = ({ children, allowedRole }: ProtectedRouteProps) => {
    const { user } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    if (user.role !== allowedRole) {
        return <Navigate to="/" replace />;
    }

    return <>{children}</>;
};
