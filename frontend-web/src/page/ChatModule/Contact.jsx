import axios from 'axios';
import {useContext, useState, useEffect} from "react";
import {UserContext} from "../../UserContext.jsx";
import {Link} from "react-router-dom";
export function ContactsShow() {
    const { token } = useContext(UserContext);
    const [contacts, setContacts] = useState([]);

    async function getContacts() {
        try {
            const { data } = await axios.get('/rest/relationships/friends', {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            console.log(data)
            const friendContact = data.content.map((e) => e.friend);
            setContacts(friendContact);
        } catch (error) {
            // Handle any errors that occurred during the API call
            console.error('Error fetching contacts:', error);
        }
    }

    useEffect(() => {
        getContacts();
    }, [token]);

    return (
        contacts
    );
}