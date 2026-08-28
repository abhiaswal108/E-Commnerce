package com.aa.ecommerce.controller;


import com.aa.ecommerce.dto.LoginRequestDTO;
import com.aa.ecommerce.dto.LoginResponseDTO;
import com.aa.ecommerce.dto.RegisterRequestDTO;
import com.aa.ecommerce.dto.UserResponseDTO;
import com.aa.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService us;
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO user){
        UserResponseDTO regUser=us.registerUser(user);
        return  new ResponseEntity<>(regUser, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO user){
        LoginResponseDTO loginUser=us.loginUser(user);
        return new ResponseEntity<>(loginUser, HttpStatus.OK);
    }
}
