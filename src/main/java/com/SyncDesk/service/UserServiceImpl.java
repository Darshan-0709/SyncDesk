package com.SyncDesk.service;

import com.SyncDesk.dto.UserDTO;
import com.SyncDesk.dto.UserLoginDTO;
import com.SyncDesk.dto.UserRegistrationDTO;
import com.SyncDesk.entity.User;
import com.SyncDesk.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO registerUser(UserRegistrationDTO userRegistrationDTO) {
        if(userRepository.existsByEmail(userRegistrationDTO.getEmail())){
            throw new RuntimeException("Email is already in use");
        }
        if(!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmedPassword())){
            throw new RuntimeException("Password does not match");
        }
        User user = new User();
        user.setFullName(userRegistrationDTO.getFullName());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        user = userRepository.save(user);
        return ConvertToDTO(user);
    }

    @Override
    public UserDTO loginUser(UserLoginDTO userLoginDTO) {
        User user = findByEmail(userLoginDTO.getEmail());
        if(user == null || !passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid Credentials");
        }
        return ConvertToDTO(user);
    }

    private UserDTO ConvertToDTO(User user) {
        return new UserDTO(user.getId(), user.getFullName(), user.getEmail());
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid Credentials"));
    }

    @Override
    public UserDTO findById(Long id) {
        return userRepository.findById(id)
                .map(this::ConvertToDTO)
                .orElseThrow(() -> new RuntimeException("No user found"));
    }

    @Override
    public List<UserDTO> fetchAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::ConvertToDTO).collect(Collectors.toList());
    }

    @Override
    public boolean deleteUser(Long id) {
        try{
            userRepository.deleteById(id);
            return true;
        }catch (Exception e){
            throw new RuntimeException("Failed to delete User");
        }
    }

    @Transactional
    @Override
    public User updateUser(Long id, UserDTO userDTO) {
        boolean isUpdated = false;

        User user = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Failed to update user"));

        if (userDTO.getEmail() != null
                && !userDTO.getEmail().isEmpty()
                && !userDTO.getEmail().equals(user.getEmail())) {
            user.setEmail(userDTO.getEmail());
            isUpdated = true;
        }

        if (userDTO.getFullName() != null
                && !userDTO.getFullName().isEmpty()
                && !userDTO.getFullName().equals(user.getFullName())) {
            user.setFullName(userDTO.getFullName());
            isUpdated = true;
        }

        if (isUpdated) {
            userRepository.save(user);
        }

        return user;
    }

    @Override
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("failed to update password"));

        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            throw new RuntimeException("Incorrect password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}
