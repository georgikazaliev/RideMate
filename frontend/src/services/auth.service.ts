import api from '../api/axios';
import type { AuthResponse } from '../types';

export const login = async (email: string, password: string): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', { email, password });
    if (response.data.accessToken) {
        localStorage.setItem('token', response.data.accessToken);
        localStorage.setItem('refreshToken', response.data.refreshToken);
    }
    return response.data;
};

export const register = async (username: string, email: string, password: string, role: string): Promise<any> => {
    const response = await api.post('/auth/register', { username, email, password, role });
    return response.data;
};

export const logout = (): void => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
};
