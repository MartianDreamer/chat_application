import {useState, useEffect, useContext} from "react";
import axios from 'axios';
import {UserContext} from "../../UserContext.jsx";

export default function Avatar({userId}) {
    const {token} = useContext(UserContext);
    const [avatar, setAvatar] = useState('');

    async function GetAvatar() {
        try {
            const {data} = await axios.get(`/rest/users/avatar/${userId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setAvatar(data);
        } catch (error) {
            // Handle any errors that occurred during the API call
            console.error('Error fetching avatar:', error);
        }
    }

    useEffect(() => {
        GetAvatar();
    }, []);

    return (
        <div>
            <div className={"w-8 h-8 bg-red-200 rounded-full"}></div>
            {/*<img src={avatar} alt="Avatar"/>*/}
        </div>
    );
}
