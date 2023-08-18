import React, {useState, useContext, useEffect} from "react"
import axios from "axios"
import {Link, useNavigate} from 'react-router-dom'
import {UserContext} from "../UserContext.jsx";

export default function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const {setUsername: setLoggedInUserName, setId, setToken} = useContext(UserContext);
    const [loginMessage, setLoginMessage] = useState('');


    async function login(ev) {
        ev.preventDefault();
        try {
            const {data} = await axios.post('/rest/login', {
                username: username,
                password: password,
            })
            if (data) {
                const {data: userInfo} = await axios.get('/rest/users?self=true', {
                    headers: {
                        "Authorization": `Bearer ${data.token}`
                    }
                })
                setLoggedInUserName(userInfo.username);
                setId(userInfo.id);
                setToken(data.token);

            }

        } catch (error) {
            setLoginMessage("Login failed. Please check your credentials and try again.");
        }
    }

    return (
        <div
            className="bg-blue-50 h-screen flex flex-col items-center justify-center w-full bg-white rounded-lg shadow dark:border md:mt-0 xl:p-0 dark:bg-gray-800 dark:border-gray-700">
            <div className={"self-center"}>
                <h2 className={"text-center font-bold text-xl mb-5"}>Login</h2>
            </div>
            <form className={"w-64 mx-auto mb-12"} onSubmit={login}>
                <input value={username}
                       onChange={ev => setUsername(ev.target.value)}
                       className={"block w-full rounded-sm p-2 mb-2 border"}
                       type="text" placeholder={"Username"}
                />
                <input value={password}
                       onChange={ev => setPassword(ev.target.value)}
                       type="password" placeholder={"Password"}
                       className={"block w-full rounded-sm p-2 mb-2 border"}
                />
                <p className="text-sm font-light text-gray-500 dark:text-gray-400">
                    Forgot password ? <a href=".#"
                                         className="font-medium text-primary-600 hover:underline dark:text-primary-500">Login
                    here</a>
                </p>
                <button className={"bg-blue-500 text-white block w-full rounded-sm p-2"}>Login</button>
            </form>
            {loginMessage && ( // Show login message if it exists
                <div className="mb-5 text-center text-green-600">{loginMessage}</div>
            )}
            <p className="text-sm font-light text-gray-500 dark:text-gray-400">
                Sign up for new account ? <Link to={"/register"}
                                                className="font-medium text-primary-600 hover:underline dark:text-primary-500">Register
                here</Link>
            </p>
        </div>
    );
}