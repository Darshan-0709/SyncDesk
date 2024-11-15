package com.SyncDesk.controller;


import com.SyncDesk.common.ApiResponse;
import com.SyncDesk.dto.user.LoginResponseDTO;
import com.SyncDesk.dto.user.UserDTO;
import com.SyncDesk.dto.user.UserLoginDTO;
import com.SyncDesk.dto.user.UserRegistrationDTO;
import com.SyncDesk.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserServiceImpl userService;


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = userService.findById(id);
            return ResponseEntity.ok(new ApiResponse<>("User fetched successfully", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.fetchAllUsers();
        return ResponseEntity.ok(new ApiResponse<>("Users fetched successfully", users));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@Valid @RequestBody UserRegistrationDTO userDTO, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(new ApiResponse<>("Validation errors", errors));
        }
        try {
            UserDTO registeredUser = userService.registerUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("User registered successfully", registeredUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), null));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        try {
            LoginResponseDTO response = userService.loginUser(userLoginDTO);
            return ResponseEntity.ok(new ApiResponse<>("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            userService.updateUser(id, userDTO);
            return ResponseEntity.ok(new ApiResponse<>("User updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUserById(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new ApiResponse<>("User deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Failed to delete user", null));
        }
    }
}
