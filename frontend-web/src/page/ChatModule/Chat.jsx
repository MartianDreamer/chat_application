import React, {useEffect, useState, useContext, useCallback} from "react"
import {UserContext} from "../../UserContext.jsx";
import {over} from 'stompjs';
import SockJS from 'sockjs-client';
import {getconnection} from "../../service/WebSocket.js";
import axios from "axios";
import {ContactsShow} from "./Contact";
import Avatar from "./Avatar";

var stompClient = null;
const host = "http://localhost:8080/ws/"
export default function Chat() {
    const {token} = useContext(UserContext);
    const [messages, setMessages] = useState([]);
    let contacts = ContactsShow();

// function onConnected() {
//     stompClient.subscribe('/user/queue/notification', (res) => {
//         const notification = JSON.parse(res.body);
//         if (notification.type === 'MESSAGE') {
//             const newMessages = [...messages, notification.content];
//             setMessages(newMessages);
//         }
//     })
// }
//
//
// function onError(){
//     console.log("WS connected not successfully")
// }
// const connect =()=>{
//     let Sock = new SockJS('http://localhost:8080/ws');
//     stompClient = over(Sock);
//     stompClient.connect({
//         Authorization: `Bearer ${token}`,
//     },onConnected, onError);
// }
    useEffect(() => {
        // connect();
        getconnection(token, "message").subscribe(e => {
            setMessages([...messages, e]);
        });
    }, []);

    return (
        <div className={"flex h-screen"}>
            <div className={"bg-white-100 w-1/3 pl-4 pt-4"}>

                <div className={"text-blue-600 font-bold flex gap-2 mb-4"}>
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth="1.5"
                         stroke="currentColor" className="w-6 h-6">
                        <path strokeLinecap="round" strokeLinejoin="round"
                              d="M20.25 8.511c.884.284 1.5 1.128 1.5 2.097v4.286c0 1.136-.847 2.1-1.98 2.193-.34.027-.68.052-1.02.072v3.091l-3-3c-1.354 0-2.694-.055-4.02-.163a2.115 2.115 0 01-.825-.242m9.345-8.334a2.126 2.126 0 00-.476-.095 48.64 48.64 0 00-8.048 0c-1.131.094-1.976 1.057-1.976 2.192v4.286c0 .837.46 1.58 1.155 1.951m9.345-8.334V6.637c0-1.621-1.152-3.026-2.76-3.235A48.455 48.455 0 0011.25 3c-2.115 0-4.198.137-6.24.402-1.608.209-2.76 1.614-2.76 3.235v6.226c0 1.621 1.152 3.026 2.76 3.235.577.075 1.157.14 1.74.194V21l4.155-4.155"/>
                    </svg>
                    Free chat application
                </div>
                <div >
                    {contacts.map((e) => (
                        <div key={e.id} className={"border-b border-gray-100 py-2 flex items-center gap-2"}>
                            <Avatar userId={e.id}/>
                            <span >{e.username}</span>
                        </div>
                    ))}
                </div>
            </div>
            <div className={"flex flex-col bg-blue-50 w-2/3 mx-2"}>
                <div className={"flex-grow"}>
                    ${messages.map(e => <p key={e.id}>{e.content}</p>)}
                </div>
                <div className={"flex gap-2"}>
                    <input type="text"
                           className={"bg-white flex-grow border rounded-md p-2"}
                           placeholder={"Type message here"}/>
                    <button className={"bg-blue-500 p-2 text-white rounded-md"}>
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth="1.5"
                             stroke="currentColor" className="w-6 h-6">
                            <path strokeLinecap="round" strokeLinejoin="round"
                                  d="M6 12L3.269 3.126A59.768 59.768 0 0121.485 12 59.77 59.77 0 013.27 20.876L5.999 12zm0 0h7.5"/>
                        </svg>
                    </button>
                </div>
            </div>
        </div>
    )
}