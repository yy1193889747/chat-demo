package com.oclye.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.oclye.config.WebSocketConfig;
import com.oclye.model.Greeting;
import com.oclye.model.HelloMessage;
import lombok.experimental.var;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.SimpleFormatter;

/**
 * @author ocly
 * @date 2018/2/2 15:42
 */
@RestController
public class GreetingController {

  private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");

  @Autowired
  private SimpMessagingTemplate template;

  @Autowired
  WebSocketConfig webSocketConfig;

  @GetMapping("/userlist")
  public JSONObject getUserlist(){
    JSONObject users = webSocketConfig.users;
    return users;
  }
  @MessageMapping("/hello")
  @SendTo("/topic/greetings")
  public Greeting greeting(HelloMessage message) throws Exception {
    String date = simpleDateFormat.format(new Date());
    String content = date + "【" + message.getName() + "】说：" + message.getContent();
    System.out.println(content);
    Thread.sleep(1000);
    return new Greeting(content);
  }

  @MessageMapping("/private")
  public void privatechat(HelloMessage message) throws Exception {

    String ctx = message.getContent();
    String userid = message.getName();
    String touser = message.getReceiver();

    String date = simpleDateFormat.format(new Date());
    String content =date+"【"+userid+"】对你说：" + ctx;
    String contents =date+" 你对【"+ touser +"】说："+ ctx;

    template.convertAndSendToUser(userid,"/topic/private",new Greeting(contents));
    Thread.sleep(1000);
    if("机器人".equals(touser)){
      touser = userid;
      String url = "http://www.tuling123.com/openapi/api";
      String post = "{\"key\": \"3a7b16679207452dbed9bae8a1b3dd32\",\"info\": \""+ctx+"\",\"userid\":\""+userid+"\"}";
      String body = Jsoup.connect(url).method(Connection.Method.POST)
        .requestBody(post)
        .header("Content-Type", "application/json; charset=utf-8")
        .ignoreContentType(true).execute().body();
      String text = JSONObject.parseObject(body).getString("text");
      content =date+"【机器人】对你说：" + text;
    }
    template.convertAndSendToUser(touser,"/topic/private",new Greeting(content));


  }
/*  @MessageMapping("/private")
  @SendToUser(value = "/topic/private")
  public Greeting privatechat(HelloMessage message) throws Exception {

    System.out.println(message.getName());
    Thread.sleep(1000);
    String content ="【"+message.getName()+"】对你说：" + message.getContent();
    return new Greeting(content);
  }*/

public static void main(String[] args) throws IOException {
    //{
/*  "key": "3a7b16679207452dbed9bae8a1b3dd32",
    "info": "你叫什么名字",
    "userid":"12345678"
}*/

  String c = "nihao";
  String userid = "123123";
  String url = "http://www.tuling123.com/openapi/api";
  String post = "{\"key\": \"3a7b16679207452dbed9bae8a1b3dd32\",\"info\": \""+c+"\",\"userid\":\""+userid+"\"}";
  String body = Jsoup.connect(url).method(Connection.Method.POST)
    .requestBody(post)
    .header("Content-Type", "application/json; charset=utf-8")
    .ignoreContentType(true).execute().body();

  System.out.println(JSONObject.parseObject(body).getString("text"));
  System.out.println(body);

}
}
