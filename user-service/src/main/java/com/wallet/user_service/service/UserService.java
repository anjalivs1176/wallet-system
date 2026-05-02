package com.wallet.user_service.service;

import com.wallet.user_service.entity.User;
import com.wallet.user_service.repository.UserRepository;
import com.wallet.user_service.requestDto.UserRequest;
import com.wallet.user_service.responseDto.UserResponse;
import com.wallet.user_service.config.JwtUtil;
import com.wallet.user_service.publisher.UserEventPublisher;
import com.wallet.user_service.event.UserCreatedEvent;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher userEventPublisher;

    public UserService(UserRepository userRepository,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,
            UserEventPublisher userEventPublisher) {

        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userEventPublisher = userEventPublisher;
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        return response;
    }

    public String login(String email, String password) {

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getId(), user.getEmail());
    }

    public UserResponse createUser(UserRequest request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User already exists with this email");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);

        UserCreatedEvent event = new UserCreatedEvent(
                String.valueOf(saved.getId()),
                saved.getEmail(),
                java.time.LocalDateTime.now().toString()
        );

        userEventPublisher.publishUserCreatedEvent(event);

        return mapToResponse(saved);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return user != null ? mapToResponse(user) : null;
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPassword(request.getPassword());

        User updated = userRepository.save(existing);
        return mapToResponse(updated);
    }

    public void deleteUser(Long id) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing != null) {
            userRepository.delete(existing);
        }
    }
}
