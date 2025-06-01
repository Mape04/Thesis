import React, {useEffect, useState, useContext} from "react";
import {useParams} from "react-router-dom";
import {VoterContext} from '../context/VoterContext';
import "../styles/ElectionDetails.css";
import Navbar from "./Navbar.jsx";
import { FaEdit, FaTrash, FaPlus } from "react-icons/fa";
import ElectionAnalytics from "./ElectionAnalytics";


// Modular exponentiation
const modPow = (base, exponent, modulus) => {
    base = BigInt(base);
    exponent = BigInt(exponent);
    modulus = BigInt(modulus);
    if (modulus === 1n) return 0n;
    let result = 1n;
    base = base % modulus;
    while (exponent > 0n) {
        if (exponent % 2n === 1n) {
            result = (result * base) % modulus;
        }
        exponent = exponent >> 1n;
        base = (base * base) % modulus;
    }
    return result;
};

// Modular inverse
const modInverse = (a, m) => {
    let m0 = m, t, q;
    let x0 = 0n, x1 = 1n;
    if (m === 1n) return 0n;
    while (a > 1n) {
        q = a / m;
        t = m;
        m = a % m;
        a = t;
        t = x0;
        x0 = x1 - q * x0;
        x1 = t;
    }
    if (x1 < 0n) x1 += m0;
    return x1;
};

// SHA-256 hash of a string -> hex string
async function sha256Hash(text) {
    const encoder = new TextEncoder();
    const data = encoder.encode(text);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');

}

function ElectionDetails() {
    const {electionId} = useParams();
    const {voterId} = useContext(VoterContext);
    const [election, setElection] = useState(null);
    const [candidates, setCandidates] = useState([]);
    const [selectedCandidates, setSelectedCandidates] = useState([]);
    const [publicKey, setPublicKey] = useState({e: null, n: null});
    const [liveResults, setLiveResults] = useState({});
    const [finalResults, setFinalResults] = useState({});
    const [hasVoted, setHasVoted] = useState(false);
    const [totalVotes, setTotalVotes] = useState(0);
    const [isEditMode, setIsEditMode] = useState(false);
    const [editedElection, setEditedElection] = useState({});
    const [isAuthority, setIsAuthority] = useState(false);
    const [removedCandidateIds, setRemovedCandidateIds] = useState([]);



    const fetchResults = async () => {
        try {
            const now = new Date();
            const electionEnd = new Date(election.endDate);

            if (now < electionEnd) {
                const response = await fetch(`http://localhost:8080/api/elections/${electionId}/live-results`);
                const data = await response.json();
                setLiveResults(data);
            } else {
                const response = await fetch(`http://localhost:8080/api/elections/${electionId}/final-results`);
                const data = await response.json();
                setFinalResults(data);
            }
        } catch (error) {
            console.error("Error fetching results:", error);
        }
    };

    const fetchTotalVotes = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/elections/${electionId}/vote-count`);
            const total = await response.json();
            setTotalVotes(total);
        } catch (error) {
            console.error("Error fetching total votes:", error);
        }
    };



    // 1. First fetch basic election + candidates + public key
    useEffect(() => {
        const fetchData = async () => {
            try {
                const electionResponse = await fetch(`http://localhost:8080/api/elections/${electionId}`);
                const electionData = await electionResponse.json();
                setElection(electionData);

                const candidatesResponse = await fetch(`http://localhost:8080/api/elections/${electionId}/candidates`);
                const candidatesData = await candidatesResponse.json();
                setCandidates(candidatesData);

                const publicKeyResponse = await fetch(`http://localhost:8080/api/encryption/public-key/${electionId}`);
                if (!publicKeyResponse.ok) {
                    throw new Error("Failed to fetch public key");
                }
                const publicKeyData = await publicKeyResponse.json();
                setPublicKey({n: BigInt(publicKeyData.n), e: BigInt(publicKeyData.e)});

            } catch (error) {
                console.error("Error fetching election details:", error);
            }
        };

        fetchData();
    }, [electionId]);

