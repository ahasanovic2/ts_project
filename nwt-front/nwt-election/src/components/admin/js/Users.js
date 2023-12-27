import React, { useState, useEffect } from "react";
import axios from "axios";
import '../css/Users.css';
import { useHistory } from "react-router-dom";
import Header from "./Header";
import {checkExpiration, useHandleLogout} from "../../HelpFunctions";

const Users = () => {
    
    const [users, setUsers] = useState([]); // State to store admin data
    const handleLogout = useHandleLogout();

    const handleDelete = async (email) => {
        checkExpiration(localStorage.getItem('access_token'),handleLogout);
        if (localStorage.getItem('access_token')) {
            try {
                const isConfirmed = window.confirm("Are you sure you want to delete this users?");
                if (!isConfirmed) {
                    return; // Do nothing if the user cancels
                }

                const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://10.0.0.155:8080';
                const token = localStorage.getItem('access_token');

                const response = await fetch(`${BASE_URL}/users/delete-user?email=${email}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    }
                });
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                setUsers(currentAdmins => currentAdmins.filter(users => users.email !== email));
            }
            catch (error) {
                console.error("Error fetching admin data:", error);
            }
        }
    };

    useEffect(() => {
        // Function to fetch admin data
        const fetchAdmins = async () => {
            try {
                const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://10.0.0.155:8080';
                const token = localStorage.getItem('access_token');
        
                const response = await fetch(`${BASE_URL}/users`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    }
                });
        
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
        
                const data = await response.json();

                setUsers(data); // Set the admin data in state
            } catch (error) {
                console.error("Error fetching admin data:", error);
                // Handle error scenarios here
            }
        };
        

        fetchAdmins(); // Fetch admin data on component mount
    }, []); // Empty dependency array means this effect runs once on mount


    return (
        <div className="admins">
            <Header/>
            <div className="adminsTable">
                <table className="admins-table">
                    <thead>
                        <tr>
                            <th>First Name</th>
                            <th>Last Name</th>
                            <th>Email</th>
                            <th>Options</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map((user, index) => (
                            <tr key={index}>
                                <td>{user.firstname}</td>
                                <td>{user.lastname}</td>
                                <td>{user.email}</td>
                                <td className="delete-button">
                                    <button className="delete-admin" onClick={() => handleDelete(user.email)}>
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Users;