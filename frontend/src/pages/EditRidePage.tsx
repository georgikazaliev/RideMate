import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getRide, updateRide } from '../services/ride.service';
import type { RideDto } from '../types';
import LocationAutocomplete from '../components/LocationAutocomplete';

const EditRidePage = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [formData, setFormData] = useState<RideDto>({
        origin: '',
        destination: '',
        dateTime: '',
        seatsAvailable: 1,
        price: 0
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchRide = async () => {
            try {
                if (!id) return;
                const ride = await getRide(id);
                const date = new Date(ride.dateTime);
                const formattedDate = date.toISOString().slice(0, 16);

                setFormData({
                    origin: ride.origin,
                    destination: ride.destination,
                    dateTime: formattedDate,
                    seatsAvailable: ride.seatsAvailable,
                    price: ride.price
                });
            } catch (err) {
                setError('Failed to load ride details');
            } finally {
                setLoading(false);
            }
        };

        fetchRide();
    }, [id]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: name === 'seatsAvailable' || name === 'price' ? parseFloat(value) : value
        }));
    };

    const handleLocationChange = (field: 'origin' | 'destination', value: string) => {
        setFormData(prev => ({ ...prev, [field]: value }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            if (!id) return;
            await updateRide(id, formData);
            navigate(`/rides/${id}`);
        } catch (err) {
            setError('Failed to update ride');
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
                    onClick={() => navigate('/rides')}
                    className="px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-gray-700"
                >
                    Back to Rides
                </button>
            </div>
        );
    }

    return (
        <div className="max-w-2xl mx-auto">
            <div className="bg-gray-800/50 backdrop-blur-md rounded-2xl shadow-2xl border border-gray-700 p-8">
                <h1 className="text-3xl font-bold text-white mb-8">Edit Ride</h1>

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
                            name="dateTime"
                            value={formData.dateTime}
                            onChange={handleChange}
                            required
                            className="w-full px-4 py-3 bg-gray-900/50 border border-gray-600 rounded-xl text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                        />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Available Seats</label>
                            <input
                                type="number"
                                name="seatsAvailable"
                                value={formData.seatsAvailable}
                                onChange={handleChange}
                                min="1"
                                required
                                className="w-full px-4 py-3 bg-gray-900/50 border border-gray-600 rounded-xl text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Price ($)</label>
                            <input
                                type="number"
                                name="price"
                                value={formData.price}
                                onChange={handleChange}
                                min="0"
                                step="0.01"
                                required
                                className="w-full px-4 py-3 bg-gray-900/50 border border-gray-600 rounded-xl text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                            />
                        </div>
                    </div>

                    <div className="flex space-x-4 pt-6">
                        <button
                            type="submit"
                            className="flex-1 py-3 px-6 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-xl shadow-lg shadow-indigo-500/30 transition-all transform hover:scale-[1.02]"
                        >
                            Update Ride
                        </button>
                        <button
                            type="button"
                            onClick={() => navigate(`/rides/${id}`)}
                            className="flex-1 py-3 px-6 bg-gray-700 hover:bg-gray-600 text-white font-bold rounded-xl transition-all"
                        >
                            Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EditRidePage;
