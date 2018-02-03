package com.oclye.controller;

import com.oclye.config.WebSocketConfig;
import com.oclye.model.Greeting;
import com.oclye.model.HelloMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

import java.util.Map;

/**
 * @author ocly
 * @date 2018/2/2 15:42
 */
@RestController
public class GreetingController {

  private SimpMessagingTemplate template;

  @Autowired
  WebSocketConfig webSocketConfig;

  @Autowired
  public GreetingController(SimpMessagingTemplate template) {
    this.template = template;
  }

  @MessageMapping("/hello")
  @SendTo("/topic/greetings")
  public Greeting greeting(HelloMessage message) throws Exception {
    System.out.println(message.getName());
    Thread.sleep(1000);
    return new Greeting("【"+message.getName()+"】：" + message.getContent() + "");
  }

  @MessageMapping("/userlist")
  @SendTo("/topic/userlist")
  public Greeting userList() throws Exception {
    Thread.sleep(1000);
    System.out.println("userlist");
    Map<String, String> users = webSocketConfig.users;
    System.out.println(users.toString());
    return new Greeting(users.toString());
  }

  @MessageMapping("/private")
  @SendToUser("/topic/private")
  public Greeting privatechat(HelloMessage message) throws Exception {
    System.out.println(message.getName());
    Thread.sleep(1000);
    String content ="【"+message.getName()+"】对你说：" + message.getContent();
    return new Greeting(content);
  }
}
