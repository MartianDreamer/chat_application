
import Register from "./Register.jsx";
import axios from "axios"

function App() {
  axios.defaults.baseURL = 'http://127.0.0.1:8080';
  axios.defaults.withCredentials = false;
  axios.defaults.headers.put['Content-Type'] = 'application/json';
  return (
    <Register />
  )
}

export default App
