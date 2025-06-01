import React, { useContext } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import { VoterContext } from '../context/VoterContext';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import "../styles/Navbar.css";
import logo from "../assets/securevote.svg";

function Navbar() {
    const { logout } = useContext(VoterContext);
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    const handleProfileClick = () => {
        navigate('/profile', { replace: true });
    };

    const handleBack = () => {
        if (window.history.length > 2 && document.referrer.startsWith(window.location.origin)) {
            navigate(-1);
        } else if (location.pathname.startsWith('/election/')) {
            navigate('/elections'); // fallback from details
        } else {
            navigate('/'); // general fallback
        }
    };

    return (
        <nav className="navbar">
            <div className="navbar-left">
                <button className="nav-button back-button" onClick={handleBack}>
                    <FontAwesomeIcon icon={faArrowLeft} style={{ marginRight: '6px' }} />
                </button>
                <div className="navbar-title">
                    <Link to="/">SecureVote</Link>
                </div>
            </div>
            <div className="navbar-right">
                <button
                    className="nav-button profile-button"
                    onClick={handleProfileClick}
                >
                    Profile
                </button>
                <button className="nav-button logout-button" onClick={handleLogout}>
                    Logout
                </button>
            </div>
        </nav>
    );
}

export default Navbar;
