import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useState } from 'react';

const Navbar = () => {
    const { user, logout } = useAuth();
    const location = useLocation();
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

    const isActive = (path: string) => location.pathname === path;

    const navLinkClass = (path: string) =>
        `px-3 py-2 rounded-md text-sm font-medium transition-colors duration-200 ${isActive(path)
            ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-500/30'
            : 'text-gray-300 hover:bg-gray-700 hover:text-white'
        }`;

    const mobileNavLinkClass = (path: string) =>
        `block px-3 py-2 rounded-md text-base font-medium ${isActive(path)
            ? 'bg-indigo-600 text-white'
            : 'text-gray-300 hover:bg-gray-700 hover:text-white'
        }`;

    return (
        <nav className="bg-gray-800/50 backdrop-blur-md border-b border-gray-700 sticky top-0 z-50">
            <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex items-center justify-between h-16">
                    <div className="flex items-center">
                        <Link to="/" className="flex-shrink-0">
                            <span className="text-2xl font-bold bg-gradient-to-r from-indigo-400 to-purple-500 text-transparent bg-clip-text">
                                RideMate
                            </span>
                        </Link>
                        <div className="hidden md:block">
                            <div className="ml-10 flex items-baseline space-x-4">
                                <Link to="/" className={navLinkClass('/')}>Home</Link>
                                <Link to="/about" className={navLinkClass('/about')}>About</Link>
                                <Link to="/contact" className={navLinkClass('/contact')}>Contact</Link>
                                {user && user.role === 'USER' && (
                                    <>
                                        <Link to="/rides" className={navLinkClass('/rides')}>Find Rides</Link>
                                        <Link to="/my-bookings" className={navLinkClass('/my-bookings')}>My Bookings</Link>
                                    </>
                                )}
                                {user && user.role === 'DRIVER' && (
                                    <>
                                        <Link to="/create-ride" className={navLinkClass('/create-ride')}>Offer Ride</Link>
                                        <Link to="/booking-requests" className={navLinkClass('/booking-requests')}>Booking Requests</Link>
                                    </>
                                )}
                                {user && user.role === 'ADMIN' && (
                                    <>
                                        <Link to="/admin/users" className={navLinkClass('/admin/users')}>Manage Users</Link>
                                    </>
                                )}
                            </div>
                        </div>
                    </div>
                    <div className="hidden md:block">
                        <div className="ml-4 flex items-center md:ml-6 space-x-4">
                            {user ? (
                                <div className="flex items-center space-x-4">
                                    <Link to="/profile" className={navLinkClass('/profile')}>Profile</Link>
                                    <button
                                        onClick={logout}
                                        className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors duration-200 shadow-lg shadow-red-500/30"
                                    >
                                        Logout
                                    </button>
                                </div>
                            ) : (
                                <div className="flex items-center space-x-4">
                                    <Link to="/login" className="text-gray-300 hover:text-white px-3 py-2 rounded-md text-sm font-medium transition-colors">
                                        Login
                                    </Link>
                                    <Link
                                        to="/register"
                                        className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-all duration-200 shadow-lg shadow-indigo-500/30 hover:shadow-indigo-500/50"
                                    >
                                        Sign Up
                                    </Link>
                                </div>
                            )}
                        </div>
                    </div>
                    <div className="-mr-2 flex md:hidden">
                        <button
                            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                            type="button"
                            className="bg-gray-800 inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-white hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-800 focus:ring-white"
                            aria-controls="mobile-menu"
                            aria-expanded="false"
                        >
                            <span className="sr-only">Open main menu</span>
                            {!isMobileMenuOpen ? (
                                <svg className="block h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 6h16M4 12h16M4 18h16" />
                                </svg>
                            ) : (
                                <svg className="block h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                                </svg>
                            )}
                        </button>
                    </div>
                </div>
            </div>

            {isMobileMenuOpen && (
                <div className="md:hidden" id="mobile-menu">
                    <div className="px-2 pt-2 pb-3 space-y-1 sm:px-3">
                        <Link to="/" className={mobileNavLinkClass('/')}>Home</Link>
                        <Link to="/about" className={mobileNavLinkClass('/about')}>About</Link>
                        <Link to="/contact" className={mobileNavLinkClass('/contact')}>Contact</Link>
                        {user && user.role === 'USER' && (
                            <>
                                <Link to="/rides" className={mobileNavLinkClass('/rides')}>Find Rides</Link>
                                <Link to="/my-bookings" className={mobileNavLinkClass('/my-bookings')}>My Bookings</Link>
                            </>
                        )}
                        {user && user.role === 'DRIVER' && (
                            <>
                                <Link to="/create-ride" className={mobileNavLinkClass('/create-ride')}>Offer Ride</Link>
                                <Link to="/booking-requests" className={mobileNavLinkClass('/booking-requests')}>Booking Requests</Link>
                            </>
                        )}
                        {user && user.role === 'ADMIN' && (
                            <>
                                <Link to="/admin/users" className={mobileNavLinkClass('/admin/users')}>Manage Users</Link>
                            </>
                        )}
                        {user && (
                            <Link to="/profile" className={mobileNavLinkClass('/profile')}>Profile</Link>
                        )}
                    </div>
                    <div className="pt-4 pb-4 border-t border-gray-700">
                        {user ? (
                            <div className="px-5">
                                <button
                                    onClick={logout}
                                    className="w-full bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md text-base font-medium shadow-lg shadow-red-500/30"
                                >
                                    Logout
                                </button>
                            </div>
                        ) : (
                            <div className="px-5 space-y-2">
                                <Link to="/login" className="block w-full text-center text-gray-300 hover:text-white px-3 py-2 rounded-md text-base font-medium">
                                    Login
                                </Link>
                                <Link
                                    to="/register"
                                    className="block w-full text-center bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-md text-base font-medium shadow-lg shadow-indigo-500/30"
                                >
                                    Sign Up
                                </Link>
                            </div>
                        )}
                    </div>
                </div>
            )}
        </nav>
    );
};

export default Navbar;
