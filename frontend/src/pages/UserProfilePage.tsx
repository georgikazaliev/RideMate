import { useEffect, useState } from 'react';
import { getProfile, updateProfile } from '../services/user.service';
import type { User } from '../types';
import { useAuth } from '../context/AuthContext';

const UserProfilePage = () => {
    const [profile, setProfile] = useState<User | null>(null);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({
        username: '',
        email: ''
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const { user } = useAuth();

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const data = await getProfile();
                setProfile(data);
                setFormData({
                    username: data.username,
                    email: data.email
                });
            } catch (err) {
                setError('Failed to load profile');
            } finally {
                setLoading(false);
            }
        };

        if (user) {
            fetchProfile();
        }
    }, [user]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        try {
            const updatedProfile = await updateProfile(formData);
            setProfile(updatedProfile);
            setIsEditing(false);
            setSuccess('Profile updated successfully');
        } catch (err) {
            setError('Failed to update profile');
        }
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center min-h-[50vh]">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-500"></div>
            </div>
        );
    }

    if (!profile) {
        return (
            <div className="text-center py-12 text-red-500">
                Failed to load profile. Please try logging in again.
            </div>
        );
    }

    return (
        <div className="max-w-2xl mx-auto">
            <div className="bg-gray-800/50 backdrop-blur-md rounded-2xl shadow-2xl border border-gray-700 p-8">
                <div className="flex justify-between items-center mb-8">
                    <h1 className="text-3xl font-bold text-white">My Profile</h1>
                    {!isEditing && (
                        <button
                            onClick={() => setIsEditing(true)}
                            className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-medium rounded-lg transition-colors"
                        >
                            Edit Profile
                        </button>
                    )}
                </div>

                {error && (
                    <div className="bg-red-500/10 border border-red-500/50 text-red-500 px-4 py-3 rounded-lg mb-6 text-sm">
                        {error}
                    </div>
                )}

                {success && (
                    <div className="bg-green-500/10 border border-green-500/50 text-green-500 px-4 py-3 rounded-lg mb-6 text-sm">
                        {success}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-6">
                    <div className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Username</label>
                            <input
                                type="text"
                                name="username"
                                value={isEditing ? formData.username : profile.username}
                                onChange={handleChange}
                                disabled={!isEditing}
                                className={`w-full px-4 py-3 bg-gray-900/50 border rounded-lg text-white transition-all ${isEditing
                                    ? 'border-gray-600 focus:ring-2 focus:ring-indigo-500 focus:border-transparent'
                                    : 'border-transparent bg-transparent px-0'
                                    }`}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Email</label>
                            <input
                                type="email"
                                name="email"
                                value={isEditing ? formData.email : profile.email}
                                onChange={handleChange}
                                disabled={!isEditing}
                                className={`w-full px-4 py-3 bg-gray-900/50 border rounded-lg text-white transition-all ${isEditing
                                    ? 'border-gray-600 focus:ring-2 focus:ring-indigo-500 focus:border-transparent'
                                    : 'border-transparent bg-transparent px-0'
                                    }`}
                            />
                        </div>
                    </div>

                    {isEditing && (
                        <div className="flex space-x-4 pt-4">
                            <button
                                type="submit"
                                className="flex-1 py-3 px-6 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-xl shadow-lg shadow-indigo-500/30 transition-all"
                            >
                                Save Changes
                            </button>
                            <button
                                type="button"
                                onClick={() => {
                                    setIsEditing(false);
                                    setFormData({
                                        username: profile.username,
                                        email: profile.email
                                    });
                                    setError('');
                                }}
                                className="flex-1 py-3 px-6 bg-gray-700 hover:bg-gray-600 text-white font-bold rounded-xl transition-all"
                            >
                                Cancel
                            </button>
                        </div>
                    )}
                </form>
            </div>

            <div className="mt-8 bg-gray-800/50 backdrop-blur-md rounded-2xl shadow-2xl border border-gray-700 p-8">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-2xl font-bold text-white">My Activity</h2>
                    <button
                        onClick={async () => {
                            if (window.confirm('Are you sure you want to clear your history?')) {
                                try {
                                    const response = await fetch('http://localhost:8080/api/v1/users/my-activity', {
                                        method: 'DELETE',
                                        headers: { 'Authorization': `Bearer ${user?.token}` }
                                    });
                                    if (response.ok) {
                                        alert('History cleared');
                                        window.location.reload();
                                    }
                                } catch (e) {
                                    alert('Failed to clear history');
                                }
                            }
                        }}
                        className="px-3 py-1 bg-red-500/20 hover:bg-red-500/30 text-red-400 text-sm font-medium rounded transition-colors"
                    >
                        Clear History
                    </button>
                </div>
                <ActivityList token={user?.token || null} />
            </div>
        </div>
    );
};

const ActivityList = ({ token }: { token: string | null }) => {
    const [activities, setActivities] = useState<any[]>([]);

    useEffect(() => {
        const fetchActivity = async () => {
            if (!token) return;
            try {
                const response = await fetch('http://localhost:8080/api/v1/users/my-activity', {
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                if (response.ok) {
                    const data = await response.json();
                    setActivities(data);
                }
            } catch (e) {
                console.error(e);
            }
        };
        fetchActivity();
    }, [token]);

    if (activities.length === 0) return <div className="text-gray-400">No recent activity</div>;

    return (
        <div className="space-y-4">
            {activities.map((activity) => (
                <div key={activity.id} className="bg-gray-900/50 p-4 rounded-lg border border-gray-700">
                    <div className="flex justify-between items-start">
                        <div>
                            <span className={`inline-block px-2 py-0.5 rounded text-xs font-bold mb-2 ${activity.actionType === 'CREATE' ? 'bg-green-500/20 text-green-400' :
                                activity.actionType === 'DELETE' ? 'bg-red-500/20 text-red-400' :
                                    'bg-blue-500/20 text-blue-400'
                                }`}>
                                {activity.actionType}
                            </span>
                            <p className="text-gray-300 text-sm">{activity.description}</p>
                        </div>
                        <span className="text-xs text-gray-500">
                            {new Date(activity.timestamp).toLocaleString()}
                        </span>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default UserProfilePage;
