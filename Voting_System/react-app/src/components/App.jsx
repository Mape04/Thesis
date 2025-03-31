import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import Login from "../components/Login";
import ElectionsPage from "../components/ElectionsPage";
import ElectionDetails from "../components/ElectionDetails"; // Import new component

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    const handleLoginSuccess = () => {
        setIsLoggedIn(true);
    };

    return (
        <Router>
            <Routes>
                <Route path="/" element={isLoggedIn ? <Navigate to="/elections" replace /> : <Login onLoginSuccess={handleLoginSuccess} />} />
                <Route path="/elections" element={isLoggedIn ? <ElectionsPage /> : <Navigate to="/" replace />} />
                <Route path="/election/:electionId" element={isLoggedIn ? <ElectionDetails /> : <Navigate to="/" replace />} />
            </Routes>
        </Router>
    );
}

export default App;
