let username = "";
let password = "";
let stompClient;

const onConnect = (frame) => {
    console.log("Connected: " + frame);
    stompClient.subscribe("/topic/announcement", onReceivedPublic);
    stompClient.subscribe(`/user/queue/announcement`, onReceivedPrivate)
};

const onError = (e) => {
    console.debug(e);
};

function connect(e) {
    e.preventDefault();
    fetch("http://localhost:8080/rest/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            username: username,
            password: password,
        }),
    })
        .then(rsp => rsp.json())
        .then((body) => {
            $("#chat-interface").removeAttr("hidden");
            $("#input-block").attr("hidden", true);
            const sock = new SockJS("http://localhost:8080/ws");
            stompClient = StompJs.Stomp.over(sock);
            stompClient.connect(
                {
                    Authorization: `Bearer ${body.token}`,
                },
                onConnect,
                onError
            );
        });
}

function onReceivedPublic(message) {
    $("#public-announcement-box").append(`<p>${JSON.parse(message.body).content}</p>`);
}

function onReceivedPrivate(message) {
    $("#private-announcement-box").append(`<p>${JSON.parse(message.body).content}</p>`);
}

function disconnect(e) {
    e.preventDefault();
    stompClient.disconnect(() => {
        $("#chat-interface").attr("hidden", true);
        $("#input-block").removeAttr("hidden");
    }, {});
}

function clear(e) {
    e.preventDefault();
    $("#public-announcement-box").empty();
    $("#private-announcement-box").empty();
}

$("#connect-btn").on("click", connect);
$("#disconnect-btn").on("click", disconnect);
$("#clear-btn").on("click", clear)
$("#username-input").on("change", (e) => {
    username = e.target.value;
});

$("#password-input").on("change", (e) => {
    password = e.target.value;
});
