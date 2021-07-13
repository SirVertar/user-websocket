var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#userinfo").html("");
}

function connect() {
    var socket = new SockJS('/user-endpoint');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/user', function (user) {
            showGreeting(JSON.parse(user.body).id, JSON.parse(user.body).name, JSON.parse(user.body).surname, JSON.parse(user.body).address);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/user-endpoint", {}, JSON.stringify({'id' : 1,'name': $("#name").val() , 'surname' : $("#surname").val(), 'address' : $("#address").val()}));
}

function showGreeting(id, name, surname, address) {
    $("#userinfo").append("<tr><td>" + id + " " + name + " " + surname + " " + address + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});