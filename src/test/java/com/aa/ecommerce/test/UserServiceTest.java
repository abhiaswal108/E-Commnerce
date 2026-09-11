package com.aa.ecommerce.test;
import com.aa.ecommerce.dto.LoginRequestDTO;
import com.aa.ecommerce.dto.LoginResponseDTO;
import com.aa.ecommerce.dto.RegisterRequestDTO;
import com.aa.ecommerce.dto.UserResponseDTO;
import com.aa.ecommerce.entity.User;
import com.aa.ecommerce.enums.Role;
import com.aa.ecommerce.exception.DuplicateResourceException;
import com.aa.ecommerce.exception.InvalidCredentialsException;
import com.aa.ecommerce.notification.EmailService;
import com.aa.ecommerce.repository.UserRepository;
import com.aa.ecommerce.security.JwtService;
import com.aa.ecommerce.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;
    @Mock
    private JwtService jwtService;

    @Test
    void registerUser_shouldSaveAndReturnUser_whenEmailNotTaken() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("new@test.com");
        dto.setPassword("plaintext123");

        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plaintext123")).thenReturn("hashed_password_xyz");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("new@test.com");
        savedUser.setPassword("hashed_password_xyz");
        savedUser.setRoles(Set.of(Role.CUSTOMER));

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDTO result = userService.registerUser(dto);

        assertEquals("new@test.com", result.getEmail());
        assertTrue(result.getRole().contains(Role.CUSTOMER));
        verify(passwordEncoder, times(1)).encode("plaintext123");
        verify(emailService, times(1)).sendWelcomeEmail("new@test.com");
    }
    @Test
    void registerUser_shouldThrowException_whenEmailAlreadyExists() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("a@gmail.com");
        existingUser.setPassword("hashed_abc");
        existingUser.setRoles(Set.of(Role.CUSTOMER));

        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("a@gmail.com");
        dto.setPassword("abc");

        when(userRepository.findByEmail("a@gmail.com")).thenReturn(Optional.of(existingUser));

        assertThrows(DuplicateResourceException.class, () -> {
            userService.registerUser(dto);
        });

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(emailService, never()).sendWelcomeEmail(anyString());
    }
    @Test
    void loginUser_shouldReturnToken_whenCredentialsAreValid() {
        User user = new User();
        user.setEmail("a@gmail.com");
        user.setPassword("hashed_password");
        user.setRoles(Set.of(Role.CUSTOMER));

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("a@gmail.com");
        dto.setPassword("plaintext123");

        when(userRepository.findByEmail("a@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plaintext123", "hashed_password")).thenReturn(true);
        when(jwtService.generateToken("a@gmail.com", user.getRoles())).thenReturn("fake.jwt.token");

        LoginResponseDTO result = userService.loginUser(dto);

        assertEquals("fake.jwt.token", result.getToken());
    }
    @Test
    void loginUser_shouldThrowException_whenPasswordIsWrong() {
        User user = new User();
        user.setEmail("a@gmail.com");
        user.setPassword("hashed_password");
        user.setRoles(Set.of(Role.CUSTOMER));

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("a@gmail.com");
        dto.setPassword("wrongpassword");

        when(userRepository.findByEmail("a@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "hashed_password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> {
            userService.loginUser(dto);
        });

        verify(jwtService, never()).generateToken(anyString(), any());
    }
    @Test
    void loginUser_shouldThrowException_whenEmailNotFound() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("nonexistent@gmail.com");
        dto.setPassword("somepassword");

        when(userRepository.findByEmail("nonexistent@gmail.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> {
            userService.loginUser(dto);
        });

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString(), any());
    }
}