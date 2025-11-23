import React from 'react';

const Footer: React.FC = () => {
    return (
        <footer className="bg-gray-800 text-gray-300 py-6 mt-12">
            <div className="container mx-auto px-4 text-center">
                <p className="mb-2">&copy; {new Date().getFullYear()} RideMate. All rights reserved.</p>
                <div className="flex justify-center space-x-4 text-sm">
                    <a href="/about" className="hover:text-white transition-colors">About</a>
                    <a href="/contact" className="hover:text-white transition-colors">Contact</a>
                    <a href="/terms" className="hover:text-white transition-colors">Terms of Service</a>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
