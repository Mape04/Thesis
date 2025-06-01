import React, {useContext, useEffect} from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import { VoterContext } from '../context/VoterContext';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import "../styles/Navbar.css";

import logo from "../assets/securevote.svg";

function Navbar() {
    const { logout } = useContext(VoterContext);
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    const handleProfileClick = () => {
        navigate('/profile', { replace: true });
    };

    const handleBack = () => {
        navigate(-1);
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
