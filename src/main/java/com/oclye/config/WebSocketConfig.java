package com.oclye.config;

import com.alibaba.fastjson.JSON;
import com.oclye.model.User;
import com.oclye.util.SpringBeanUtil;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.ui.Model;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import sun.security.acl.PrincipalImpl;

import javax.servlet.ServletContext;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ocly
 * @date 2018/2/2 15:55
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
  public Map<String,String> users =new HashMap<>();

  @Autowired
  private SimpMessagingTemplate template;
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
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
            users.put(session.getId(), "");
            new PrincipalImpl(session.getId());
            User user = new User();
            user.setName(session.getId());
            template.convertAndSend("/topic/userlist", JSON.toJSON(user));
            System.out.println(users.toString());
            System.out.println(session.getUri());
            System.out.println("连接成功："+session.getId());
            super.afterConnectionEstablished(session);
          }

          @Override
          public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
            throws Exception {
            String id = session.getId();
          //  users.remove(id);
            System.out.println(session.getUri());
            System.out.println("断开连接: " + id);
            super.afterConnectionClosed(session, closeStatus);
          }
        };
      }
    });
    super.configureWebSocketTransport(registration);
  }

}
