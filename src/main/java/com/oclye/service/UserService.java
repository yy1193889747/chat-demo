package com.oclye.service;

import com.oclye.config.WebSocketConfig;
import com.oclye.model.User;
import com.oclye.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author ocly
 * @date 2018/2/3 17:18
 */
@Service
public class UserService implements UserDetailsService {

  @Autowired
  WebSocketConfig webSocketConfig;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    String onlinuser = webSocketConfig.users.toString();
    if (onlinuser.contains(username)||"机器人".equals(username)) {
      throw new UsernameNotFoundException("用户已存在");
    }
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("USER"));
    UserPrincipal user = new UserPrincipal(username,"",authorities);
    user.setName(username);
    return user;
	}

}
