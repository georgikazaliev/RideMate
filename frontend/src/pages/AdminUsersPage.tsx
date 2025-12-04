import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface User {
    id: string;
    username: string;
    email: string;
    role: string;
}

const AdminUsersPage: React.FC = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!user || user.role !== 'ADMIN') {
            navigate('/');
            return;
        }
        fetchUsers();
    }, [user, navigate]);

    const fetchUsers = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/v1/users/', {
                headers: {
                    'Authorization': `Bearer ${user?.token}`
                }
            });
            if (!response.ok) throw new Error('Failed to fetch users');
            const data = await response.json();
            setUsers(data);
        } catch (err) {
            setError('Failed to load users');
        } finally {
            setLoading(false);
        }
    };

    const handleRoleChange = async (userId: string, newRole: string) => {
        try {
            const response = await fetch(`http://localhost:8080/api/v1/users/${userId}/role?role=${newRole}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${user?.token}`
                }
            });
            if (!response.ok) throw new Error('Failed to update role');
            fetchUsers(); // Refresh list
        } catch (err) {
            alert('Failed to update role');
        }
    };

    if (loading) return <div className="flex items-center justify-center py-20">Loading...</div>;

    return (
        <>
            <h1 className="text-3xl font-bold mb-8 text-purple-400">Manage Users</h1>
            {error && <div className="bg-red-500/20 text-red-400 p-4 rounded mb-4">{error}</div>}

            <div className="bg-gray-800 rounded-xl overflow-hidden border border-gray-700">
                <table className="w-full text-left">
                    <thead className="bg-gray-700/50">
                        <tr>
                            <th className="p-4">Username</th>
                            <th className="p-4">Email</th>
                            <th className="p-4">Role</th>
                            <th className="p-4">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map(u => (
                            <tr key={u.id} className="border-t border-gray-700 hover:bg-gray-700/30">
                                <td className="p-4">{u.username}</td>
                                <td className="p-4 text-gray-400">{u.email}</td>
                                <td className="p-4">
                                    <span className={`px-2 py-1 rounded text-xs font-bold ${u.role === 'ADMIN' ? 'bg-red-500/20 text-red-400' :
                                        u.role === 'DRIVER' ? 'bg-green-500/20 text-green-400' :
                                            'bg-blue-500/20 text-blue-400'
                                        }`}>
                                        {u.role}
                                    </span>
                                </td>
                                <td className="p-4">
                                    <select
                                        value={u.role}
                                        onChange={(e) => handleRoleChange(u.id, e.target.value)}
                                        className="bg-gray-900 border border-gray-600 rounded px-2 py-1 text-sm focus:border-purple-500 outline-none"
                                    >
                                        <option value="USER">USER</option>
                                        <option value="DRIVER">DRIVER</option>
                                        <option value="ADMIN">ADMIN</option>
                                    </select>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </>
    );
};

export default AdminUsersPage;
