package com.oclye.service;

import com.oclye.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * @author ocly
 * @date 2018/2/3 17:18
 */
@Service
public class UserService implements UserDetailsService {


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = new User();
    user.setName(username);
    return user;
	}

}
