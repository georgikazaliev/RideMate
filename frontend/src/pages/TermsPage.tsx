const TermsPage = () => {
    return (
        <div className="max-w-4xl mx-auto py-12">
            <div className="text-center mb-16">
                <h1 className="text-5xl font-bold text-white mb-6">Terms of Service</h1>
                <p className="text-xl text-gray-400 max-w-2xl mx-auto">
                    Please read these terms carefully before using our platform.
                </p>
            </div>

            <div className="bg-gray-800/50 backdrop-blur-md rounded-2xl p-8 border border-gray-700 space-y-8">
                <div>
                    <h2 className="text-2xl font-bold text-white mb-4">1. Acceptance of Terms</h2>
                    <p className="text-gray-400 leading-relaxed">
                        By accessing and using RideMate, you accept and agree to be bound by the terms and provision of this agreement. In addition, when using these particular services, you shall be subject to any posted guidelines or rules applicable to such services.
                    </p>
                </div>

                <div>
                    <h2 className="text-2xl font-bold text-white mb-4">2. User Conduct</h2>
                    <p className="text-gray-400 leading-relaxed">
                        You agree to use the service only for lawful purposes. You are prohibited from posting on or transmitting through RideMate any material that is unlawful, harmful, threatening, abusive, harassing, defamatory, vulgar, obscene, sexually explicit, profane, hateful, racially, ethnically, or otherwise objectionable.
                    </p>
                </div>

                <div>
                    <h2 className="text-2xl font-bold text-white mb-4">3. Booking and Cancellations</h2>
                    <p className="text-gray-400 leading-relaxed">
                        Cancellations made 24 hours prior to the ride will receive a full refund. Cancellations made within 24 hours of the ride may be subject to a cancellation fee. Drivers reserve the right to cancel rides due to unforeseen circumstances.
                    </p>
                </div>

                <div>
                    <h2 className="text-2xl font-bold text-white mb-4">4. Safety and Trust</h2>
                    <p className="text-gray-400 leading-relaxed">
                        RideMate is a community platform. While we verify users and provide safety features, users are responsible for their own safety. We recommend reviewing profiles and ratings before booking or accepting rides.
                    </p>
                </div>

                <div className="pt-8 border-t border-gray-700">
                    <p className="text-gray-500 text-sm">
                        Last updated: November 24, 2025
                    </p>
                </div>
            </div>
        </div>
    );
};

export default TermsPage;
