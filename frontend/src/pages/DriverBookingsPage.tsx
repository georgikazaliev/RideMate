import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getBookingRequests, approveBooking, rejectBooking } from '../services/booking.service';
import type { BookingResponse } from '../types';

const DriverBookingsPage = () => {
    const [bookings, setBookings] = useState<BookingResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const fetchBookings = async () => {
        try {
            const requests = await getBookingRequests();
            setBookings(requests);
        } catch (err) {
            setError('Failed to load booking requests');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBookings();
    }, []);

    const handleApprove = async (id: string) => {
        if (!window.confirm('Approve this booking request?')) return;
        try {
            await approveBooking(id);
            setBookings(bookings.map(b =>
                b.id === id ? { ...b, status: 'APPROVED' } : b
            ));
        } catch (err) {
            alert('Failed to approve booking');
        }
    };

    const handleReject = async (id: string) => {
        if (!window.confirm('Reject this booking request?')) return;
        try {
            await rejectBooking(id);
            setBookings(bookings.filter(b => b.id !== id));
        } catch (err) {
            alert('Failed to reject booking');
        }
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center min-h-[50vh]">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-500"></div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="text-center py-12">
                <div className="text-red-500 text-xl mb-4">{error}</div>
                <button
                    onClick={() => window.location.reload()}
                    className="px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-gray-700"
                >
                    Try Again
                </button>
            </div>
        );
    }

    return (
        <div className="max-w-4xl mx-auto">
            <div className="flex justify-between items-center mb-8">
                <h1 className="text-3xl font-bold text-white">Booking Requests</h1>
                <Link to="/rides" className="text-indigo-400 hover:text-indigo-300">
                    My Rides &rarr;
                </Link>
            </div>

            <div className="space-y-6">
                {bookings.map((booking) => (
                    <div
                        key={booking.id}
                        className="bg-gray-800/50 backdrop-blur-md rounded-2xl shadow-xl border border-gray-700 overflow-hidden hover:border-indigo-500/30 transition-all"
                    >
                        <div className="p-6">
                            <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                                <div className="space-y-2 flex-1">
                                    <div className="flex items-center space-x-3 text-sm text-gray-400">
                                        <span className={`px-2 py-1 rounded-full text-xs font-medium ${booking.status === 'APPROVED'
                                                ? 'bg-green-500/10 text-green-400 border border-green-500/20'
                                                : booking.status === 'PENDING'
                                                    ? 'bg-yellow-500/10 text-yellow-400 border border-yellow-500/20'
                                                    : 'bg-red-500/10 text-red-400 border border-red-500/20'
                                            }`}>
                                            {booking.status}
                                        </span>
                                        <span>Request #{booking.id.substring(0, 8)}</span>
                                    </div>

                                    <div className="flex items-center space-x-4">
                                        <div className="flex items-center text-white font-semibold text-lg">
                                            <span className="text-indigo-400 mr-2">{booking.ride.origin}</span>
                                            <svg className="w-5 h-5 text-gray-500 mx-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 8l4 4m0 0l-4 4m4-4H3" />
                                            </svg>
                                            <span className="text-purple-400">{booking.ride.destination}</span>
                                        </div>
                                    </div>

                                    <div className="flex items-center text-gray-400 text-sm">
                                        <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                        </svg>
                                        {new Date(booking.ride.dateTime).toLocaleString()}
                                    </div>

                                    <div className="text-sm text-gray-400">
                                        Passenger: {booking.passenger.username} ({booking.passenger.email})
                                    </div>
                                </div>

                                <div className="flex flex-col items-end space-y-4">
                                    <div className="text-2xl font-bold text-white">
                                        ${booking.ride.price}
                                    </div>
                                    {booking.status === 'PENDING' && (
                                        <div className="flex space-x-3">
                                            <button
                                                onClick={() => handleApprove(booking.id)}
                                                className="px-4 py-2 bg-green-500/10 hover:bg-green-500/20 text-green-400 border border-green-500/20 text-sm font-medium rounded-lg transition-colors"
                                            >
                                                Approve
                                            </button>
                                            <button
                                                onClick={() => handleReject(booking.id)}
                                                className="px-4 py-2 bg-red-500/10 hover:bg-red-500/20 text-red-500 border border-red-500/20 text-sm font-medium rounded-lg transition-colors"
                                            >
                                                Reject
                                            </button>
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                ))}

                {bookings.length === 0 && !loading && (
                    <div className="text-center py-20 bg-gray-800/30 rounded-3xl border border-gray-700 border-dashed">
                        <div className="text-gray-500 text-xl mb-4">No booking requests</div>
                        <p className="text-gray-400 mb-8">You haven't received any booking requests yet.</p>
                        <Link
                            to="/rides"
                            className="px-6 py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-medium rounded-xl transition-colors shadow-lg shadow-indigo-500/30"
                        >
                            View My Rides
                        </Link>
                    </div>
                )}
            </div>
        </div>
    );
};

export default DriverBookingsPage;
