import React, {useContext, useEffect, useState} from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import { VoterContext } from '../context/VoterContext';
import { Link, useNavigate } from 'react-router-dom';
import logo from '../assets/securevote.svg';
import '../styles/Navbar.css';

function Navbar() {
    const { logout, voterId } = useContext(VoterContext);
    const navigate = useNavigate();

    const [voterInfo, setVoterInfo] = useState({ name: '', image: '' });


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

    useEffect(() => {
        if (voterId) {
            fetch(`http://localhost:8080/api/voters/${voterId}`)
                .then(res => res.json())
                .then(data => {
                    setVoterInfo({
                        name: data.voterName,
                        image: `http://localhost:8080/api/voters/${voterId}/image`
                    });
                })
                .catch(err => {
                    console.error("Failed to load voter info:", err);
                });
        }
    }, [voterId]);


    if (!voterId) {
        // NOT logged in (Landing/Home version)
        return (
            <header className="navbar guest-navbar">
                <div className="navbar-left">
                    <h1 className="navbar-title">SecureVote</h1>
                </div>
                <nav className="navbar-right">
                    <Link to="/login">
                        <button className="nav-button btn-login ">Login</button>
                    </Link>
                    <Link to="/register">
                        <button className="nav-button btn-register">Register</button>
                    </Link>
                </nav>
            </header>
        );
    }

    // LOGGED IN version
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
                {voterId ? (
                    <div className="nav-profile-info" onClick={handleProfileClick}>
                        <img src={voterInfo.image} alt="PFP" className="navbar-avatar" />
                        <span className="navbar-name">{voterInfo.name}</span>
                        <button className="nav-button logout-button" onClick={handleLogout}>
                            Logout
                        </button>
                    </div>
                ) : (
                    <>
                        <Link to="/login"><button className="nav-button btn-login">Login</button></Link>
                        <Link to="/register"><button className="nav-button logout-button">Register</button></Link>
                    </>
                )}
            </div>
        </nav>
    );
}

export default Navbar;
