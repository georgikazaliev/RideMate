import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const HomePage = () => {
    const { user } = useAuth();

    return (
        <div className="space-y-20">
            <section className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-indigo-900 via-purple-900 to-gray-900 shadow-2xl">
                <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?ixlib=rb-4.0.3&auto=format&fit=crop&w=2070&q=80')] bg-cover bg-center opacity-20"></div>
                <div className="relative px-6 py-24 sm:px-12 sm:py-32 lg:py-40 text-center">
                    <h1 className="text-4xl font-extrabold tracking-tight text-white sm:text-5xl lg:text-6xl mb-6">
                        Share the Ride, <span className="text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 to-pink-400">Split the Cost</span>
                    </h1>
                    <p className="mx-auto max-w-2xl text-lg text-gray-300 mb-10">
                        Connect with travelers heading your way. Save money, reduce your carbon footprint, and make new friends on the road.
                    </p>
                    <div className="flex justify-center gap-4">
                        {user ? (
                            <Link
                                to="/rides"
                                className="px-8 py-4 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-bold text-lg transition-all transform hover:scale-105 shadow-lg shadow-indigo-500/50"
                            >
                                Find a Ride
                            </Link>
                        ) : (
                            <Link
                                to="/register"
                                className="px-8 py-4 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-bold text-lg transition-all transform hover:scale-105 shadow-lg shadow-indigo-500/50"
                            >
                                Get Started
                            </Link>
                        )}
                        <Link
                            to="/about"
                            className="px-8 py-4 bg-gray-800 hover:bg-gray-700 text-white rounded-xl font-bold text-lg transition-all border border-gray-700 hover:border-gray-600"
                        >
                            Learn More
                        </Link>
                    </div>
                </div>
            </section>

            <section className="py-12">
                <div className="text-center mb-16">
                    <h2 className="text-3xl font-bold text-white mb-4">Why Choose RideMate?</h2>
                    <p className="text-gray-400">Experience the best way to travel together.</p>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                    {[
                        {
                            title: 'Cost Effective',
                            description: 'Save significantly on travel costs by sharing expenses with fellow travelers.',
                            icon: (
                                <svg className="w-8 h-8 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                            )
                        },
                        {
                            title: 'Safe & Secure',
                            description: 'Verified profiles and secure payment handling for your peace of mind.',
                            icon: (
                                <svg className="w-8 h-8 text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                                </svg>
                            )
                        },
                        {
                            title: 'Eco Friendly',
                            description: 'Reduce carbon emissions by sharing rides and taking cars off the road.',
                            icon: (
                                <svg className="w-8 h-8 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                            )
                        }
                    ].map((feature, index) => (
                        <div key={index} className="bg-gray-800/50 backdrop-blur-sm p-8 rounded-2xl border border-gray-700 hover:border-indigo-500/50 transition-colors">
                            <div className="bg-gray-900/50 w-16 h-16 rounded-xl flex items-center justify-center mb-6 shadow-inner">
                                {feature.icon}
                            </div>
                            <h3 className="text-xl font-bold text-white mb-3">{feature.title}</h3>
                            <p className="text-gray-400 leading-relaxed">{feature.description}</p>
                        </div>
                    ))}
                </div>
            </section>

            <section className="relative overflow-hidden rounded-3xl bg-indigo-600 p-12 text-center">
                <div className="relative z-10">
                    <h2 className="text-3xl font-bold text-white mb-6">Ready to start your journey?</h2>
                    <p className="text-indigo-100 mb-8 max-w-2xl mx-auto">
                        Join thousands of users who are already saving money and making connections.
                    </p>
                    <Link
                        to="/register"
                        className="inline-block px-8 py-4 bg-white text-indigo-600 rounded-xl font-bold text-lg transition-transform hover:scale-105 shadow-xl"
                    >
                        Create Free Account
                    </Link>
                </div>
                <div className="absolute top-0 left-0 -translate-x-1/2 -translate-y-1/2 w-64 h-64 bg-indigo-500 rounded-full opacity-50 blur-3xl"></div>
                <div className="absolute bottom-0 right-0 translate-x-1/2 translate-y-1/2 w-64 h-64 bg-purple-500 rounded-full opacity-50 blur-3xl"></div>
            </section>
        </div>
    );
};

export default HomePage;

