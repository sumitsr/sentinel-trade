package com.bank.sentinel.adapter.in.rest;

import com.bank.sentinel.infrastructure.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/token")
    public ResponseEntity<AuthResponse> token(@RequestBody AuthRequest request) {
        if (request.accountId() == null || request.accountId().isBlank()
                || request.password() == null || request.password().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        String token = jwtTokenProvider.generateToken(request.accountId());
        return ResponseEntity.ok(new AuthResponse(token, 3600000L));
    }

    public record AuthRequest(String accountId, String password) {}
    public record AuthResponse(String token, long expiresIn) {}
}
