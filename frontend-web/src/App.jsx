import Register from "./Register.jsx";
import Login from "./Login.jsx"
import axios from "axios"
import {Switch, Route} from "react-router-dom";
import {React} from "react";

function App() {
    axios.defaults.baseURL = 'http://127.0.0.1:8080';
    axios.defaults.withCredentials = false;
    axios.defaults.headers.put['Content-Type'] = 'application/json';
    return (
        <div className="App">
            <Switch>
                <Route exact path={["", "/register"]} component={Register}></Route>
                <Route path={"/login"} render={() => <Login/>}></Route>
            </Switch>
        </div>
    )
}

export default App;
