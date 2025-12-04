import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import RideListPage from './pages/RideListPage';
import CreateRidePage from './pages/CreateRidePage';
import RideDetailsPage from './pages/RideDetailsPage';
import MyBookingsPage from './pages/MyBookingsPage';
import DriverBookingsPage from './pages/DriverBookingsPage';
import UserProfilePage from './pages/UserProfilePage';
import EditRidePage from './pages/EditRidePage';
import AboutPage from './pages/AboutPage';
import ContactPage from './pages/ContactPage';
import TermsPage from './pages/TermsPage';
import NotFoundPage from './pages/NotFoundPage';
import AdminUsersPage from './pages/AdminUsersPage';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-gray-900 text-white font-sans flex flex-col">
          <Navbar />
          <div className="container mx-auto px-4 py-8 flex-1">
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
              <Route path="/rides" element={<ProtectedRoute allowedRole="USER"><RideListPage /></ProtectedRoute>} />
              <Route path="/create-ride" element={<ProtectedRoute allowedRole="DRIVER"><CreateRidePage /></ProtectedRoute>} />
              <Route path="/rides/:id" element={<RideDetailsPage />} />
              <Route path="/rides/:id/edit" element={<EditRidePage />} />
              <Route path="/my-bookings" element={<ProtectedRoute allowedRole="USER"><MyBookingsPage /></ProtectedRoute>} />
              <Route path="/booking-requests" element={<ProtectedRoute allowedRole="DRIVER"><DriverBookingsPage /></ProtectedRoute>} />
              <Route path="/profile" element={<UserProfilePage />} />
              <Route path="/about" element={<AboutPage />} />
              <Route path="/contact" element={<ContactPage />} />
              <Route path="/terms" element={<TermsPage />} />
              <Route path="/admin/users" element={<ProtectedRoute allowedRole="ADMIN"><AdminUsersPage /></ProtectedRoute>} />
              <Route path="*" element={<NotFoundPage />} />
            </Routes>
          </div>
          <Footer />
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
