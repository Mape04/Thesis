import React from "react";
import { Link } from "react-router-dom";
import { FaVoteYea, FaShieldAlt, FaLock, FaFacebook, FaTwitter, FaGithub } from "react-icons/fa";
import logo from "../assets/react.svg"; // Replace with your actual logo path
import "../styles/LandingPage.css";

export default function LandingPage() {
    return (
        <div className="landing-page">
            <header>
                <div style={{ display: 'flex', alignItems: 'center' }}>
                    <img src={logo} alt="SecureVote logo" />
                    <h1>SecureVote</h1>
                </div>
                <nav>
                    <Link to="/login">
                        <button className="btn-login">Login</button>
                    </Link>
                    <Link to="/register">
                        <button className="btn-register">Register</button>
                    </Link>
                </nav>
            </header>

            <section className="hero">
                <h2>
                    Private. Secure. Verifiable. <span style={{ color: '#22d3ee' }}>Voting Reinvented</span>
                </h2>
                <p>End-to-end encrypted elections. Anonymous ballot casting. Trusted by transparency.</p>
                <Link to="/register">
                    <button>Get Started</button>
                </Link>
            </section>

            <section className="features">
                <div className="feature-box">
                    <FaVoteYea />
                    <h3>Fast and Intuitive</h3>
                    <p>Modern design for easy participation. Vote in seconds. No technical knowledge required.</p>
                </div>
                <div className="feature-box">
                    <FaLock />
                    <h3>Bulletproof Privacy</h3>
                    <p>Blind signature cryptography ensures your vote stays anonymous and cannot be traced.</p>
                </div>
                <div className="feature-box">
                    <FaShieldAlt />
                    <h3>Immutable Integrity</h3>
                    <p>Every ballot is cryptographically verifiable. Tamper-proof and audit-friendly by design.</p>
                </div>
            </section>

            <section className="feedback">
                <h3>What users are saying</h3>
                <blockquote>
                    “SecureVote gave me confidence that my vote remained private while still being counted. Super intuitive!”
                </blockquote>
                <p>— A Verified Voter</p>
            </section>

            <footer>
                <div className="footer-content">
                    <div>
                        <h4>Contact</h4>
                        <p>Email: contact@securevote.app</p>
                        <p>Phone: +1 (555) 123-4567</p>
                    </div>
                    <div className="socials">
                        <a href="https://facebook.com" target="_blank" rel="noopener noreferrer">
                            <FaFacebook />
                        </a>
                        <a href="https://twitter.com" target="_blank" rel="noopener noreferrer">
                            <FaTwitter />
                        </a>
                        <a href="https://github.com" target="_blank" rel="noopener noreferrer">
                            <FaGithub />
                        </a>
                    </div>
                </div>
                <p>&copy; {new Date().getFullYear()} SecureVote. All rights reserved.</p>
            </footer>
        </div>
    );
}
