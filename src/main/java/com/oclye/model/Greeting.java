package com.oclye.model;

import lombok.Data;

/**
 * @author ocly
 * @date 2018/2/2 15:45
 */
@Data
public class Greeting {
  private String content;

  public Greeting(String content) {
    this.content = content;
  }
}
