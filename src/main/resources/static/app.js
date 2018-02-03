/**
 * Created by Administrator on 2018/2/2.
 */
var stompClient = null;

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  $("#send").prop("disabled", !connected);
  if (!connected) {
    $("#lobby").html("");
  }
}



function connect() {
  var socket = new SockJS('/gs-guide-websocket');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    setConnected(true);
    console.log('Connected: ' + frame);
    console.log(frame.toString());
    console.log(frame._generateSessionId);
    stompClient.subscribe('/topic/greetings', function (greeting) {
      showGreeting(JSON.parse(greeting.body).content);
    });
    stompClient.subscribe('/user/topic/private', function (greeting) {
      showMessage(JSON.parse(greeting.body).content);
    });
    stompClient.subscribe('/topic/userlist', function (greeting) {
      showUser(JSON.parse(greeting.body).name);
    });
  });
}



function disconnect() {
  if (stompClient !== null) {
    stompClient.disconnect();
  }
  stompClient.send("/app/userlist", {});
  setConnected(false);
  console.log("Disconnected");
}

function sendName() {
  var context = $("#content").val();
  var touser = $("#touser").val();
  if(context.trim()===''){
    return
  }
  if(touser.trim()!==''){
    console.log("发到私人")
    stompClient.send("/app/private", {}, JSON.stringify({'name': $("#name").val(),'content': $("#content").val(),'receiver': $("#touser").val()}));
  }else{
    console.log("发到大厅")
  stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val(),'content': $("#content").val()}));
  $("#content").val('');
  }
}

function showGreeting(message) {
  $("#lobby").prepend("<tr><td>" + message + "</td></tr>");
}
function showMessage(message) {
  $("#private").prepend("<tr><td>" + message + "</td></tr>");
}
function showUser(user) {
  $("#user").prepend("<tr><td>" + user + "</td></tr>");
}

$(function () {
  $("form").on('submit', function (e) {
    e.preventDefault();
  });
  $( "#connect" ).click(function() { connect(); });
  $( "#disconnect" ).click(function() { disconnect(); });
  $( "#send" ).click(function() { sendName(); });
});
