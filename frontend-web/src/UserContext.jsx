import {useState, createContext} from "react";

export const UserContext = createContext({})

export function UserContextProvider({children}) {
    const [userName,setUsername] = useState(null);
    const [id, setId] = useState(null);
    const [token, setToken] =useState(null)
    return (
        <UserContext.Provider value = {{userName, setUsername, id, setId, token, setToken}}>
            {children}
        </UserContext.Provider>
    )
}