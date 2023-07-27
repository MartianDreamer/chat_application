const backendAddress = "http://localhost:8080";
let username = "";
let password = "";
let stompClient;
let currentConversationId;
let headers = {
    "Content-Type": "application/json",
};

function renderImage(id) {
    fetch(`${backendAddress}/rest/attachments/${id}` , {
        method: "GET",
        headers: headers
    })
        .then(rsp => rsp.json())
        .then(rsp => {
            const $attachment = $(`<div id="attachment${id}"></div>`);
            $("#conversation-content").append($attachment)
            rsp.map(e => {
                $attachment.append(`<img alt="" src="data:image/jpeg;base64,${e.content}" style="max-height: 200px">`)
            })
        })
}

function renderProfile(profile) {
    const $profile = $("#profile");
    $profile.append(`<div id="${profile.id}" style="margin: 10px"><img id="my-avatar${profile.id}" alt="" style="max-height: 200px"><p id="online-status${profile.id}">${profile.online ? "Online" : "Offline"}</p><p>Username: ${profile.username}</p><p>Email: ${profile.email}</p><p>Phone Number: ${profile.phoneNumber}</p></div>`);
    fetch(`${backendAddress}/rest/users/avatar/${profile.id}`, {
        method: "GET",
        headers: headers,
    })
        .then((resp) => resp.json())
        .then((resp) => {
            $(`#my-avatar${profile.id}`).attr("src", `data:image/jpeg;base64,${resp.content}`);
        });
}

function loadProfile() {
    fetch(`${backendAddress}/rest/users?self=true`, {
        method: "GET",
        headers: headers,
    })
        .then((rsp) => rsp.json())
        .then((rsp) => {
            rsp.online = true;
            renderProfile(rsp);
        });
}

function connectWS() {
    const sock = new SockJS(`${backendAddress}/ws`);
    stompClient = StompJs.Stomp.over(sock);
    stompClient.connect(
        {
            Authorization: headers.Authorization,
        },
        onConnect,
        onError
    );
}

function getConversation() {
    fetch(`${backendAddress}/rest/conversations?page=0&size=100`, {
        method: "GET",
        headers: headers,
    })
        .then((rsp) => rsp.json())
        .then((body) => {
            body.content.forEach((e) => {
                const conversation = $(`<p id="${e.id}">${e.name}</p>`);
                conversation.on("click", (c) => {
                    if (c.target.id === currentConversationId) {
                        return;
                    }
                    currentConversationId = c.target.id;
                    fetch(
                        `${backendAddress}/rest/conversations/contents/${currentConversationId}`,
                        {
                            method: "GET",
                            headers: headers,
                        }
                    )
                        .then((rsp) => rsp.json())
                        .then((body) => {
                            $("#conversation-content").empty();
                            body.forEach((e) => {
                                if (e.type === "MESSAGE") {
                                    $("#conversation-content").append(`<p>${e.dto.content}</p>`);
                                } else {
                                    renderImage(e.dto.id)
                                }
                            });
                        });
                });
                $("#conversation-list").append(conversation);
            });
        });
}

function onConnect(frame) {
    console.log("Connected: " + frame);
    stompClient.subscribe("/topic/announcement", onReceivedPublic);
    stompClient.subscribe("/user/queue/announcement", onReceivedPrivate);
    stompClient.subscribe("/user/queue/notification", onNotification);
}

function onError(e) {
    console.debug(e);
}

function connect(e) {
    e.preventDefault();
    fetch(`${backendAddress}/rest/login`, {
        method: "POST",
        headers: headers,
        body: JSON.stringify({
            username: username,
            password: password,
        }),
    })
        .then((rsp) => rsp.json())
        .then((body) => {
            headers.Authorization = `Bearer ${body.token}`;
            $("#chat-interface").removeAttr("hidden");
            $("#input-block").attr("hidden", true);
            connectWS();
            getConversation();
            loadProfile();
        });
}

function onReceivedPublic(message) {
    $("#public-announcement-box").append(
        `<p>${JSON.parse(message.body).content}</p>`
    );
}

function onReceivedPrivate(message) {
    $("#private-announcement-box").append(
        `<p>${JSON.parse(message.body).content}</p>`
    );
}

function onNotification(message) {
    const notification = JSON.parse(message.body);
    if (notification.type === "MESSAGE") {
        $("#conversation-content").append(`<p>${notification.content.content}</p>`);
    } else if (notification.type === "ONLINE_STATUS_CHANGE") {
        $(`#${notification.content.id}`).remove();
        if (notification.content.online) {
            renderProfile(notification.content);
        }
    } else if (notification.type === "ATTACHMENT") {
        renderImage(notification.content.id);
    }
}

function disconnect(e) {
    e.preventDefault();
    stompClient.disconnect(() => {
        $("#chat-interface").attr("hidden", true);
        $("#input-block").removeAttr("hidden");
        $("#conversation-content").empty();
        $("#conversation-list").empty();
        $("#profile").empty();
    }, {});
}

function send(e) {
    e.preventDefault();
    const $chatContent = $("#chat-content");
    const message = $chatContent.val();
    $chatContent.val("");
    stompClient.send(`/app/conversations/${currentConversationId}`, {}, message);
}

function clear(e) {
    e.preventDefault();
    $("#public-announcement-box").empty();
    $("#private-announcement-box").empty();
}

$("#connect-btn").on("click", connect);
$("#disconnect-btn").on("click", disconnect);
$("#clear-btn").on("click", clear);
$("#username-input").on("change", (e) => {
    username = e.target.value;
});

$("#password-input").on("change", (e) => {
    password = e.target.value;
});

$("#send-button").on("click", send);
