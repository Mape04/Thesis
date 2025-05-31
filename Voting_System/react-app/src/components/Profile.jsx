import React, { useContext, useEffect, useState } from 'react';
import { VoterContext } from '../context/VoterContext';
import { useNavigate } from 'react-router-dom';
import Navbar from './Navbar';
import '../styles/Profile.css';

function Profile() {
    const { voterId, logout } = useContext(VoterContext);
    const navigate = useNavigate();

    const [voterData, setVoterData] = useState({
        voterName: '',
        voterEmail: '',
        voterType: '',
        verifiedHuman: false,
    });

    const [cnp, setCnp] = useState('');
    const [region, setRegion] = useState('');
    const [birthdate, setBirthdate] = useState('');

    const [selectedImage, setSelectedImage] = useState(null);
    const [previewUrl, setPreviewUrl] = useState('');
    const [verificationStatus, setVerificationStatus] = useState('');



    // Fetch voter info only if voterId is valid
    useEffect(() => {
        if (!voterId) {
            navigate('/');
            return;
        }

        const fetchVoter = async () => {
            try {
                const res = await fetch(`http://localhost:8080/api/voters/${voterId}`);
                if (!res.ok) throw new Error('Failed to fetch voter data');
                const data = await res.json();
                setVoterData({
                    voterName: data.voterName,
                    voterEmail: data.voterEmail,
                    voterType: data.voterType,
                    verifiedHuman: data.verifiedHuman,
                });
                setRegion(data.region || '');
                setBirthdate(data.birthdate || '');
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
        formData.append('voterName', voterData.voterName);
        formData.append('voterEmail', voterData.voterEmail);
        if (selectedImage) formData.append('file', selectedImage);

        try {
            const res = await fetch(`http://localhost:8080/api/voters/${voterId}`, {
                method: 'PUT',
                body: formData,
            });

            const result = await res.json();
            if (!res.ok) throw new Error(result.message || 'Unknown error');
            alert('✅ Profile updated successfully!');
            // Refresh voter info after save
            const refreshedRes = await fetch(`http://localhost:8080/api/voters/${voterId}`);
            const refreshedData = await refreshedRes.json();
            setVoterData({
                voterName: refreshedData.voterName,
                voterEmail: refreshedData.voterEmail,
                voterType: refreshedData.voterType,
                verifiedHuman: refreshedData.verifiedHuman,
            });
            setPreviewUrl(`http://localhost:8080/api/voters/${voterId}/image`);
        } catch (err) {
            console.error('Update failed:', err);
            alert(`❌ Update failed: ${err.message}`);
        }
    };

    const handleDelete = async () => {
        if (!window.confirm('Are you sure you want to delete your profile?')) return;

        try {
            const res = await fetch(`http://localhost:8080/api/voters/${voterId}`, {
                method: 'DELETE',
            });
            if (!res.ok) throw new Error('Failed to delete profile.');
            alert('✅ Profile deleted.');
            logout();
            navigate('/');
        } catch (err) {
            console.error('Delete failed:', err);
            alert(`❌ ${err.message}`);
        }
    };

    const handleVerifyHuman = async () => {
        if (!/^\d{13}$/.test(cnp)) {
            setVerificationStatus('❌ CNP must be 13 digits.');
            return;
        }

        try {
            const res = await fetch(`http://localhost:8080/api/voters/${voterId}/verify-human`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    ssn: cnp,
                    region,
                    birthdate,
                }),

            });

            const result = await res.json();
            if (!res.ok) throw new Error(result.error || 'Verification failed.');
            setVerificationStatus('✅ Verified successfully!');
            // Refresh voter data after verification
            const refreshedRes = await fetch(`http://localhost:8080/api/voters/${voterId}`);
            const refreshedData = await refreshedRes.json();
            setVoterData({
                voterName: refreshedData.voterName,
                voterEmail: refreshedData.voterEmail,
                voterType: refreshedData.voterType,
                verifiedHuman: refreshedData.verifiedHuman,
            });
        } catch (err) {
            console.error('Verification failed:', err);
            setVerificationStatus(`❌ ${err.message}`);
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
                    <h2>Your Profile</h2>
                </div>

                <div className="profile-form-group">
                    <label>Name</label>
                    <input
                        type="text"
                        value={voterData.voterName}
                        onChange={(e) => setVoterData({ ...voterData, voterName: e.target.value })}
                    />
                </div>

                <div className="profile-form-group">
                    <label>Email</label>
                    <input
                        type="email"
                        value={voterData.voterEmail}
                        onChange={(e) => setVoterData({ ...voterData, voterEmail: e.target.value })}
                    />
                </div>

                <div className="profile-form-group">
                    <label>Profile Image</label>
                    <input type="file" accept="image/*" onChange={handleImageChange} />
                </div>

                <div className="profile-buttons">
                    <button className="save" onClick={handleSave}>Save</button>
                    <button className="delete" onClick={handleDelete}>Delete</button>
                </div>

                {!voterData.verifiedHuman ? (
                    <div className="verify-section">
                        <h3>Human Verification</h3>
                        <p className="warning-text">
                            To vote in INSTITUTION elections, enter your CNP (SSN) below:
                        </p>
                        <input
                            type="text"
                            value={cnp}
                            onChange={(e) => setCnp(e.target.value)}
                            placeholder="Enter your CNP (13 digits)"
                            maxLength={13}
                        />
                        <input
                            type="text"
                            value={region}
                            onChange={(e) => setRegion(e.target.value)}
                            placeholder="Enter your region (e.g. Cluj)"
                        />

                        <input
                            type="date"
                            value={birthdate}
                            onChange={(e) => setBirthdate(e.target.value)}
                            placeholder="Enter your birthdate"
                        />

                        <button onClick={handleVerifyHuman}>Verify</button>
                        {verificationStatus && <p>{verificationStatus}</p>}
                    </div>
                ) : (
                    <div className="verify-section">
                        <h3>✅ You are already verified!</h3>
                        <p>You can vote in INSTITUTION elections.</p>
                    </div>
                )}
            </div>
        </>
    );
}

export default Profile;
