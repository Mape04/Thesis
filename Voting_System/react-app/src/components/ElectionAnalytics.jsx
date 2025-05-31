import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
    Bar, Line, Doughnut,
} from "react-chartjs-2";
import {
    Chart as ChartJS,
    CategoryScale, LinearScale, BarElement,
    PointElement, LineElement, Title, Tooltip, Legend,
    ArcElement,
} from "chart.js";
import "../styles/ElectionAnalytics.css";

ChartJS.register(
    CategoryScale, LinearScale, BarElement,
    PointElement, LineElement, ArcElement,
    Title, Tooltip, Legend
);

function ElectionAnalytics() {
    const { electionId } = useParams();
    const [activeTab, setActiveTab] = useState("votes");
    const [votesPerCandidate, setVotesPerCandidate] = useState([]);
    const [turnoutStats, setTurnoutStats] = useState([]);
    const [regionParticipation, setRegionParticipation] = useState([]);

    useEffect(() => {
        const fetchAnalytics = async () => {
            const urls = [
                `http://localhost:8080/api/elections/${electionId}/analytics/votes-per-candidate`,
                `http://localhost:8080/api/elections/${electionId}/analytics/turnout`,
                `http://localhost:8080/api/elections/${electionId}/analytics/region-participation`
            ];

            try {
                const [votesRes, turnoutRes, regionRes] = await Promise.all(urls.map(url => fetch(url)));
                setVotesPerCandidate(await votesRes.json());
                setTurnoutStats(await turnoutRes.json());
                setRegionParticipation(await regionRes.json());
            } catch (err) {
                console.error("❌ Analytics fetch failed:", err);
            }
        };

        fetchAnalytics();
    }, [electionId]);

    const chartOptions = {
        responsive: true,
        plugins: {
            legend: {
                display: false
            },
            tooltip: {
                backgroundColor: "#333",
                titleColor: "#fff",
                bodyColor: "#fff"
            }
        },
        scales: {
            x: {
                ticks: { color: "#444" },
                grid: { display: false }
            },
            y: {
                beginAtZero: true,
                ticks: { color: "#444" },
                grid: { color: "#eee" }
            }
        }
    };

    const renderChart = () => {
        if (activeTab === "votes") {
            return (
                <Bar
                    data={{
                        labels: votesPerCandidate.map(v => v.candidateName),
                        datasets: [{
                            label: "Votes",
                            data: votesPerCandidate.map(v => v.voteCount),
                            backgroundColor: "#4A90E2"
                        }]
                    }}
                    options={{ ...chartOptions, plugins: { ...chartOptions.plugins, title: { display: true, text: "Votes Per Candidate" } } }}
                />
            );
        }

        if (activeTab === "turnout") {
            return (
                <Line
                    data={{
                        labels: turnoutStats.map(t => t.timeInterval),
                        datasets: [{
                            label: "Voter Turnout",
                            data: turnoutStats.map(t => t.voteCount),
                            borderColor: "#7ED957",
                            fill: false,
                            tension: 0.3
                        }]
                    }}
                    options={{ ...chartOptions, plugins: { ...chartOptions.plugins, title: { display: true, text: "Turnout Over Time" } } }}
                />
            );
        }

        if (activeTab === "region") {
            return (
                <Doughnut
                    data={{
                        labels: regionParticipation.map(r => r.region),
                        datasets: [{
                            data: regionParticipation.map(r => r.voteCount),
                            backgroundColor: [
                                "#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#F67019"
                            ]
                        }]
                    }}
                    options={{ ...chartOptions, plugins: { ...chartOptions.plugins, title: { display: true, text: "Participation by Region" } } }}
                />
            );
        }

        return null;
    };

    return (
        <div className="analytics-wrapper">
            <div className="analytics-tabs">
                <button className={activeTab === "votes" ? "active" : ""} onClick={() => setActiveTab("votes")}>📊 Votes</button>
                <button className={activeTab === "turnout" ? "active" : ""} onClick={() => setActiveTab("turnout")}>🧑‍🤝‍🧑 Turnout</button>
                <button className={activeTab === "region" ? "active" : ""} onClick={() => setActiveTab("region")}>🗺️ Regions</button>
            </div>
            <div className="analytics-chart">
                {renderChart()}
            </div>
        </div>
    );
}

export default ElectionAnalytics;
