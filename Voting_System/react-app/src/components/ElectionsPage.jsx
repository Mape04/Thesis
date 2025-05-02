import React, { useState, useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faLock } from '@fortawesome/free-solid-svg-icons';

import "../styles/ElectionsPage.css";
import { VoterContext } from "../context/VoterContext.jsx";
import Navbar from "./Navbar.jsx";

function ElectionsPage() {
    const [elections, setElections] = useState([]);
    const [authorities, setAuthorities] = useState({});
    const [searchQuery, setSearchQuery] = useState('');
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [showPasswordModal, setShowPasswordModal] = useState(false);
    const [selectedElectionId, setSelectedElectionId] = useState(null);
    const [passwordInput, setPasswordInput] = useState('');
    const [passwordError, setPasswordError] = useState('');
    const [voterInfo, setVoterInfo] = useState({ voterName: '', voterEmail: '' });

    const [newElection, setNewElection] = useState({
        electionName: '',
        electionPassword: '',
        startDate: '',
        endDate: '',
        nrVotesPerVoter: '',
        candidates: [{ name: '', party: '' }]
    });


    const { voterId } = useContext(VoterContext); // 🔥 You might use voterId for creating ElectionAuthority

    const navigate = useNavigate();

    const handlePasswordSubmit = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/elections/verify-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    electionId: selectedElectionId,
                    password: passwordInput
                })
            });

            if (response.ok) {
                navigate(`/election/${selectedElectionId}`);
            } else {
                const data = await response.json();
                setPasswordError(data.error || 'Incorrect password.');
            }
        } catch (err) {
            console.error('Error verifying password:', err);
            setPasswordError('Server error.');
        }
    };



    useEffect(() => {
        const fetchElections = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/elections");
                if (!response.ok) {
                    throw new Error("Failed to fetch elections");
                }
                const data = await response.json();
                setElections(data);

                const authorityMap = {};
                for (const election of data) {
                    const res = await fetch(`http://localhost:8080/api/election-authorities/${election.electionAuthorityId}`);
                    if (res.ok) {
                        const authData = await res.json();
                        authorityMap[election.electionAuthorityId] = authData;
                    }
                }
                setAuthorities(authorityMap);
            } catch (error) {
                console.error("Error fetching elections or authorities:", error);
            }
        };

        const fetchVoterInfo = async () => {
            try {
                const response = await fetch(`http://localhost:8080/api/voters/${voterId}`);
                if (response.ok) {
                    const data = await response.json();
                    setVoterInfo({
                        voterName: data.voterName,
                        voterEmail: data.voterEmail
                    });
                }
            } catch (error) {
                console.error("Error fetching voter info:", error);
            }
        };

        fetchVoterInfo();
        fetchElections();
    }, [voterId]);

    const handleElectionClick = async (electionId) => {
        try {
            const res = await fetch(`http://localhost:8080/api/elections/${electionId}`);
            const data = await res.json();

            if (data.electionPassword) {
                setSelectedElectionId(electionId);
                setShowPasswordModal(true);
                setPasswordInput('');
                setPasswordError('');
            } else {
                navigate(`/election/${electionId}`);
            }
        } catch (error) {
            console.error("Failed to fetch election info:", error);
        }
    };



    const handleCreateElectionClick = () => {
        setShowCreateModal(true);
    };

    const handleModalChange = (e) => {
        const { name, value } = e.target;
        setNewElection(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleCandidateChange = (index, field, value) => {
        const updatedCandidates = [...newElection.candidates];
        updatedCandidates[index][field] = value;
        setNewElection(prev => ({
            ...prev,
            candidates: updatedCandidates
        }));
    };


    const addCandidateField = () => {
        setNewElection(prev => ({
            ...prev,
            candidates: [...prev.candidates, { name: '', party: '' }]
        }));
    };


    const submitNewElection = async () => {
        try {
            // 🔥 Step 1: Create ElectionAuthority (backend should create based on voterId!)
            const authorityResponse = await fetch("http://localhost:8080/api/election-authorities", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    authorityName: voterInfo.voterName,
                    authorityEmail: voterInfo.voterEmail
                })
            });

            const authorityData = await authorityResponse.json();
            const electionAuthorityId = authorityData.electionAuthorityId;

            // 🔥 Step 2: Create Election
            const electionResponse = await fetch(`http://localhost:8080/api/elections/${electionAuthorityId}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    electionName: newElection.electionName,
                    electionPassword: newElection.electionPassword || null, //send null if empty
                    startDate: newElection.startDate,
                    endDate: newElection.endDate,
                    electionVotes: 0,
                    nrVotesPerVoter: parseInt(newElection.nrVotesPerVoter),
                    electionDescription: "Custom created election"

                })
            });

            const createdElection = await electionResponse.json();

            // 🔥 Step 3: Add Candidates
            for (const candidate of newElection.candidates) {
                if (candidate.name.trim() !== '') {
                    await fetch("http://localhost:8080/api/candidates", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({
                            candidateName: candidate.name,
                            candidateParty: candidate.party,
                            candidateElectionId: createdElection.electionId
                        })
                    });
                }
            }

            alert("Election created successfully!");
            setShowCreateModal(false);
            window.location.reload(); // 🔥 Reload to refresh elections list

        } catch (error) {
            console.error("Error creating election:", error);
            alert("Error creating election.");
        }
    };

    return (
        <>
            <Navbar />
            <div className="elections-page-container">
                <h1>Available Elections</h1>

                {/* 🔥 Search and Create Bar */}
                <div className="search-create-bar">
                    <input
                        type="text"
                        placeholder="Search elections..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="search-bar"
                    />
                    <button className="create-election-button" onClick={handleCreateElectionClick}>
                        + Create Election
                    </button>
                </div>

                <div className="elections-list">
                    {elections.length > 0 ? (
                        elections
                            .filter(election => election.electionName.toLowerCase().includes(searchQuery.toLowerCase()))
                            .map((election) => (
                                <div
                                    key={election.electionId}
                                    className="election-item"
                                    onClick={() => handleElectionClick(election.electionId)}
                                >
                                    <h3>
                                        {election.electionName}
                                        {election.electionPassword && (
                                            <FontAwesomeIcon icon={faLock} title="Private Election"
                                                             style={{marginLeft: "8px", color: "#ff5252"}}/>
                                        )}
                                    </h3>

                                    <h4>Creator: {authorities[election.electionAuthorityId]?.authorityName || "Unknown"}</h4>
                                    <p>
                                        {election.startDate ? new Date(election.startDate).toLocaleDateString() : "Unknown"} -
                                        {election.endDate ? new Date(election.endDate).toLocaleDateString() : "Unknown"}
                                    </p>
                                </div>
                            ))
                    ) : (
                        <p>No elections available.</p>
                    )}
                </div>
            </div>

            {/* 🔥 Create Election Modal */}
            {showCreateModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Create New Election</h2>

                        <input
                            type="text"
                            name="electionName"
                            placeholder="Election Name"
                            value={newElection.electionName}
                            onChange={handleModalChange}
                        />
                        <input
                            type="password"
                            name="electionPassword"
                            placeholder="(Optional) Election Password"
                            value={newElection.electionPassword}
                            onChange={handleModalChange}
                        />
                        <input
                            type="datetime-local"
                            name="startDate"
                            value={newElection.startDate}
                            onChange={handleModalChange}
                        />
                        <input
                            type="datetime-local"
                            name="endDate"
                            value={newElection.endDate}
                            onChange={handleModalChange}
                        />
                        <input
                            type="number"
                            name="nrVotesPerVoter"
                            placeholder="Votes per Voter"
                            value={newElection.nrVotesPerVoter}
                            onChange={handleModalChange}
                        />

                        <h3>Candidates</h3>
                        {newElection.candidates.map((candidate, index) => (
                            <div key={index} className="candidate-input-group">
                                <input
                                    type="text"
                                    placeholder="Candidate Name"
                                    value={candidate.name || ''}
                                    onChange={(e) => handleCandidateChange(index, 'name', e.target.value)}
                                />
                                <input
                                    type="text"
                                    placeholder="Candidate Party"
                                    value={candidate.party || ''}
                                    onChange={(e) => handleCandidateChange(index, 'party', e.target.value)}
                                />
                            </div>
                        ))}
                        <button onClick={addCandidateField}>+ Add Candidate</button>


                        <div className="modal-buttons">
                            <button onClick={submitNewElection}>Submit</button>
                            <button onClick={() => setShowCreateModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {showPasswordModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>Enter Password to Access Election</h3>
                        <input
                            type="password"
                            placeholder="Election password"
                            value={passwordInput}
                            onChange={(e) => setPasswordInput(e.target.value)}
                        />
                        {passwordError && <p style={{ color: 'red' }}>{passwordError}</p>}
                        <div className="modal-buttons">
                            <button onClick={handlePasswordSubmit}>Submit</button>
                            <button onClick={() => setShowPasswordModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

        </>
    );
}

export default ElectionsPage;
