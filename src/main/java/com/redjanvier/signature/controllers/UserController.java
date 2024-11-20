package com.redjanvier.signature.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redjanvier.signature.dtos.ChangePasswordRequest;
import com.redjanvier.signature.dtos.RegisterResponse;
import com.redjanvier.signature.dtos.UserDto;
import com.redjanvier.signature.models.User;
import com.redjanvier.signature.services.UserService;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping()
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<RegisterResponse> getAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @PatchMapping("phone")
    public ResponseEntity<RegisterResponse> postMethodName(
        @RequestBody UserDto phone,
        Principal connectedUser
    ) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        
        return ResponseEntity.ok(service.changePhone(phone, user.getId()));
    }


    @PatchMapping
    public ResponseEntity<?> changePassword(
          @RequestBody ChangePasswordRequest request,
          Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @PutMapping("position/{id}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<RegisterResponse> changePosition(
          @RequestBody UserDto request,
          @PathVariable(name = "id") Integer userId
    ) {
        return ResponseEntity.ok(service.changePosition(request, userId));
    }
}
