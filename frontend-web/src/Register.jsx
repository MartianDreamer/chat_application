import {useState, useContext} from "react"
import axios from "axios"
import {UserContext} from "./UserContext.jsx";
import {Link} from "react-router-dom"

export default function Register() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [phone, setPhone] = useState('');
    const context = useContext(UserContext);

    async function register(ev) {
        ev.preventDefault();
        await axios.put('/rest/users', {
            username: username,
            password: password,
            email: email,
            phoneNumber: phone
        });
    }

    return (
        <div className="bg-blue-50 h-screen flex flex-col items-center justify-center w-full bg-white rounded-lg shadow dark:border md:mt-0 xl:p-0 dark:bg-gray-800 dark:border-gray-700">
            <div className={"self-center"}>
                <h4 className={"text-center font-bold text-xl"}>Register</h4>
                <p className={"mb-5 text-sm"}>Please enter details to register</p>
            </div>
            <form className={"w-64 mx-auto mb-12"} onSubmit={register}>
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
                <input value={email}
                       onChange={ev => setEmail(ev.target.value)}
                       type="email" placeholder={"Email"}
                       className={"block w-full rounded-sm p-2 mb-2 border"}
                />
                <input value={phone}
                       onChange={ev => setPhone(ev.target.value)}
                       type="phone" placeholder={"Phone Number"}
                       className={"block w-full rounded-sm p-2 mb-2 border"}
                />
                <button className={"bg-blue-500 text-white block w-full rounded-sm p-2"}>Register</button>
            </form>
            <div className="flex items-start">
                <div className="flex items-center h-5">
                    <input id="terms" aria-describedby="terms" type="checkbox"
                           className="w-4 h-4 border border-gray-300 rounded bg-gray-50 focus:ring-3 focus:ring-primary-300 dark:bg-gray-700 dark:border-gray-600 dark:focus:ring-primary-600 dark:ring-offset-gray-800"
                           required=""/>
                </div>
                <div className="ml-3 text-sm">
                    <label htmlFor="terms" className="font-light text-gray-500 dark:text-gray-300">I accept the <a
                        className="font-medium text-primary-600 hover:underline dark:text-primary-500" href="#">Terms
                        and Conditions</a></label>
                </div>
            </div>
            <p className="text-sm font-light text-gray-500 dark:text-gray-400">
                Already have an account? <Link to={"/login"}
                                               className="font-medium text-primary-600 hover:underline dark:text-primary-500">Login
                here</Link>
            </p>
        </div>
);
}