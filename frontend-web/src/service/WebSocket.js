import {over} from 'stompjs';
import SockJS from 'sockjs-client';
import {Subject } from 'rxjs';

let stompClient;
let messageObservable = new Subject();
let friendObservable = new Subject();
const onError = () => {
    alert("Failed")
}
function onConnected() {
    stompClient.subscribe('/user/queue/notification', (res) => {
        const notification = JSON.parse(res.body);
        if (notification.type === 'MESSAGE') {
            messageObservable.next(notification.content);
        } else if (notification.type === 'FRIEND_REQUEST') {
            friendObservable.next(notification.content);
        }
    })
}

export function getconnection(token, type) {
    if (stompClient) {
        if (type === 'message')
        return messageObservable;
        else if (type === 'friend')
            return friendObservable;
    }
    let Sock = new SockJS('http://localhost:8080/ws');
    stompClient = over(Sock);
    stompClient.connect({
        Authorization: `Bearer ${token}`,
    },onConnected, onError);
    if (type === 'message')
        return messageObservable;
    else if (type === 'friend')
        return friendObservable;

}