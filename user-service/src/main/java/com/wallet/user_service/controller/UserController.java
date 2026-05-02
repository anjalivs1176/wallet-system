package com.wallet.user_service.controller;

import com.wallet.user_service.requestDto.LoginRequest;
import com.wallet.user_service.requestDto.UserRequest;
import com.wallet.user_service.responseDto.UserResponse;
import com.wallet.user_service.service.UserService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "http://localhost:3000",
             allowedHeaders = "*"
)
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private String getLoggedInEmail(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName(); // this is email
    }


    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody UserRequest request){
        return userService.createUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        String token = userService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id){

        String loggedInEmail = getLoggedInEmail();

        UserResponse user = userService.getUserById(id);

        if(user == null){
            throw new RuntimeException("User not found");
        }

        if(!user.getEmail().equals(loggedInEmail)){
            throw new RuntimeException("Unauthorized access");
        }

        return user;
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id,
                                   @RequestBody UserRequest request){

        String loggedInEmail = getLoggedInEmail();

        UserResponse user = userService.getUserById(id);

        if(user == null){
            throw new RuntimeException("User not found");
        }

        if(!user.getEmail().equals(loggedInEmail)){
            throw new RuntimeException("Unauthorized access");
        }

        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){

        String loggedInEmail = getLoggedInEmail();

        UserResponse user = userService.getUserById(id);

        if(user == null){
            throw new RuntimeException("User not found");
        }

        if(!user.getEmail().equals(loggedInEmail)){
            throw new RuntimeException("Unauthorized access");
        }

        userService.deleteUser(id);
    }
}