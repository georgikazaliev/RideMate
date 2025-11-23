import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createRide } from '../services/ride.service';
import LocationAutocomplete from '../components/LocationAutocomplete';

const CreateRidePage = () => {
    const [formData, setFormData] = useState({
        origin: '',
        destination: '',
        departureTime: '',
        availableSeats: 1,
        price: 0
    });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleLocationChange = (field: 'origin' | 'destination', value: string) => {
        setFormData({ ...formData, [field]: value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        try {
            await createRide({
                origin: formData.origin,
                destination: formData.destination,
                dateTime: formData.departureTime,
                seatsAvailable: formData.availableSeats,
                price: formData.price
            });
            navigate('/rides');
        } catch (err) {
            setError('Failed to create ride. Please try again.');
        }
    };

    return (
        <div className="max-w-2xl mx-auto">
            <div className="bg-gray-800/50 backdrop-blur-md rounded-2xl shadow-2xl border border-gray-700 p-8">
                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-white mb-2">Offer a Ride</h1>
                    <p className="text-gray-400">Share your journey and save costs</p>
                </div>

                {error && (
                    <div className="bg-red-500/10 border border-red-500/50 text-red-500 px-4 py-3 rounded-lg mb-6 text-sm">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-6">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <LocationAutocomplete
                            label="Origin"
                            value={formData.origin}
                            onChange={(value) => handleLocationChange('origin', value)}
                            placeholder="Search for origin location"
                            required
                        />
                        <LocationAutocomplete
                            label="Destination"
                            value={formData.destination}
                            onChange={(value) => handleLocationChange('destination', value)}
                            placeholder="Search for destination location"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-300 mb-2">Departure Time</label>
                        <input
                            type="datetime-local"
                            name="departureTime"
                            value={formData.departureTime}
                            onChange={handleChange}
                            className="w-full px-4 py-3 bg-gray-900/50 border border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent text-white placeholder-gray-500 transition-all"
                            required
                        />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Available Seats</label>
                            <input
                                type="number"
                                name="availableSeats"
                                min="1"
                                max="8"
                                value={formData.availableSeats}
                                onChange={handleChange}
                                className="w-full px-4 py-3 bg-gray-900/50 border border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent text-white placeholder-gray-500 transition-all"
                                required
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Price per Seat ($)</label>
                            <input
                                type="number"
                                name="price"
                                min="0"
                                step="0.01"
                                value={formData.price}
                                onChange={handleChange}
                                className="w-full px-4 py-3 bg-gray-900/50 border border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent text-white placeholder-gray-500 transition-all"
                                required
                            />
                        </div>
                    </div>

                    <div className="pt-4">
                        <button
                            type="submit"
                            className="w-full py-4 px-6 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-xl shadow-lg shadow-indigo-500/30 transition-all transform hover:scale-[1.02]"
                        >
                            Create Ride
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreateRidePage;
