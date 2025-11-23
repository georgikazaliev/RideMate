import { createContext, useState, useEffect, useContext } from 'react';
import type { ReactNode } from 'react';
import { login as loginService, logout as logoutService } from '../services/auth.service';

interface User {
    token: string;
    role: string;
}

interface AuthContextType {
    user: User | null;
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
    loading: boolean;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            try {
                const payload = JSON.parse(atob(token.split('.')[1]));
                setUser({ token, role: payload.role });
            } catch (e) {
                console.error("Failed to decode token", e);
                setUser(null);
            }
        }
        setLoading(false);
    }, []);

    const login = async (email: string, password: string) => {
        const data = await loginService(email, password);
        try {
            const payload = JSON.parse(atob(data.accessToken.split('.')[1]));
            setUser({ token: data.accessToken, role: payload.role });
        } catch (e) {
            console.error("Failed to decode token", e);
        }
    };

    const logout = () => {
        logoutService();
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
