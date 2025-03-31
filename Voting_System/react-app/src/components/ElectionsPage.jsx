import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/ElectionsPage.css";

function ElectionsPage() {
    const [elections, setElections] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchElections = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/elections");
                if (!response.ok) {
                    throw new Error("Failed to fetch elections");
                }
                const data = await response.json();
                setElections(data);
            } catch (error) {
                console.error("Error fetching elections:", error);
            }
        };

        fetchElections();
    }, []);

    const handleElectionClick = (electionId) => {
        if (electionId) {
            navigate(`/election/${electionId}`);
        } else {
            console.error("Invalid election ID");
        }
    };

    return (
        <div className="elections-page-container">
            <h1>Available Elections</h1>
            <div className="elections-list">
                {elections.length > 0 ? (
                    elections.map((election) => (
                        <div
                            key={election.electionId}
                            className="election-item"
                            onClick={() => handleElectionClick(election.electionId)}
                        >
                            <h3>{election.electionName}</h3>
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
    );
}

export default ElectionsPage;