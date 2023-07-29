
import {useState} from "react"
import axios from "axios"

export default function Register() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [phone, setPhone] = useState('');


    async function register(ev){
        ev.preventDefault();
        await axios.put('/rest/users', {
            username: username,
            password: password,
            email: email,
            phoneNumber: phone
        });
    }

    return (
        <div className="bg-blue-50 h-screen flex items-center">
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
                       type="text" placeholder={"Email"}
                       className={"block w-full rounded-sm p-2 mb-2 border"}
                />
                <input value={phone}
                       onChange={ev => setPhone(ev.target.value)}
                       type="text" placeholder={"Phone Number"}
                       className={"block w-full rounded-sm p-2 mb-2 border"}
                />
                <button className={"bg-blue-500 text-white block w-full rounded-sm p-2"}>Register</button>
            </form>
        </div>
    );
}