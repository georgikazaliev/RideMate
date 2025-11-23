import { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getRide, bookRide } from '../services/ride.service';
import type { RideResponse } from '../types';
import { useAuth } from '../context/AuthContext';

const RideDetailsPage = () => {
    const { id } = useParams<{ id: string }>();
    const [ride, setRide] = useState<RideResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [bookingSuccess, setBookingSuccess] = useState(false);
    const { user } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        const fetchRide = async () => {
            if (!id) return;
            try {
                const data = await getRide(id);
                setRide(data);
            } catch (err) {
                setError('Failed to load ride details');
            } finally {
                setLoading(false);
            }
        };

        fetchRide();
    }, [id]);

    const handleBookRide = async () => {
        if (!ride) return;
        if (!user) {
            navigate('/login');
            return;
        }

        try {
            await bookRide(ride.id);
            setBookingSuccess(true);
            const updatedRide = await getRide(ride.id);
            setRide(updatedRide);
        } catch (err) {
            setError('Failed to book ride. Please try again.');
        }
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center min-h-[50vh]">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-500"></div>
            </div>
        );
    }

    if (error || !ride) {
        return (
            <div className="text-center py-12">
                <div className="text-red-500 text-xl mb-4">{error || 'Ride not found'}</div>
                <Link to="/rides" className="text-indigo-400 hover:text-indigo-300">
                    &larr; Back to Rides
                </Link>
            </div>
        );
    }

    return (
        <div className="max-w-4xl mx-auto">
            <div className="mb-6">
                <Link to="/rides" className="text-gray-400 hover:text-white transition-colors flex items-center">
                    <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                    </svg>
                    Back to Rides
                </Link>
            </div>

            <div className="bg-gray-800/50 backdrop-blur-md rounded-2xl shadow-2xl border border-gray-700 overflow-hidden">
                <div className="p-8">
                    <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8">
                        <div>
                            <h1 className="text-3xl font-bold text-white mb-2">Ride Details</h1>
                            <div className="flex items-center space-x-4 text-gray-400">
                                <span className="flex items-center">
                                    <svg className="w-5 h-5 mr-2 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                    </svg>
                                    {new Date(ride.dateTime).toLocaleDateString()}
                                </span>
                                <span className="flex items-center">
                                    <svg className="w-5 h-5 mr-2 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                                    </svg>
                                    {new Date(ride.dateTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                </span>
                            </div>
                        </div>
                        <div className="mt-4 md:mt-0 text-right">
                            <div className="text-4xl font-bold text-white">${ride.price}</div>
                            <div className="text-gray-400">per seat</div>
                        </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-12 mb-12">
                        <div className="space-y-8">
                            <div className="relative pl-8 border-l-2 border-gray-700 space-y-12">
                                <div className="relative">
                                    <span className="absolute -left-[41px] top-0 w-5 h-5 rounded-full bg-indigo-500 border-4 border-gray-900"></span>
                                    <h3 className="text-lg font-semibold text-indigo-400 mb-1">Origin</h3>
                                    <p className="text-2xl text-white font-bold">{ride.origin}</p>
                                </div>
                                <div className="relative">
                                    <span className="absolute -left-[41px] top-0 w-5 h-5 rounded-full bg-purple-500 border-4 border-gray-900"></span>
                                    <h3 className="text-lg font-semibold text-purple-400 mb-1">Destination</h3>
                                    <p className="text-2xl text-white font-bold">{ride.destination}</p>
                                </div>
                            </div>
                        </div>

                        <div className="bg-gray-900/50 rounded-xl p-6 border border-gray-700">
                            <h3 className="text-xl font-bold text-white mb-6">Booking Summary</h3>

                            <div className="flex justify-between items-center mb-4 pb-4 border-b border-gray-700">
                                <span className="text-gray-400">Available Seats</span>
                                <span className="text-white font-bold text-lg">{ride.seatsAvailable}</span>
                            </div>

                            {bookingSuccess ? (
                                <div className="bg-green-500/10 border border-green-500/50 text-green-500 p-4 rounded-lg text-center">
                                    <p className="font-bold mb-2">Booking Successful!</p>
                                    <Link to="/my-bookings" className="text-sm underline hover:text-green-400">
                                        View My Bookings
                                    </Link>
                                </div>
                            ) : (
                                <button
                                    onClick={handleBookRide}
                                    disabled={ride.seatsAvailable === 0}
                                    className={`w-full py-4 px-6 rounded-xl font-bold text-lg transition-all transform hover:scale-[1.02] shadow-lg ${ride.seatsAvailable > 0
                                        ? 'bg-indigo-600 hover:bg-indigo-700 text-white shadow-indigo-500/30'
                                        : 'bg-gray-700 text-gray-400 cursor-not-allowed'
                                        }`}
                                >
                                    {ride.seatsAvailable > 0 ? 'Book This Ride' : 'Sold Out'}
                                </button>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default RideDetailsPage;
