package com.homeqadam.backend.auth;

import java.security.SecureRandom;

public final class VerificationCodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private VerificationCodeGenerator() {}

    public static String generateSixDigitCode() {
        int code = RANDOM.nextInt(900_000) + 100_000; // Диапазон: 100000–999999
        return String.valueOf(code);
    }
}
