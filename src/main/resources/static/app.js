/**
 * Created by Administrator on 2018/2/2.
 */
var stompClient = null;
var sessionId = null;
function setConnected(connected) {
  $("#connect").html('连接');
  if (connected) {
    $("#connect").html('成功');
  }
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  $("#send").prop("disabled", !connected);
  $("#sendtouser").prop("disabled", !connected);
  $("#username").prop("disabled", connected);

}

function login() {
  var name = $("#username").val().trim();
  if (name === '') {
    $("#username").val('用户名不能为空');
    return;
  }

  $.ajax({
    type: "POST",
    url: "/login",
    data: {username: name},
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
   // showUser($("#username").val(), sessionId);
    stompClient.subscribe('/topic/greetings', function (greeting) {
      showGreeting(JSON.parse(greeting.body).content);
    });
    stompClient.subscribe('/user/topic/private', function (greeting) {
      var parse = JSON.parse(greeting.body);
      showMessage(parse.content, parse.name);
    });
    stompClient.subscribe('/topic/userlist', function (greeting) {
      var parse = JSON.parse(greeting.body);
      if (parse.online) {
        showUser(parse.name, parse.id);
      } else {
        removeUser(parse.id);
      }
    });
  });
}

function disconnect() {
  if (stompClient !== null) {
    stompClient.disconnect();
  }
  //removeUser(sessionId);
  setConnected(false);
  console.log("Disconnected");
}

function sendName() {
  var content = $("#content").val();
  if (content.trim() === '') {
    return;
  }
  stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#username").val(), 'content': content}));
  $("#content").val('');
}
function sendToUser() {
  var touser = $("#privateuser").html();
  var patt1 = new RegExp(/【(.*?)】/g);
  var tousername = patt1.exec($("#privateuser").html())[1];
  var content = $("#contentuser").val();
  if (content.trim() === '') {
    return;
  }
  if (touser.trim() !== '私信聊天') {
    stompClient.send("/app/private", {}, JSON.stringify({'name': $("#username").val(), 'content': content, 'receiver': tousername}));
    $("#contentuser").val('');
  }
}
function touser(message) {
  $("#" + message.id + " span").html('');
  if ($("#privateuser").html() === '私信聊天') {
    $("#privateuser").html("私信聊天 与 【" + message.textContent + "】");
    $(".msg-" + message.textContent).prop("hidden", false);
    return;
  }
  var patt1 = new RegExp(/【(.*?)】/g);
  var tousername = patt1.exec($("#privateuser").html())[1];
  if(message.class===tousername){
    return;
  }
  $(".msg-" + tousername).prop("hidden", true);
  $("#privateuser").html("私信聊天 与 【" + message.textContent + "】");
  $(".msg-" + message.textContent).prop("hidden", false);
}

function hidetlobby(){
  if($("#lobby").is(":hidden")){
    $("#lobby").show();
    $("#conversation +div").show();
  }else{
    $("#lobby").hide();
    $("#conversation +div").hide();
  }
}
function hidetprivate(){
  if($("#private").is(":hidden")){
    $("#private").show();
    $("#privatechat +div").show();
  }else{
    $("#private").hide();
    $("#privatechat +div").hide();
  }
}
function hidetuser(){
  if($("#user").is(":hidden")){
    $("#user").show();
  }else{
    $("#user").hide();
  }
}
function showGreeting(message) {
  $("#lobby").append("<tr><td>" + message + "</td></tr>");
  var div = document.getElementById('lobby');
  div.scrollTop = div.scrollHeight;
}
function showMessage(message, touser) {
  var patt1 = new RegExp(/【(.*?)】/g);
  var tousername='';
  if($("#privateuser").html()!=='私信聊天'){
    tousername = patt1.exec($("#privateuser").html())[1];
  }
  if (touser === $("#username").val()) {
    $("#private").append("<tr class='msg-" + touser + "'><td>" + message + "</td></tr>");
  }
  if (touser === tousername) {
    $("#private").append("<tr class='msg-" + touser + "'><td>" + message + "</td></tr>");
  } else {
    var i = $("." + touser + " span").html();
    if (i === '') {
      i = 0;
    }
    $("." + touser + " span").html(++i);
    $("#private").append("<tr class='msg-" + touser + "' hidden><td>" + message + "</td></tr>");
  }

  var div = document.getElementById('private');
  div.scrollTop = div.scrollHeight;
}
function showUser(user, id) {
  $("#user").append("<tr id='" + id + "' onclick='javascript:touser(this)' class='" + user + "'><td>" + user + "<span class='badge pull-right'></span></td></tr>");
}
function removeUser(id) {
  $("tr").remove("#" + id);
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
  $("#connect").click(function () {
    login();
  });
  $("#disconnect").click(function () {
    disconnect();
  });
  $("#send").click(function () {
    sendName();
  });
  $("#sendtouser").click(function () {
    sendToUser();
  });
});
$(document).ready(function () {
  var div = document.getElementById('lobby');
  div.scrollTop = div.scrollHeight;
});
