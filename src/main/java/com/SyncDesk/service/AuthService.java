package com.SyncDesk.service;

import com.SyncDesk.dto.user.UserDTO;
import com.SyncDesk.entity.User;
import com.SyncDesk.entity.UserPrincipal;
import com.SyncDesk.repository.UserRepository;
import com.SyncDesk.utils.NoSuchUserFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {


    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserRepository userRepository;



    @Autowired
    public AuthService(AuthenticationManager authenticationManager, JWTService jwtService, UserServiceImpl userService, UserRepository userRepository, MyUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public String login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtService.generateToken(userDetails.getUsername());
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();
            return userRepository.findByEmail(email).orElseThrow(() -> new NoSuchUserFoundException("User not authenticated"));
        }
        throw new NoSuchUserFoundException("User not authenticated");
    }
}
