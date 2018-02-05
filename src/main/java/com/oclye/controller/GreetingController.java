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

import javax.xml.crypto.Data;
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
    System.out.println(message.getName());
    Thread.sleep(1000);
    String date = simpleDateFormat.format(new Date());
    return new Greeting(date+"【"+message.getName()+"】说：" + message.getContent() + "");
  }

  @MessageMapping("/private")
  public void privatechat(HelloMessage message) throws Exception {
    String date = simpleDateFormat.format(new Date());
    Thread.sleep(1000);
    String content =date+"【"+message.getName()+"】对你说：" + message.getContent();
    String contents =date+" 你对【"+ message.getReceiver() +"】说："+ message.getContent();
    System.out.println(message.getReceiver());
    template.convertAndSendToUser(message.getName(),"/topic/private",new Greeting(content));
    template.convertAndSendToUser(message.getReceiver(),"/topic/private",new Greeting(content));
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
