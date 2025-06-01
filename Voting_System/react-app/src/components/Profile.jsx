import React, { useContext, useEffect, useState } from 'react';
import { VoterContext } from '../context/VoterContext';
import { useNavigate } from 'react-router-dom';
import Navbar from './Navbar';
import '../styles/Profile.css';
import Select from 'react-select';
import Fuse from 'fuse.js';
import cityData from '../assets/ro_city_county.json';

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
    const [regionOptions, setRegionOptions] = useState([]);
    const [selectedCounty, setSelectedCounty] = useState('');
    const [cityOptions, setCityOptions] = useState([]);


    const [birthdate, setBirthdate] = useState('');

    const [selectedImage, setSelectedImage] = useState(null);
    const [previewUrl, setPreviewUrl] = useState('');
    const [verificationStatus, setVerificationStatus] = useState('');

    useEffect(() => {
        const localities = [];
        cityData.judete.forEach(judet => {
            judet.localitati.forEach(loc => {
                const name = normalizeName(loc.simplu || loc.nume);
                localities.push({ label: name, value: name });
            });
        });
        localities.sort((a, b) => a.label.localeCompare(b.label));
        setRegionOptions(localities);
    }, []);

    // Fetch voter info only if voterId is valid
    useEffect(() => {
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
            if (selectedImage) {
                const reader = new FileReader();
                reader.onloadend = () => {
                    setPreviewUrl(reader.result); // ✅ show uploaded image immediately
                };
                reader.readAsDataURL(selectedImage);
            } else {
                setPreviewUrl(`http://localhost:8080/api/voters/${voterId}/image`);
            }

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

    useEffect(() => {
        if (!voterId) {
            navigate('/');
        }
    }, [voterId, navigate]);

    function normalizeName(name) {
        return name
            .normalize("NFD").replace(/[\u0300-\u036f]/g, '')
            .replace(/[șŞ]/g, 's').replace(/[țŢ]/g, 't')
            .replace(/[ăĂ]/g, 'a').replace(/[îÎ]/g, 'i')
            .replace(/[âÂ]/g, 'a');
    }



    return (
        <div className="profile-page">
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
                        onChange={(e) => setVoterData({...voterData, voterName: e.target.value})}
                    />
                </div>

                <div className="profile-form-group">
                    <label>Email</label>
                    <input
                        type="email"
                        value={voterData.voterEmail}
                        onChange={(e) => setVoterData({...voterData, voterEmail: e.target.value})}
                    />
                </div>

                <div className="profile-form-group">
                    <label>Profile Image</label>
                    <label htmlFor="file-upload" className="custom-file-upload">
                        Click to upload your Profile Image!
                    </label>
                    <input id="file-upload" type="file" accept="image/*" onChange={handleImageChange}/>
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

                        <div className={"form-wrapper"}>
                            <label>SSN:</label>
                        <input
                            type="text"
                            value={cnp}
                            onChange={(e) => setCnp(e.target.value)}
                            placeholder="Enter your CNP (13 digits)"
                            maxLength={13}
                        />
                        </div>
                        <div className={"form-wrapper"}>
                        <label>County:</label>
                        <select
                            value={selectedCounty}
                            onChange={(e) => {
                                const countyName = e.target.value;
                                setSelectedCounty(countyName);

                                const county = cityData.judete.find(j => j.nume === countyName);
                                const localitati = county?.localitati || [];

                                const normalized = localitati.map(loc =>
                                    normalizeName(loc.simplu || loc.nume)
                                );

                                normalized.sort();
                                setCityOptions(normalized);
                                setRegion(''); // reset city selection
                            }}
                        >
                            <option value="">Select a county</option>
                            {cityData.judete.map((j, i) => (
                                <option key={i} value={j.nume}>{j.nume}</option>
                            ))}
                        </select>
                        </div>

                        <div className={"form-wrapper"}>
                        <label>City:</label>
                        <select
                            value={region}
                            onChange={(e) => setRegion(e.target.value)}
                            disabled={!selectedCounty}
                        >
                            <option value="">Select a city</option>
                            {cityOptions.map((city, i) => (
                                <option key={i} value={city}>{city}</option>
                            ))}
                        </select>
                        </div>

                        <div className={"form-wrapper"}>
                        <label>DOB:</label>
                        <input
                            type="date"
                            value={birthdate}
                            onChange={(e) => setBirthdate(e.target.value)}
                            placeholder="Enter your birthdate"
                        />
                        </div>

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
        </div>
    );
}

export default Profile;
