export interface User {
    id: string;
    username: string;
    email: string;
    role: string;
}

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
}

export interface RideDto {
    origin: string;
    destination: string;
    dateTime: string;
    price: number;
    seatsAvailable: number;
}

export interface RideResponse extends RideDto {
    id: string;
    seatsTaken: number;
    status: string;
}

export interface BookingDto {
    rideId: string;
}

export interface BookingResponse {
    id: string;
    rideId: string;
    passengerId: string;
    status: string;
    ride: RideResponse;
    passenger: User;
}

export interface VehicleDto {
    make: string;
    model: string;
    seats: number;
    imageUrl?: string;
}

export interface VehicleResponse extends VehicleDto {
    id: string;
}
