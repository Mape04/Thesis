import React, { useContext } from 'react';
import { VoterContext } from '../context/VoterContext';
import { useNavigate } from 'react-router-dom';
import "../styles/Navbar.css";

function Navbar() {
    const { logout } = useContext(VoterContext);
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    const handleProfileClick = () => {
        navigate('/profile');
    };

    return (
        <nav className="navbar">
            <div className="navbar-left">
                <span className="navbar-title">Electronic Voting System</span>
            </div>
            <div className="navbar-right">
                <button className="nav-button profile-button" onClick={handleProfileClick}>
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
