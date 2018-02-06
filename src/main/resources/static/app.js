/**
 * Created by Administrator on 2018/2/2.
 */
var stompClient = null;
var sessionId = null;
function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  $("#send").prop("disabled", !connected);
  $("#sendtouser").prop("disabled", !connected);
  $("#username").prop("disabled", connected);
  if (!connected) {
    $("#lobby").html("");
  }
}

function login() {
  var name = $("#username").val();
  if(name.trim() ===''){
    $("#username").val('用户名不能为空');
    return;
  }

  $.ajax({
    type: "POST",
    url: "/login",
    data: {username:$("#username").val()},
    success: function () {
      connect();
    },
    error: function () {
      $("#username").val('重名了');
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
        removeUser(parse.id);
      }
    });
  });
}

function disconnect() {
  if (stompClient !== null) {
    stompClient.disconnect();
  }
  removeUser(sessionId);
  setConnected(false);
  console.log("Disconnected");
}

function sendName() {
  var content = $("#content").val();
  if(content.trim()===''){
    return;
  }
  stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#username").val(),'content': content}));
  $("#content").val('');
}
function sendToUser() {
  var touser = $("#privateuser").html();
  var patt1 = new RegExp(/【(.*?)】/g);
  var tousername = patt1.exec($("#privateuser").html())[1];
  var content = $("#contentuser").val();
  if(content.trim()===''){
    return;
  }
  if (touser.trim() !== '私信聊天') {
    stompClient.send("/app/private", {}, JSON.stringify({'name': $("#username").val(), 'content': content, 'receiver': tousername}));
    $("#contentuser").val('');
  }
}
function touser(message) {
  $("#privateuser").html("私信聊天 与 【" + message.textContent + "】");
}

function showGreeting(message) {
  $("#lobby").append("<tr><td>" + message + "</td></tr>");
  var div = document.getElementById('lobby');
  div.scrollTop = div.scrollHeight;
}
function showMessage(message) {
  $("#private").append("<tr><td>" + message + "</td></tr>");
  var div = document.getElementById('private');
  div.scrollTop = div.scrollHeight;
}
function showUser(user,id) {
  $("#user").append("<tr id="+id+" onclick='javascript:touser(this)'><td>" + user +"</td></tr>");
}
function removeUser(id) {
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
  $( "#sendtouser" ).click(function() { sendToUser(); });
});
$(document).ready(function(){
  var div = document.getElementById('lobby');
  div.scrollTop = div.scrollHeight;
});
