import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getAllRides } from '../services/ride.service';
import type { RideResponse } from '../types';

const RideListPage = () => {
    const [rides, setRides] = useState<RideResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchRides = async () => {
            try {
                const data = await getAllRides();
                setRides(data);
            } catch (err) {
                setError('Failed to load rides');
            } finally {
                setLoading(false);
            }
        };

        fetchRides();
    }, []);

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
        <div className="space-y-8">
            <div className="flex justify-between items-center">
                <div>
                    <h1 className="text-3xl font-bold text-white mb-2">Available Rides</h1>
                    <p className="text-gray-400">Find the perfect ride for your journey</p>
                </div>
                <Link
                    to="/create-ride"
                    className="px-6 py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-xl shadow-lg shadow-indigo-500/30 transition-all transform hover:scale-105"
                >
                    Offer a Ride
                </Link>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {rides.map((ride) => (
                    <Link
                        key={ride.id}
                        to={`/rides/${ride.id}`}
                        className="group bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-2xl overflow-hidden hover:border-indigo-500/50 transition-all duration-300 hover:shadow-2xl hover:shadow-indigo-500/10"
                    >
                        <div className="p-6">
                            <div className="flex justify-between items-start mb-4">
                                <div className="space-y-1">
                                    <div className="flex items-center text-indigo-400 font-medium">
                                        <span className="w-2 h-2 bg-indigo-500 rounded-full mr-2"></span>
                                        {ride.origin}
                                    </div>
                                    <div className="h-4 border-l-2 border-gray-700 ml-1 my-1"></div>
                                    <div className="flex items-center text-purple-400 font-medium">
                                        <span className="w-2 h-2 bg-purple-500 rounded-full mr-2"></span>
                                        {ride.destination}
                                    </div>
                                </div>
                                <div className="text-2xl font-bold text-white">
                                    ${ride.price}
                                </div>
                            </div>

                            <div className="flex items-center justify-between text-sm text-gray-400 mt-6 pt-4 border-t border-gray-700">
                                <div className="flex items-center">
                                    <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                    </svg>
                                    {new Date(ride.dateTime).toLocaleDateString()}
                                </div>
                                <div className="flex items-center">
                                    <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                                    </svg>
                                    {new Date(ride.dateTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                </div>
                            </div>

                            <div className="mt-4 flex items-center justify-between">
                                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-indigo-900/50 text-indigo-200 border border-indigo-700/50">
                                    {ride.seatsAvailable - ride.seatsTaken} seats left
                                </span>
                                <span className="text-indigo-400 group-hover:translate-x-1 transition-transform duration-200">
                                    View Details &rarr;
                                </span>
                            </div>
                        </div>
                    </Link>
                ))}
            </div>

            {rides.length === 0 && !loading && (
                <div className="text-center py-20 bg-gray-800/30 rounded-3xl border border-gray-700 border-dashed">
                    <div className="text-gray-500 text-xl mb-4">No rides available at the moment</div>
                    <p className="text-gray-400 mb-8">Be the first to offer a ride!</p>
                    <Link
                        to="/create-ride"
                        className="px-6 py-3 bg-gray-700 hover:bg-gray-600 text-white font-medium rounded-xl transition-colors"
                    >
                        Offer a Ride
                    </Link>
                </div>
            )}
        </div>
    );
};

export default RideListPage;
