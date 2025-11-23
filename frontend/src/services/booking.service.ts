import api from '../api/axios';
import type { BookingResponse } from '../types';

export const getMyBookings = async (): Promise<BookingResponse[]> => {
    const response = await api.get<BookingResponse[]>('/bookings/mine');
    return response.data;
};

export const cancelBooking = async (id: string): Promise<BookingResponse> => {
    const response = await api.put<BookingResponse>(`/bookings/${id}/cancel`);
    return response.data;
};

export const downloadTicket = async (bookingId: string): Promise<Blob> => {
    const response = await api.get(`/pdf/download/${bookingId}`, {
        responseType: 'blob'
    });
    return response.data;
};

export const getBookingRequests = async (): Promise<BookingResponse[]> => {
    const response = await api.get<BookingResponse[]>('/bookings/requests');
    return response.data;
};

export const approveBooking = async (id: string): Promise<BookingResponse> => {
    const response = await api.put<BookingResponse>(`/bookings/${id}/approve`);
    return response.data;
};

export const rejectBooking = async (id: string): Promise<BookingResponse> => {
    const response = await api.put<BookingResponse>(`/bookings/${id}/reject`);
    return response.data;
};
