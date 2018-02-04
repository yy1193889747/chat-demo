/**
 * Created by Administrator on 2018/2/2.
 */
var stompClient = null;
var sessionId = null;
function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  $("#send").prop("disabled", !connected);
  $("#username").prop("disabled", connected);
  if (!connected) {
    $("#lobby").html("");
  }
}

function login() {
  var name = $("#username").val();
  if(name.trim() ===''){
    $("#username").val('!!name not null');
    return;
  }

  $.ajax({
    type: "POST",
    url: "/login",
    data: {username:$("#username").val()},
    success: function(data){
      connect();
    }
  });
}

function connect() {
  var socket = new SockJS('/gs-guide-websocket');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    setConnected(true);
    sessionId = /\/([^\/]+)\/websocket/.exec(socket._transport.url)[1];
    showUser($("#username").val(),sessionId);
    console.log("connected, session id: " + sessionId);
    console.log('Connected: ' + frame);
    console.log($("#user").html());
    stompClient.subscribe('/topic/greetings', function (greeting) {
      showGreeting(JSON.parse(greeting.body).content);
    });
    stompClient.subscribe('/user/topic/private', function (greeting) {
      showMessage(JSON.parse(greeting.body).content);
    });
    stompClient.subscribe('/topic/userlist', function (greeting) {
      var parse = JSON.parse(greeting.body);
      if(parse.online){
        showUser(parse.name,parse.id);
      }else{
        removeUser(parse.name,parse.id);
      }
    });
  });
}

function disconnect() {
  if (stompClient !== null) {
    stompClient.disconnect();
  }
  removeUser($("#username").val(),sessionId);
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
    stompClient.send("/app/private", {}, JSON.stringify({'name': $("#username").val(),'content': $("#content").val(),'receiver': $("#touser").val()}));
  }else{
  stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#username").val(),'content': $("#content").val()}));
  $("#content").val('');
  }
}
function touser(message) {
  $("#touser").val(message.textContent);
}

function showGreeting(message) {
  $("#lobby").prepend("<tr><td>" + message + "</td></tr>");
}
function showMessage(message) {
  $("#private").prepend("<tr><td>" + message + "</td></tr>");
}
function showUser(user,id) {
  $("#user").prepend("<tr id="+id+" onclick='javascript:touser(this)'><td>" + user + "</td></tr>");
}
function removeUser(user,id) {
  console.log($("#user").html());
  $("tr").remove("#"+id);
}

$(function () {
  $.ajax({
    type: "GET",
    url: "/userlist",
    dataType: "json",
    success: function (json) {
      for (var p in json) {//遍历json对象的每个key/value对,p为key
        showUser(json[p], p);
      }
    }
  });
  $("form").on('submit', function (e) {
    e.preventDefault();
  });
  $( "#connect" ).click(function() { login(); });
  $( "#disconnect" ).click(function() { disconnect(); });
  $( "#send" ).click(function() { sendName(); });
  $( "#lobbys" ).click(function() {  $("#touser").val(''); });
});
