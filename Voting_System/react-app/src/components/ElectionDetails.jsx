import React, {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import "../styles/ElectionDetails.css";

function ElectionDetails() {
    const {electionId} = useParams();
    const [election, setElection] = useState(null);
    const [candidates, setCandidates] = useState([]);
    const [selectedCandidates, setSelectedCandidates] = useState([]);

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

        fetchElectionDetails();
        fetchCandidates();
    }, [electionId]);

    const handleCheckboxChange = (candidateId) => {
        setSelectedCandidates(prev =>
            prev.includes(candidateId)
                ? prev.filter(id => id !== candidateId)
                : [...prev, candidateId]
        );
    };

    const handleSubmitVote = async () => {
        if (selectedCandidates.length === 0) {
            alert("Please select at least one candidate.");
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/api/votes`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    electionId,
                    candidateIds: selectedCandidates
                })
            });

            if (response.ok) {
                alert("Vote submitted successfully!");
                setSelectedCandidates([]);
            } else {
                alert("Error submitting vote.");
            }
        } catch (error) {
            console.error("Error submitting vote:", error);
            alert("Error submitting vote.");
        }
    };

    if (!election) return <p>Loading election details...</p>;

    return (
        <div className="election-page">
            <div className="election-container">
                <h1 className="election-title">{election.electionName}</h1>
                <div className="election-details">
                    <p><strong>Start Date:</strong> {election.startDate}</p>
                    <p><strong>End Date:</strong> {election.endDate}</p>
                    <p><strong>Total Votes:</strong> {election.electionVotes}</p>
                    <p><strong>Must select:</strong> {election.nrVotesPerVoter} checkboxes</p>
                    <p>{election.electionDescription}</p>
                </div>

                <h2>Candidates</h2>
                <ul className="candidates-list">
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

                <button className="submit-vote-button" onClick={handleSubmitVote}>
                    Submit Vote
                </button>
            </div>
        </div>
    );
}

export default ElectionDetails;
