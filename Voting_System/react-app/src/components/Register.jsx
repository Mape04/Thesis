import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom"
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import "../styles/Register.css"; // you can reuse this for consistent styling
import "../styles/index.css";

function Registration() {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const [successMsg, setSuccessMsg] = useState("");
    const [confirmPassword, setConfirmPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        setErrorMsg('');
        setSuccessMsg('');

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const strongPasswordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;

        if (!emailRegex.test(email)) {
            return setErrorMsg("Please enter a valid email address.");
        }

        if (!strongPasswordRegex.test(password)) {
            return setErrorMsg("Password must be at least 8 characters, include uppercase, lowercase, and a number.");
        }

        if (password !== confirmPassword) {
            return setErrorMsg("Passwords do not match.");
        }

        // Proceed with API
        try {
            const response = await fetch("http://localhost:8080/api/auth/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    voterName: name,
                    voterEmail: email,
                    voterPassword: password
                })
            });

            const data = await response.json();

            if (!response.ok) {
                setErrorMsg(data.error || "Registration failed.");
            } else {
                setSuccessMsg(data.message);
                setTimeout(() => navigate("/"), 1500);
            }
        } catch (err) {
            setErrorMsg("Server error.");
            console.error(err);
        }
    };


    return (
        <div className="auth-container">
            <div className="auth-left">
            </div>
            <div className="auth-right">
            <form onSubmit={handleRegister} className="auth-form">
                <h2>Register</h2>
                <input
                    type="text"
                    placeholder="Full Name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                />
                <input
                    type="email"
                    placeholder="Email Address"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
                <div className="password-wrapper">
                    <input
                        type={showPassword ? "text" : "password"}
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                    <FontAwesomeIcon
                        icon={showPassword ? faEyeSlash : faEye}
                        className="password-toggle"
                        onClick={() => setShowPassword(!showPassword)}
                    />
                </div>
                <div className="password-wrapper">
                    <input
                        type={showConfirmPassword ? "text" : "password"}
                        placeholder="Confirm Password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                    />
                    <FontAwesomeIcon
                        icon={showConfirmPassword ? faEyeSlash : faEye}
                        className="password-toggle"
                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    />
                </div>


                <button type="submit">Register</button>
                {errorMsg && <p className="error">{errorMsg}</p>}
                {successMsg && <p className="success">{successMsg}</p>}
                <p className="login-link">
                    Have an account already? <Link to="/login">Login here</Link>
                </p>
            </form>
            </div>

        </div>
    );
}

export default Registration;
