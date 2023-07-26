let username = "";
let password = "";
let stompClient;
let currentConversationId;
let headers = {
    "Content-Type": "application/json",
};

function loadProfile() {
    fetch("http://localhost:8080/rest/users?self=true", {
        method: "GET",
        headers: headers,
    })
        .then((rsp) => rsp.json())
        .then((rsp) => {
            const $profile = $("#profile");
            $profile.append(`<p>Username: ${rsp.username}</p>`);
            $profile.append(`<p>Email: ${rsp.email}</p>`);
            $profile.append(`<p>Phone Number: ${rsp.phoneNumber}</p>`);
            $profile.append(`<p>Avatar: <img id="my-avatar" alt="" > </p>`);
            fetch(`http://localhost:8080/rest/users/avatar/${rsp.id}`, {
                method: "GET",
                headers: headers,
            })
                .then((resp) => resp.json())
                .then((resp) => {
                    $("#my-avatar").attr("src", `data:image/jpeg;base64,${resp.content}`);
                });
        });
}

function connectWS() {
    const sock = new SockJS("http://localhost:8080/ws");
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
    fetch("http://localhost:8080/rest/conversations?page=0&size=100", {
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
                        `http://localhost:8080/rest/conversations/contents/${currentConversationId}`,
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
    fetch("http://localhost:8080/rest/login", {
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
    JSON.parse(message.body).content.map((e) => {
        $("#conversation-content").append(`<p>${e.content}</p>`);
    });
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
