package com.oclye.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.oclye.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author ocly
 * @date 2018/2/2 15:55
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

  public JSONObject users =new JSONObject();

  @Autowired
  private SimpMessagingTemplate template;
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setUserDestinationPrefix("/user");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/gs-guide-websocket").withSockJS();
  }

  @Override
  public void configureWebSocketTransport(final WebSocketTransportRegistration registration) {
    registration.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
      @Override
      public WebSocketHandler decorate(final WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
          @Override
          public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
            users.put(session.getId(), session.getPrincipal().getName());
            User user = new User();
            user.setName(session.getPrincipal().getName());
            user.setId(session.getId());
            user.setOnline(true);
            template.convertAndSend("/topic/userlist", JSON.toJSON(user));
            System.out.println("连接成功："+session.getId());
            super.afterConnectionEstablished(session);
          }

          @Override
          public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
            throws Exception {
            users.remove(session.getId());
            User user = new User();
            user.setName(session.getPrincipal().getName());
            user.setId(session.getId());
            user.setOnline(false);
            template.convertAndSend("/topic/userlist", JSON.toJSON(user));
            System.out.println("断开连接: " + session.getId());
            super.afterConnectionClosed(session, closeStatus);
          }
        };
      }
    });
    super.configureWebSocketTransport(registration);
  }
}
