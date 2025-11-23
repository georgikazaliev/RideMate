import { Link } from 'react-router-dom';

const NotFoundPage = () => {
    return (
        <div className="min-h-[80vh] flex flex-col justify-center items-center text-center px-4">
            <h1 className="text-9xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-indigo-500 to-purple-600 mb-4">
                404
            </h1>
            <h2 className="text-3xl font-bold text-white mb-6">Page Not Found</h2>
            <p className="text-gray-400 text-lg mb-8 max-w-md">
                The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.
            </p>
            <Link
                to="/"
                className="px-8 py-4 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-xl shadow-lg shadow-indigo-500/30 transition-all transform hover:scale-105"
            >
                Go Home
            </Link>
        </div>
    );
};

export default NotFoundPage;
