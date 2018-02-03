package com.oclye.controller;

import com.oclye.model.Greeting;
import com.oclye.model.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * @author ocly
 * @date 2018/2/2 15:42
 */
@RestController
public class GreetingController {


  @MessageMapping("/hello")
  @SendTo("/topic/greetings")
  public Greeting greeting(HelloMessage message) throws Exception {
    System.out.println(message.getName());
    Thread.sleep(1000);
    return new Greeting("【"+message.getName()+"】：" + message.getContent() + "");
  }

}
