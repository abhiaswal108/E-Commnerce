package com.aa.ecommerce.service;


import com.aa.ecommerce.dto.LoginRequestDTO;
import com.aa.ecommerce.dto.LoginResponseDTO;
import com.aa.ecommerce.dto.RegisterRequestDTO;
import com.aa.ecommerce.dto.UserResponseDTO;
import com.aa.ecommerce.entity.User;
import com.aa.ecommerce.enums.Role;
import com.aa.ecommerce.exception.DuplicateResourceException;
import com.aa.ecommerce.exception.InvalidCredentialsException;
import com.aa.ecommerce.repository.UserRepository;
import com.aa.ecommerce.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository ur;
    private final PasswordEncoder pe;
    private final JwtService js;
    public UserResponseDTO registerUser(RegisterRequestDTO dto){
        if (ur.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists, please login");
        }
        User u= new User();
        u.setEmail(dto.getEmail());
        u.setPassword(pe.encode(dto.getPassword()));
        u.setRoles(Set.of(Role.CUSTOMER));
        User savedUser=ur.save(u);
        return mapToUserResponseDTO(savedUser);

    }
    public LoginResponseDTO loginUser(LoginRequestDTO dto) {
        User u = ur.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!pe.matches(dto.getPassword(), u.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = js.generateToken(u.getEmail(), u.getRoles());
        return new LoginResponseDTO(token);
    }

    private UserResponseDTO mapToUserResponseDTO(User u){
        UserResponseDTO user= new UserResponseDTO();
        user.setId(u.getId());
        user.setEmail(u.getEmail());
        user.setRole(u.getRoles());
        return user;
    }
}
