import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import Login from "../components/Login";
import ElectionsPage from "../components/ElectionsPage";
import ElectionDetails from "../components/ElectionDetails";
import { VoterProvider, VoterContext } from '../context/VoterContext';
import { useContext } from 'react';

function AppRoutes() {
    const { isLoggedIn } = useContext(VoterContext);

    return (
        <Routes>
            <Route path="/" element={isLoggedIn ? <Navigate to="/elections" replace /> : <Login />} />
            <Route path="/elections" element={isLoggedIn ? <ElectionsPage /> : <Navigate to="/" replace />} />
            <Route path="/election/:electionId" element={isLoggedIn ? <ElectionDetails /> : <Navigate to="/" replace />} />
        </Routes>
    );
}

function App() {
    return (
        <VoterProvider>
            <Router>
                <AppRoutes />
            </Router>
        </VoterProvider>
    );
}

export default App;
