import type { ReactNode } from 'react';
import Navbar from './Navbar';

interface LayoutProps {
    children: ReactNode;
}

const Layout = ({ children }: LayoutProps) => {
    return (
        <div className="min-h-screen flex flex-col bg-gray-900 text-white font-sans">
            <Navbar />
            <main className="flex-grow container mx-auto px-4 py-8 sm:px-6 lg:px-8">
                {children}
            </main>
            <footer className="bg-gray-800 border-t border-gray-700 py-6 mt-auto">
                <div className="container mx-auto px-4 text-center text-gray-400 text-sm">
                    &copy; {new Date().getFullYear()} RideMate. All rights reserved.
                </div>
            </footer>
        </div>
    );
};

export default Layout;
