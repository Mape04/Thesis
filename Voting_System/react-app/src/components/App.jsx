import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import Login from "../components/Login";
import Registration from "../components/Register";
import ElectionsPage from "../components/ElectionsPage";
import ElectionDetails from "../components/ElectionDetails";
import LandingPage from "./LandingPage.jsx";
import { VoterProvider, VoterContext } from '../context/VoterContext';
import { useContext } from 'react';
import Profile from "./Profile.jsx";

function AppRoutes() {
    const { isLoggedIn } = useContext(VoterContext);

    return (
        <Routes>
            <Route path="/" element={<LandingPage />}/>
            <Route path="/login" element={isLoggedIn ? <Navigate to="/elections" replace /> : <Login />} />
            <Route path="/register" element={<Registration />} />
            <Route path="/elections" element={isLoggedIn ? <ElectionsPage /> : <Navigate to="/" replace />} />
            <Route path="/election/:electionId" element={isLoggedIn ? <ElectionDetails /> : <Navigate to="/" replace />} />
            <Route path="/profile" element={isLoggedIn ? <Profile /> : <Navigate  to="/profile" replace /> }/>
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
