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

    return (
        <nav className="navbar">
            <div className="navbar-left">
                Electronic Voting System
            </div>
            <div className="navbar-right">
                <button className="logout-button" onClick={handleLogout}>
                    Logout
                </button>
            </div>
        </nav>
    );
}

export default Navbar;
