package com.redjanvier.signature.controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.redjanvier.signature.dtos.AuthenticationRequest;
import com.redjanvier.signature.dtos.AuthenticationResponse;
import com.redjanvier.signature.dtos.RegisterRequest;
import com.redjanvier.signature.dtos.RegisterResponse;
import com.redjanvier.signature.services.AuthenticationService;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  @Value("${application.url.fe}")
  private String BASE_URL_FE;

  private final AuthenticationService service;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) throws MessagingException {
    return ResponseEntity.ok(service.register(request));
  }

  @GetMapping("/verify")
  public RedirectView verify(@RequestParam String key, RedirectAttributes redirectAttributes) {
    RedirectView redirectView = new RedirectView(BASE_URL_FE);
    if (service.verify(key)) return redirectView;
    else {
      redirectAttributes.addFlashAttribute("error", "User not verified!");
      return redirectView;
    }
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.authenticate(request));
  }
  
  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }


}
