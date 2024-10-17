package com.SyncDesk.service;

import com.SyncDesk.dto.user.UserLoginDTO;
import com.SyncDesk.dto.user.UserRegistrationDTO;
import com.SyncDesk.entity.User;
import com.SyncDesk.dto.user.UserDTO;

import java.util.List;


public interface UserService {
    UserDTO registerUser(UserRegistrationDTO userRegistrationDTO);
    UserDTO loginUser(UserLoginDTO userLoginDTO);
    User findByEmail(String email);
    UserDTO findById(Long id);
    List<UserDTO> fetchAllUsers();
    boolean deleteUser(Long id);
    User updateUser(Long id, UserDTO userDTO);
    boolean changePassword(Long id, String oldPassword, String newPassword);
}
