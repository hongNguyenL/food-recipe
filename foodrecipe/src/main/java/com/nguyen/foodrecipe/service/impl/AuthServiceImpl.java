package com.nguyen.foodrecipe.service.impl;

import com.nguyen.foodrecipe.dto.AuthResponse;
import com.nguyen.foodrecipe.dto.LoginRequest;
import com.nguyen.foodrecipe.dto.RegisterRequest;
import com.nguyen.foodrecipe.dto.UserResponse;
import com.nguyen.foodrecipe.entity.Role;
import com.nguyen.foodrecipe.entity.User;
import com.nguyen.foodrecipe.exception.DuplicateEmailException;
import com.nguyen.foodrecipe.exception.DuplicateUsernameException;
import com.nguyen.foodrecipe.mapper.UserMapper;
import com.nguyen.foodrecipe.repository.UserRepository;
import com.nguyen.foodrecipe.security.JwtService;
import com.nguyen.foodrecipe.security.UserPrincipal;
import com.nguyen.foodrecipe.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.debug("Registering user: {}", request.username());

        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateUsernameException(request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        User user = User.builder()
                .username(request.username().trim())
                .email(request.email().trim().toLowerCase())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .enabled(true)
                .build();

        User saved = userRepository.save(user);
        log.info("User registered successfully: {}", saved.getUsername());

        return userMapper.toResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.debug("Login attempt: {}", request.usernameOrEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.usernameOrEmail(),
                            request.password()));
        } catch (BadCredentialsException e) {
            log.warn("Invalid login attempt: {}", request.usernameOrEmail());
            throw new BadCredentialsException("Invalid username/email or password");
        }

        UserPrincipal userPrincipal = (UserPrincipal) userRepository
                .findByUsername(request.usernameOrEmail())
                .or(() -> userRepository.findByEmail(request.usernameOrEmail()))
                .map(UserPrincipal::new)
                .orElseThrow(() -> new BadCredentialsException("Invalid username/email or password"));

        String token = jwtService.generateToken(userPrincipal);
        log.info("User logged in: {}", userPrincipal.getUsername());

        return new AuthResponse(token, "Bearer", jwtService.getExpirationMs() / 1000);
    }
}
