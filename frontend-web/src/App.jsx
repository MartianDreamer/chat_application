import axios from "axios"
import {React} from "react";
import Router from "./service/Router.jsx";
import {UserContextProvider} from "./UserContext";


function App() {
    axios.defaults.baseURL = 'http://127.0.0.1:8080';
    axios.defaults.withCredentials = false;
    axios.defaults.headers.post['Content-Type'] = 'application/json';
    axios.defaults.headers.put['Content-Type'] = 'application/json';

    return (
        <UserContextProvider>
        <Router/>
        </UserContextProvider>
    )
}

export default App;
