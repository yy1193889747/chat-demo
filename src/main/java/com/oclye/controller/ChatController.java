package com.oclye.controller;

import com.alibaba.fastjson.JSONObject;
import com.oclye.config.WebSocketConfig;
import com.oclye.model.ChatMessage;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ocly
 * @date 2018/2/2 15:42
 */
@RestController
public class ChatController {

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
  public ChatMessage greeting(ChatMessage message) throws Exception {
    String date = simpleDateFormat.format(new Date());
    String content = date + "【" + message.getName() + "】说：" + message.getContent();
    message.setDate(date);
    message.setContent(content);
    Thread.sleep(1000);
    return message;
  }

  @MessageMapping("/private")
  public void privatechat(ChatMessage message) throws Exception {

    String ctx = message.getContent();
    String userid = message.getName();
    String touser = message.getReceiver();

    String date = simpleDateFormat.format(new Date());
    String content =date+"【"+userid+"】对你说：" + ctx;
    String contents =date+" 你对【"+ touser +"】说："+ ctx;

    template.convertAndSendToUser(userid,"/topic/private",new ChatMessage(touser,contents,touser,date));
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
      template.convertAndSendToUser(touser,"/topic/private",new ChatMessage("机器人",content,"机器人",date));
      return;
    }
    if("情感分析".equals(touser)){
      touser = userid;
      String url = "https://aip.baidubce.com/rpc/2.0/nlp/v1/sentiment_classify?access_token=24.0b1b34dd623026d53c7613b25ac0594d.2592000.1519900456.282335-10770393";
      String post = "{\"text\": \""+ctx+"\"}";
      String body = Jsoup.connect(url).method(Connection.Method.POST)
        .requestBody(post)
        .header("Content-Type", "application/json; charset=utf-8")
        .ignoreContentType(true).execute().body();
      String text = JSONObject.parseObject(body).getString("text");
      content =date+"【机器人】对你说：" + text;
    }
    template.convertAndSendToUser(touser,"/topic/private",new ChatMessage(userid,content,userid,date));


  }
  public static void main(String[] args) throws IOException {
/*    String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=YdirUBPrx8sF4LQthH1D6Obe&client_secret=aYhfrY7FuBcrhHiG5bMTdaGEPkhyKnT2&";
    String body = Jsoup.connect(url).ignoreContentType(true).execute().body();
    JSONObject jsonObject = JSONObject.parseObject(body);
    System.out.println(body);*/

    String context = new String("爱你啊你啊你".getBytes("GBK"), "GBK");
    String url = "https://aip.baidubce.com/rpc/2.0/nlp/v1/sentiment_classify?access_token=24.68ad3571d82c8f9eb6f7a16ae9608f89.2592000.1520499693.282335-10802209";
    String post = "{\"text\": \""+context+"\"}";
    String body = Jsoup.connect(url).method(Connection.Method.POST)
      .requestBody(post)
      .header("Content-Type", "application/json; charset=utf-8")
      .ignoreContentType(true).execute().body();
    System.out.println(body);
  }
}
