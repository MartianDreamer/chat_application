
import Register from "./Register.jsx";
import axios from "axios"

function App() {
  axios.defaults.baseURL = 'http://localhost:8080';
  return (
    <Register />
  )
}

export default App
