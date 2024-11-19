package com.redjanvier.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redjanvier.security.config.JwtService;
import com.redjanvier.security.token.Token;
import com.redjanvier.security.token.TokenRepository;
import com.redjanvier.security.token.TokenType;
import com.redjanvier.security.user.Role;
import com.redjanvier.security.user.User;
import com.redjanvier.security.user.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final JavaMailSender mailSender;

  public RegisterResponse register(RegisterRequest request) throws MessagingException {
    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .position("General employee")
        .role(request.getRole())
        .enabled(false)
        .build();
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    saveUserToken(savedUser, jwtToken, TokenType.VERIFY);
    final String VERIFICATION_URL = "http://localhost:8080/api/v1/auth/verify?key=" + jwtToken;

    sendEmail(savedUser.getEmail(), "IST Signatures - Verify your account", """
      <h2>Hey, USER_FIRSTNAME</h2>
      <p>Congratulations for your successfull registration to IST Signatures Platform.</p>
      <p>As a step of your registration you need to verify your account now!</p>
      <a href='VERIFICATION_URL' target='__blank'>Click this link to verify</a>
      """
      .replaceAll("USER_FIRSTNAME", savedUser.getFirstname())
      .replaceAll("VERIFICATION_URL", VERIFICATION_URL)
    );

    return RegisterResponse.builder()
        .success(true)
        .data(null)
        .message("Successfully registered check your email to verify your account (" + savedUser.getEmail() + ")")
        .build();
  }

  public RegisterResponse registerAdmin(RegisterRequest request) {
    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .position("Admin")
        .role(Role.ADMIN)
        .enabled(true)
        .build();
    var savedUser = repository.save(user);
    repository.flush();

    var jwtToken = jwtService.generateToken(user);
    saveUserToken(savedUser, jwtToken, TokenType.BEARER);
    return RegisterResponse.builder()
        .success(true)
        .data(null)
        .message("Successfully registered check your email to verify your account (" + savedUser.getEmail() + ")")
        .build();
  }

  public RegisterResponse verify(String jwtToken) {
    String userEmail = jwtService.extractUsername(jwtToken);
    User user = repository.findByEmail(userEmail).orElseThrow();
    user.setEnabled(true);

    repository.save(user);
    return RegisterResponse.builder()
        .success(true)
        .data(user)
        .message("Successfully verified the user. Kindly login to proceed")
        .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void saveUserToken(User user, String jwtToken, TokenType type) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(type)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); 
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
    }
}
