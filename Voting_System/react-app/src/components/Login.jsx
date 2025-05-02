import React, { useState, useContext } from 'react';
import "../styles/Login.css";
import { VoterContext } from '../context/VoterContext';
import { Link } from "react-router-dom"
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';

function Login() {
    const { setVoterId, setVoterToken } = useContext(VoterContext);
    const [voterEmail, setVoterEmail] = useState('');
    const [voterPassword, setVoterPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);


    const handleLogin = async (e) => {
        e.preventDefault();

        const loginData = {
            voterEmail: voterEmail.trim(),
            voterPassword: voterPassword.trim(),
        };

        try {
            const response = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(loginData),
            });

            const data = await response.json();

            if (response.ok) {
                console.log("Login successful", data);
                setVoterId(data.voterId);
                setVoterToken(data.voterToken);
            } else {
                console.error("Login failed:", data);
                alert(data.error || 'Login failed');
            }
        } catch (error) {
            console.error('Login error:', error);
        }
    };

    return (
        <div className="login-container">
            <form onSubmit={handleLogin}>

                <h1>Login</h1>
                <div>
                    <input
                        type="text"
                        id="voterEmail"
                        value={voterEmail}
                        placeholder= "Email"
                        onChange={(e) => setVoterEmail(e.target.value)}
                    />
                </div>
                <div className="password-wrapper">
                    <input
                        type={showPassword ? "text" : "password"}
                        id="voterPassword"
                        placeholder="Password"
                        value={voterPassword}
                        onChange={(e) => setVoterPassword(e.target.value)}
                    />
                    <FontAwesomeIcon
                        icon={showPassword ? faEyeSlash : faEye}
                        className="password-toggle"
                        onClick={() => setShowPassword(!showPassword)}
                    />
                </div>

                <button type="submit">Login</button>
                <p className="register-link">
                    Don’t have an account? <Link to="/register">Register here</Link>
                </p>
            </form>


        </div>
    );
}

export default Login;
