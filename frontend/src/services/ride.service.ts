import api from '../api/axios';
import type { RideDto, RideResponse } from '../types';

export const getAllRides = async (): Promise<RideResponse[]> => {
    const response = await api.get<RideResponse[]>('/rides/');
    return response.data;
};

export const getRide = async (id: string): Promise<RideResponse> => {
    const response = await api.get<RideResponse>(`/rides/${id}`);
    return response.data;
};

export const createRide = async (rideDto: RideDto): Promise<RideResponse> => {
    const response = await api.post<RideResponse>('/rides/', rideDto);
    return response.data;
};

export const bookRide = async (id: string): Promise<any> => {
    const response = await api.post(`/rides/${id}/book`);
    return response.data;
};

export const updateRide = async (id: string, rideDto: RideDto): Promise<RideResponse> => {
    const response = await api.put<RideResponse>(`/rides/${id}`, rideDto);
    return response.data;
};
