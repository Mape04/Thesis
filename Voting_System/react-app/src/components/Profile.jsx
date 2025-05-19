import React, { useContext, useEffect, useState } from 'react';
import { VoterContext } from '../context/VoterContext';
import { useNavigate } from 'react-router-dom';
import '../styles/Profile.css';
import Navbar from './Navbar';

function Profile() {
    const { voterId, logout } = useContext(VoterContext);
    const [voter, setVoter] = useState(null);
    const [voterName, setVoterName] = useState('');
    const [voterEmail, setVoterEmail] = useState('');
    const [selectedImage, setSelectedImage] = useState(null);
    const [previewUrl, setPreviewUrl] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        if (!voterId) {
            navigate('/');
            return;
        }

        const fetchVoter = async () => {
            try {
                const res = await fetch(`http://localhost:8080/api/voters/${voterId}`);
                const data = await res.json();
                setVoter(data);
                setVoterName(data.voterName);
                setVoterEmail(data.voterEmail);
                setPreviewUrl(`http://localhost:8080/api/voters/${voterId}/image`);
            } catch (err) {
                console.error('Failed to fetch voter:', err);
            }
        };

        fetchVoter();
    }, [voterId, navigate]);

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        setSelectedImage(file);
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => setPreviewUrl(reader.result);
            reader.readAsDataURL(file);
        }
    };

    const handleSave = async () => {
        const formData = new FormData();
        formData.append('voterName', voterName);
        formData.append('voterEmail', voterEmail);
        if (selectedImage) {
            formData.append('file', selectedImage); // 👈 image
        }

        try {
            const res = await fetch(`http://localhost:8080/api/voters/${voterId}`, {
                method: 'PUT',
                body: formData
            });

            if (!res.ok) {
                const err = await res.json();
                alert(`❌ Update failed: ${err.message || 'Unknown error'}`);
                return;
            }

            alert('✅ Profile updated successfully!');
        } catch (err) {
            console.error('Error updating profile:', err);
            alert('Something went wrong.');
        }
    };

    const handleDelete = async () => {
        const confirmed = window.confirm('Are you sure you want to delete your profile?');
        if (!confirmed) return;

        try {
            const res = await fetch(`http://localhost:8080/api/voters/${voterId}`, {
                method: 'DELETE'
            });

            if (!res.ok) {
                alert('❌ Failed to delete profile.');
                return;
            }

            alert('✅ Profile deleted.');
            logout();
            navigate('/');
        } catch (err) {
            console.error('Delete failed:', err);
            alert('Something went wrong.');
        }
    };

    return (
        <>
            <Navbar />
            <div className="profile-wrapper">
                <div className="profile-header">
                    <img
                        src={previewUrl || '/default-user.png'}
                        alt="Profile"
                        className="profile-avatar"
                    />
                    <h2 className="profile-title">Your Profile</h2>
                </div>

                <div className="profile-form-group">
                    <label htmlFor="voterName">Name</label>
                    <input
                        id="voterName"
                        type="text"
                        value={voterName}
                        onChange={(e) => setVoterName(e.target.value)}
                    />
                </div>

                <div className="profile-form-group">
                    <label htmlFor="voterEmail">Email</label>
                    <input
                        id="voterEmail"
                        type="email"
                        value={voterEmail}
                        onChange={(e) => setVoterEmail(e.target.value)}
                    />
                </div>

                <div className="profile-form-group">
                    <label htmlFor="profileImage">Profile Image</label>
                    <input
                        id="profileImage"
                        type="file"
                        accept="image/*"
                        onChange={handleImageChange}
                    />
                </div>

                <div className="profile-buttons">
                    <button className="profile-button save" onClick={handleSave}>Save</button>
                    <button className="profile-button delete" onClick={handleDelete}>Delete</button>
                </div>
            </div>
        </>
    );
}

export default Profile;
