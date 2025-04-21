import React, {useState, useEffect} from "react";
import {useNavigate} from "react-router-dom";
import "../styles/ElectionsPage.css";

function ElectionsPage() {
    const [elections, setElections] = useState([]);
    const navigate = useNavigate();
    const [authorities, setAuthorities] = useState({});


    useEffect(() => {
        const fetchElections = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/elections");
                if (!response.ok) {
                    throw new Error("Failed to fetch elections");
                }
                const data = await response.json();
                console.log(data);
                setElections(data);

                // Fetch authorities for each election
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

        fetchElections();
    }, []);

    const handleGetElectionAuthority = async (id) => {
        try {
            const response = await fetch(`http://localhost:8080/api/election-authorities/${id}`);
            if (!response.ok) {
                throw new Error("Failed to fetch election authority");
            }
            const data = await response.json();
            console.log("Election Authority:", data);
            return data;
        } catch (error) {
            console.error("Error fetching election authority:", error);
            return null;
        }
    };

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
                            <h3>
                                {election.electionName}
                            </h3>
                            <h4>Creator: {authorities[election.electionAuthorityId]?.authorityName || "Unknown"}</h4>
                            <p>
                                {election.startDate ? new Date(election.startDate).toLocaleDateString() : "Unknown"}
                                -
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