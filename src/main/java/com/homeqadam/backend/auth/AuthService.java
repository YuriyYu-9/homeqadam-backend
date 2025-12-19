package com.homeqadam.backend.auth;

import com.homeqadam.backend.email.EmailService;
import com.homeqadam.backend.security.jwt.JwtTokenProvider;
import com.homeqadam.backend.user.User;
import com.homeqadam.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /* =========================
       REGISTRATION
       ========================= */
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        String verificationCode = VerificationCodeGenerator.generateSixDigitCode();

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(false);
        user.setVerified(false);
        user.setVerificationCode(verificationCode);

        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), verificationCode);
    }

    /* =========================
       EMAIL VERIFICATION
       ========================= */
    public void verify(VerifyRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isVerified()) {
            throw new RuntimeException("User already verified");
        }

        if (!request.getCode().equals(user.getVerificationCode())) {
            throw new RuntimeException("Invalid verification code");
        }

        user.setVerified(true);
        user.setEnabled(true);
        user.setVerificationCode(null);

        userRepository.save(user);
    }

    /* =========================
       LOGIN (JWT)
       ========================= */
    public AuthResponse login(AuthRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("Email not verified");
        }

        String token = jwtTokenProvider.generateToken(authentication);

        return new AuthResponse(token);
    }
}
