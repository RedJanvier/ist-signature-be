package com.redjanvier.security.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redjanvier.security.auth.RegisterResponse;

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
    public ResponseEntity<RegisterResponse> getAll(@RequestParam String param) {
        return ResponseEntity.ok(service.listAll());
    }

    @PatchMapping("phone")
    public ResponseEntity<RegisterResponse> postMethodName(
        @RequestBody String phone,
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

    @PatchMapping("position/{id}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<RegisterResponse> changePosition(
          @RequestBody String newPosition,
          @PathVariable(name = "id") Integer userId
    ) {
        return ResponseEntity.ok(service.changePosition(newPosition, userId));
    }
}
