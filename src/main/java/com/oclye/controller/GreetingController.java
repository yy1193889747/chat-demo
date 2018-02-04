package com.oclye.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.oclye.config.WebSocketConfig;
import com.oclye.model.Greeting;
import com.oclye.model.HelloMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author ocly
 * @date 2018/2/2 15:42
 */
@RestController
public class GreetingController {

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
    System.out.println(message.getName());
    Thread.sleep(1000);
    return new Greeting("【"+message.getName()+"】：" + message.getContent() + "");
  }

  @MessageMapping("/private")
  @SendToUser("/topic/private")
  public Greeting privatechat(HelloMessage message ,@Header("simpSessionId") String sessionId) throws Exception {
    System.out.println(sessionId);
    Thread.sleep(1000);
    String content ="【"+message.getName()+"】对你说：" + message.getContent();
    String contents ="你对【"+ message.getReceiver() +"】说："+ message.getContent();
    System.out.println(message.getReceiver());
    template.convertAndSendToUser(message.getReceiver(),"/topic/private",new Greeting(content));
    return new Greeting(contents);
  }
/*  @MessageMapping("/private")
  @SendToUser(value = "/topic/private")
  public Greeting privatechat(HelloMessage message) throws Exception {

    System.out.println(message.getName());
    Thread.sleep(1000);
    String content ="【"+message.getName()+"】对你说：" + message.getContent();
    return new Greeting(content);
  }*/
}
