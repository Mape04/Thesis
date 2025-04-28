import React, { useEffect, useState, useContext } from "react";
import { useParams } from "react-router-dom";
import { VoterContext } from '../context/VoterContext';
import "../styles/ElectionDetails.css";
import Navbar from "./Navbar.jsx";

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
    const { electionId } = useParams();
    const { voterId } = useContext(VoterContext);
    const [election, setElection] = useState(null);
    const [candidates, setCandidates] = useState([]);
    const [selectedCandidates, setSelectedCandidates] = useState([]);
    const [publicKey, setPublicKey] = useState({ e: null, n: null });

    useEffect(() => {
        const fetchElectionDetails = async () => {
            try {
                const response = await fetch(`http://localhost:8080/api/elections/${electionId}`);
                const data = await response.json();
                setElection(data);
            } catch (error) {
                console.error("Error fetching election details:", error);
            }
        };

        const fetchCandidates = async () => {
            try {
                const response = await fetch(`http://localhost:8080/api/elections/${electionId}/candidates`);
                const data = await response.json();
                setCandidates(data);
            } catch (error) {
                console.error("Error fetching candidates:", error);
            }
        };

        const fetchPublicKey = async () => {
            try {
                const response = await fetch(`http://localhost:8080/api/encryption/public-key/${electionId}`);
                if (!response.ok) {
                    throw new Error("Failed to fetch public key");
                }
                const data = await response.json();
                setPublicKey({ n: BigInt(data.n), e: BigInt(data.e) });
            } catch (error) {
                console.error("Error fetching public key:", error);
            }
        };

        fetchElectionDetails();
        fetchCandidates();
        fetchPublicKey();
    }, [electionId]);

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
                headers: { "Content-Type": "application/json" },
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
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    voterId: voterId,
                    electionId: electionId,
                    voterToken: finalSignedToken.toString()
                })
            });

            // 7. Submit vote normally
            const voteResponse = await fetch("http://localhost:8080/api/votes", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    voterId: voterId,
                    voterToken: finalSignedToken.toString(),
                    electionId: electionId,
                    candidateIds: selectedCandidates
                })
            });

            if (voteResponse.ok) {
                alert("Vote submitted successfully!");
                setSelectedCandidates([]);
            } else {
                const errorData = await voteResponse.text();
                alert(errorData || "Error submitting vote.");
            }
        } catch (error) {
            console.error("Error submitting vote:", error);
            alert(error.message || "Unexpected error occurred.");
        }
    };

    if (!election) return <p>Loading election details...</p>;

    return (
        <>
            <Navbar/>
            <div className="election-page">
                <div className="election-container">
                    <h1>{election.electionName}</h1>
                    <p><strong>Start:</strong> {election.startDate}</p>
                    <p><strong>End:</strong> {election.endDate}</p>
                    <p><strong>Votes:</strong> {election.electionVotes}</p>
                    <p><strong>Select:</strong> {election.nrVotesPerVoter} candidates</p>
                    <p>{election.electionDescription}</p>

                    <h2>Candidates</h2>
                    <ul>
                        {candidates.map(candidate => (
                            <li key={candidate.candidateId}>
                                <label>
                                    <input
                                        type="checkbox"
                                        checked={selectedCandidates.includes(candidate.candidateId)}
                                        onChange={() => handleCheckboxChange(candidate.candidateId)}
                                    />
                                    {candidate.candidateName} - {candidate.candidateParty}
                                </label>
                            </li>
                        ))}
                    </ul>

                    <button onClick={handleSubmitVote}>Submit Vote</button>
                </div>
            </div>
        </>
    );
}

export default ElectionDetails;
