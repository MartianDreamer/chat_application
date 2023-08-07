import Register from "../page/Register.jsx";
import Login from "../page/Login.jsx";
import {Routes, Route, useNavigate} from "react-router-dom";
import Chat from "../page/ChatModule/Chat.jsx";
import {UserContext} from "../UserContext.jsx";
import {useEffect, useContext} from "react"

export default function Router({children }) {
    const {userName, id, token } = useContext(UserContext);
    const navigate = useNavigate();

    useEffect(() => {
        if(token)
            navigate("/chat");
        else navigate("/login");
    },[token])
    // } else 
    //     return <Login/>;
    return (
        <Routes>
            <Route exact path={"/"} element = {<Register />}/>
            <Route exact path={"/register"} element = {<Register />}/>
            <Route path="/login" element={<Login/>}/>
            <Route path="/chat" element={<Chat/>}/>
            <Route
                path="/chat/:id/"
                render={(props) => <Chat {...props} conversationId = {id} />}
            ></Route>
        </Routes>
    )
}