// 2. Then when election is loaded, fetch results and voter status
    useEffect(() => {
        if (!election || !voterId) return;

        fetchResults();
        fetchTotalVotes();

        const checkIfVoted = async () => {
            const response = await fetch(`http://localhost:8080/api/voters/${voterId}/elections/${electionId}/has-token-been-used`);
            const voted = await response.json();
            setHasVoted(voted);
        };



        checkIfVoted();

        const savedChoices = localStorage.getItem(`votedChoices-${electionId}`);
        if (savedChoices) {
            const parsedChoices = JSON.parse(savedChoices);
            setSelectedCandidates(parsedChoices);
            setHasVoted(true);
        }

        fetch(`http://localhost:8080/api/election-authorities/match?voterId=${voterId}&electionId=${electionId}`)
            .then(res => res.json())
            .then(setIsAuthority)
            .catch(err => console.error("Authority check failed", err));

    }, [election, electionId, voterId]);

    const submitElectionEdit = async () => {
        try {
            // 🔹 Update the election info
            const res = await fetch(`http://localhost:8080/api/elections/${election.electionId}`, {
                method: 'PUT',
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(editedElection),
            });

            if (!res.ok) {
                alert("Failed to update election.");
                return;
            }

            // 🔹 Update candidates
            // 1. Create new ones
            for (const c of editedElection.candidates.filter(c => !c.candidateId)) {
                await fetch("http://localhost:8080/api/candidates", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        candidateName: c.candidateName,
                        candidateParty: c.candidateParty,
                        candidateElectionId: election.electionId
                    })
                });
            }

            // 2. Update existing
            for (const c of editedElection.candidates.filter(c => c.candidateId)) {
                await fetch(`http://localhost:8080/api/candidates/${c.candidateId}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        candidateName: c.candidateName,
                        candidateParty: c.candidateParty,
                        candidateElectionId: election.electionId
                    })
                });
            }

            // 3. Delete removed candidates (track with a `removedCandidateIds` array)
            for (const id of removedCandidateIds) {
                await fetch(`http://localhost:8080/api/candidates/${id}`, {
                    method: "DELETE"
                });
            }

            alert("Election updated successfully!");
            setIsEditMode(false);

            // 🔁 Optionally re-fetch the election and candidates
            window.location.reload();

        } catch (err) {
            console.error("Edit error:", err);
            alert("Server error.");
        }
    };




    const handleCheckboxChange = (candidateId) => {
        setSelectedCandidates(prev =>
            prev.includes(candidateId) ? prev.filter(id => id !== candidateId) : [...prev, candidateId]
        );
    };

    const handleSubmitVote = async () => {
        if (selectedCandidates.length === 0) {
            alert("Please select at least one candidate.");
            return;
        }

        if (!publicKey.e || !publicKey.n) {
            alert("Public key not loaded yet. Please wait...");
            return;
        }

        try {
            // 1. Concatenate candidate IDs
            const concatenatedIds = selectedCandidates.join("-");
            console.log("Concatenated Candidate IDs:", concatenatedIds);

            // 2. Hash the concatenated IDs
            const hashedHex = await sha256Hash(concatenatedIds);
            console.log("Hashed Hex:", hashedHex);

            const messageBigIntRaw = BigInt("0x" + hashedHex);
            const messageBigInt = messageBigIntRaw % publicKey.n;
            console.log("Message BigInt:", messageBigInt.toString());

            // 3. Generate random r
            let r;
            do {
                r = BigInt("0x" + Array.from(crypto.getRandomValues(new Uint8Array(32)))
                    .map(b => b.toString(16).padStart(2, '0')).join(''));
            } while (r <= 1n || r >= publicKey.n - 1n);

            const blindingFactor = modPow(r, publicKey.e, publicKey.n);
            const blindedMessage = (blindingFactor * messageBigInt) % publicKey.n;
            console.log("Blinded Message:", blindedMessage.toString());

            // 4. Send blinded message for signing
            const response = await fetch("http://localhost:8080/api/encryption/sign-blinded-message", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    voterId: voterId,
                    blindedMessage: blindedMessage.toString(),
                    electionId: electionId
                })
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error("Signing error: " + errorText);
            }

            const signedBlindedMessageString = await response.text();
            console.log("Signed Blinded Message:", signedBlindedMessageString);

            // 5. Unblind the signed message
            const signedBlindedBigInt = BigInt(signedBlindedMessageString);
            const rInverse = modInverse(r, publicKey.n);
            const finalSignedToken = (signedBlindedBigInt * rInverse) % publicKey.n;
            console.log("Final Signed Token (Unblinded):", finalSignedToken.toString());

            // 6. Save final token into BlindCredential
            await fetch("http://localhost:8080/api/blind-credential/save", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    voterId: voterId,
                    electionId: electionId,
                    voterToken: finalSignedToken.toString()
                })
            });

            const voteResponse = await fetch("http://localhost:8080/api/votes", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    voterId: voterId,
                    voterToken: finalSignedToken.toString(),
                    electionId: electionId,
                    candidateIds: selectedCandidates
                })
            });

            const responseText = await voteResponse.text(); // Always first read as plain text

            let responseData;
            try {
                responseData = JSON.parse(responseText); // Try parsing it manually
            } catch (e) {
                console.error("Error parsing JSON:", e);
                throw new Error(responseText || "Server returned invalid response.");
            }

            if (responseData.status === "success") {
                alert(responseData.message);
                localStorage.setItem(`votedChoices-${electionId}`, JSON.stringify(selectedCandidates));
                setHasVoted(true);
                fetchResults();
                fetchTotalVotes();
            } else {
                alert(responseData.message);
            }
        } catch (error) {
            console.error("Error submitting vote:", error);
            alert(error.message || "Unexpected error occurred.");
        }
    };

    if (!election) return <p>Loading election details...</p>;

    return (
        <div className="election-details">
            <Navbar/>
            <div className="election-page">
                <div className="election-container">
                    <h1 className="election-title">{election.electionName}</h1>

                    <div className="election-info">
                        <p><strong>Start:</strong> {new Date(election.startDate).toLocaleString()}</p>
                        <p><strong>End:</strong> {new Date(election.endDate).toLocaleString()}</p>
                        <p><strong>Total Votes:</strong> {totalVotes}</p>
                        <p><strong>Max Selections:</strong> {election.nrVotesPerVoter}</p>
                        <p className="election-description">{election.electionDescription}</p>
                    </div>

                    <h2 className="candidates-title">Candidates</h2>
                    <ul className="candidates-list">
                        {candidates.map(candidate => (
                            <li key={candidate.candidateId} className={`candidate-item ${hasVoted ? 'voted' : ''}`}>
                                <label className="candidate-label">
                                    <input
                                        type="checkbox"
                                        disabled={hasVoted}
                                        checked={selectedCandidates.includes(candidate.candidateId)}
                                        onChange={() => handleCheckboxChange(candidate.candidateId)}
                                    />
                                    <span className="candidate-name">
                                    {candidate.candidateName} <small>({candidate.candidateParty})</small>
                                    </span>

                                    <span className="candidate-result">
                                      {liveResults[candidate.candidateName] !== undefined && (
                                          <span> | Votes: {liveResults[candidate.candidateName]}</span>
                                      )}
                                        {finalResults[candidate.candidateName] !== undefined && (
                                            <span> | {finalResults[candidate.candidateName].toFixed(2)}%</span>
                                        )}
                                     </span>
                                </label>
                            </li>
                        ))}
                    </ul>

                    <button
                        onClick={handleSubmitVote}
                        disabled={hasVoted}
                        className="submit-vote-button"
                    >
                        Submit Vote
                    </button>
                    {isAuthority && new Date(election.endDate) > new Date() && (
                        <button
                            className="edit-election-button"
                            onClick={() => {
                                setEditedElection({...election, candidates});
                                setRemovedCandidateIds([]);
                                setIsEditMode(true);
                            }}
                        >
                            <FaEdit style={{marginRight: "6px"}}/>
                            Edit Election
                        </button>

                    )}


                </div>
                <ElectionAnalytics electionId={electionId} />
            </div>

            {isEditMode && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Edit Election</h2>

                        <input
                            type="text"
                            name="electionName"
                            placeholder="Election Name"
                            value={editedElection.electionName}
                            onChange={(e) => setEditedElection({...editedElection, electionName: e.target.value})}
                        />
                        <input
                            type="text"
                            name="electionDescription"
                            placeholder="Description"
                            value={editedElection.electionDescription}
                            onChange={(e) => setEditedElection({
                                ...editedElection,
                                electionDescription: e.target.value
                            })}
                        />
                        <input
                            type="datetime-local"
                            name="startDate"
                            value={editedElection.startDate}
                            onChange={(e) => setEditedElection({...editedElection, startDate: e.target.value})}
                        />
                        <input
                            type="datetime-local"
                            name="endDate"
                            value={editedElection.endDate}
                            onChange={(e) => setEditedElection({...editedElection, endDate: e.target.value})}
                        />
                        <input
                            type="number"
                            name="nrVotesPerVoter"
                            placeholder="Votes per Voter"
                            value={editedElection.nrVotesPerVoter}
                            onChange={(e) => setEditedElection({...editedElection, nrVotesPerVoter: e.target.value})}
                        />
                        <h3>Edit Candidates</h3>
                        {editedElection.candidates?.map((candidate, index) => (
                            <div key={index} className="candidate-input-group">
                                <input
                                    type="text"
                                    placeholder="Candidate Name"
                                    value={candidate.candidateName}
                                    onChange={(e) => {
                                        const updated = [...editedElection.candidates];
                                        updated[index].candidateName = e.target.value;
                                        setEditedElection({...editedElection, candidates: updated});
                                    }}
                                />
                                <input
                                    type="text"
                                    placeholder="Party"
                                    value={candidate.candidateParty}
                                    onChange={(e) => {
                                        const updated = [...editedElection.candidates];
                                        updated[index].candidateParty = e.target.value;
                                        setEditedElection({...editedElection, candidates: updated});
                                    }}
                                />
                                <button
                                    type="button"
                                    className="remove-candidate-button"
                                    onClick={() => {
                                        const removed = candidate.candidateId
                                            ? [...removedCandidateIds, candidate.candidateId]
                                            : removedCandidateIds;

                                        const updated = editedElection.candidates.filter((_, i) => i !== index);

                                        setRemovedCandidateIds(removed);
                                        setEditedElection({...editedElection, candidates: updated});
                                    }}
                                >
                                    <FaTrash/>
                                </button>
                            </div>
                        ))}

                        <button
                            type="button"
                            className="add-candidate-button"
                            onClick={() =>
                                setEditedElection({
                                    ...editedElection,
                                    candidates: [...(editedElection.candidates || []), {
                                        candidateName: "",
                                        candidateParty: ""
                                    }]
                                })
                            }
                        >
                            <FaPlus/> Add Candidate
                        </button>


                        <div className="modal-buttons">
                            <button onClick={submitElectionEdit}>Save Changes</button>
                            <button onClick={() => setIsEditMode(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

        </div>
    );
}

export default ElectionDetails;
