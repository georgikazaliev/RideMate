import api from '../api/axios';
import type { VehicleDto, VehicleResponse } from '../types';

export const getMyVehicles = async (): Promise<VehicleResponse[]> => {
    const response = await api.get<VehicleResponse[]>('/vehicles/mine');
    return response.data;
};

export const createVehicle = async (vehicleDto: VehicleDto): Promise<VehicleResponse> => {
    const response = await api.post<VehicleResponse>('/vehicles/', vehicleDto);
    return response.data;
};
