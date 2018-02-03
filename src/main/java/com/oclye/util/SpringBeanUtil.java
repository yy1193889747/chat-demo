package com.oclye.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

/**
 * @author ocly
 * @date 2018/2/3 19:23
 */
public final class SpringBeanUtil implements ApplicationContextAware {

  private static ApplicationContext ctx;

  public static Object getBean(String id) {
    if (ctx == null) {
      throw new NullPointerException("ApplicationContext is null");
    }
    return ctx.getBean(id);
  }

  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    ctx = applicationContext;
  }
public static ServletContext getServletContext(){
  WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
  ServletContext servletContext = webApplicationContext.getServletContext();
  return servletContext;
}
}
