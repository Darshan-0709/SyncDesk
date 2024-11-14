package com.SyncDesk.service;

import com.SyncDesk.entity.User;
import com.SyncDesk.entity.UserPrincipal;
import com.SyncDesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String name) throws NoSuchUserFoundException {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new NoSuchUserFoundException("User not found with email: " + name));
        return new UserPrincipal(user);
    }
}
