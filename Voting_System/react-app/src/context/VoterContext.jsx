import React, { createContext, useState, useEffect } from 'react';

export const VoterContext = createContext();

export const VoterProvider = ({ children }) => {
    const [voterId, setVoterId] = useState(localStorage.getItem('voterId') || null);
    const [voterToken, setVoterToken] = useState(localStorage.getItem('voterToken') || null);
    const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('voterId'));

    useEffect(() => {
        if (voterId && voterToken) {
            localStorage.setItem('voterId', voterId);
            localStorage.setItem('voterToken', voterToken);
            setIsLoggedIn(true);
        } else {
            localStorage.removeItem('voterId');
            localStorage.removeItem('voterToken');
            setIsLoggedIn(false);
        }
    }, [voterId, voterToken]);

    const logout = () => {
        setVoterId(null);
        setVoterToken(null);
    };

    return (
        <VoterContext.Provider value={{ voterId, voterToken, setVoterId, setVoterToken, isLoggedIn, setIsLoggedIn, logout }}>
            {children}
        </VoterContext.Provider>
    );
};
