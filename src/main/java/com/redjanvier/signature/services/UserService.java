package com.redjanvier.signature.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.redjanvier.signature.dtos.ChangePasswordRequest;
import com.redjanvier.signature.dtos.RegisterResponse;
import com.redjanvier.signature.dtos.UserDto;
import com.redjanvier.signature.models.User;
import com.redjanvier.signature.repositories.UserRepository;

import java.security.Principal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }

    public RegisterResponse changePosition(UserDto request, Integer id) {
        User user = repository.findById(id).orElseThrow();
        user.setPosition(request.getPosition());
        repository.save(user);
        repository.flush();

        return RegisterResponse.builder()
            .success(true)
            .message("Position successfully updated!")
            .data(UserDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .position(user.getPosition())
                .phone(user.getPhone())
                .enabled(user.getEnabled())
                .build()
            )
            .build();
    }

    public RegisterResponse changePhone(UserDto request, Integer id) {
        User user = repository.findById(id).orElseThrow(() -> new RuntimeException("User is not found."));
        user.setPhone(request.getPhone());
        repository.save(user);
        repository.flush();

        return RegisterResponse.builder()
            .success(true)
            .message("Phone successfully updated!")
            .data(UserDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .position(user.getPosition())
                .phone(user.getPhone())
                .enabled(user.getEnabled())
                .build()
            ).build();
    }

    public RegisterResponse listAll() {
        List<UserDto> users = repository.findAll().stream().map(user -> 
            UserDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .position(user.getPosition())
                .phone(user.getPhone())
                .enabled(user.getEnabled())
                .build()
            ).toList();

        return RegisterResponse.builder()
            .success(true)
            .message("All users retrieved successfully")
            .data(users)
            .build();
    }
}
