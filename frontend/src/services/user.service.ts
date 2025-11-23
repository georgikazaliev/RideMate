import api from '../api/axios';
import type { User } from '../types';

export const getProfile = async (): Promise<User> => {
    const response = await api.get<User>('/users/me');
    return response.data;
};

export const updateProfile = async (userData: Partial<User>): Promise<User> => {
    const response = await api.put<User>('/users/me', userData);
    return response.data;
};
