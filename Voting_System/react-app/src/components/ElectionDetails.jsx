import React, {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import "../styles/ElectionDetails.css"

function ElectionDetails() {
    const {electionId} = useParams(); // Get the election ID from the URL
    const [election, setElection] = useState(null);
    const [candidates, setCandidates] = useState([]);

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

    if (!election) return <p>Loading election details...</p>;

    return (
        <div className="election-page">
            <div className="election-container">
                <h1 className="election-title">{election.electionName}</h1>
                <div className="election-details">
                    <p><strong>Start Date:</strong> {election.startDate}</p>
                    <p><strong>End Date:</strong> {election.endDate}</p>
                    <p><strong>Total Votes:</strong> {election.electionVotes}</p>
                </div>

                <h2>Candidates</h2>
                <ul className="candidates-list">
                    {candidates.map(candidate => (
                        <li key={candidate.candidateId}>
                            {candidate.candidateName} - {candidate.candidateParty}
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );

}

export default ElectionDetails;
