package com.oclye.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author ocly
 * @date 2018/2/3 17:19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserPrincipal extends User {

  private String name;

  public UserPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}
}
