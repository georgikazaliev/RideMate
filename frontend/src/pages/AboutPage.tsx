const AboutPage = () => {
    return (
        <div className="max-w-4xl mx-auto py-12">
            <div className="text-center mb-16">
                <h1 className="text-5xl font-bold text-white mb-6">About RideMate</h1>
                <p className="text-xl text-gray-400 max-w-2xl mx-auto">
                    Connecting people, reducing carbon footprints, and making travel affordable for everyone.
                </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-12 mb-20">
                <div className="bg-gray-800/50 backdrop-blur-md rounded-2xl p-8 border border-gray-700">
                    <div className="w-12 h-12 bg-indigo-500/20 rounded-xl flex items-center justify-center mb-6">
                        <svg className="w-6 h-6 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                        </svg>
                    </div>
                    <h3 className="text-2xl font-bold text-white mb-4">Our Mission</h3>
                    <p className="text-gray-400 leading-relaxed">
                        To revolutionize intercity travel by creating a trusted community of drivers and passengers. We believe in a future where every empty seat is filled, making transportation more efficient and sustainable.
                    </p>
                </div>

                <div className="bg-gray-800/50 backdrop-blur-md rounded-2xl p-8 border border-gray-700">
                    <div className="w-12 h-12 bg-purple-500/20 rounded-xl flex items-center justify-center mb-6">
                        <svg className="w-6 h-6 text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                        </svg>
                    </div>
                    <h3 className="text-2xl font-bold text-white mb-4">Community First</h3>
                    <p className="text-gray-400 leading-relaxed">
                        Safety and trust are at the core of RideMate. We verify profiles, enable ratings, and provide support to ensure every journey is comfortable and secure for our community members.
                    </p>
                </div>
            </div>

            <div className="bg-gradient-to-r from-indigo-900/50 to-purple-900/50 rounded-3xl p-12 text-center border border-indigo-500/30">
                <h2 className="text-3xl font-bold text-white mb-6">Ready to start your journey?</h2>
                <p className="text-indigo-200 mb-8 max-w-xl mx-auto">
                    Join thousands of users who are already saving money and making new friends on the road.
                </p>
                <div className="flex justify-center space-x-4">
                    <a href="/register" className="px-8 py-3 bg-white text-indigo-900 font-bold rounded-xl hover:bg-gray-100 transition-colors">
                        Sign Up Now
                    </a>
                </div>
            </div>
        </div>
    );
};

export default AboutPage;
