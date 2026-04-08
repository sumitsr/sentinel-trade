package com.bank.sentinel.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-key-minimum-32-chars-x";
    private static final long EXPIRY_MS = 3_600_000L;

    private final JwtTokenProvider provider = new JwtTokenProvider(SECRET, EXPIRY_MS);

    @Test
    void should_generate_non_null_non_empty_token() {
        String token = provider.generateToken("acc-123");

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void should_validate_token_generated_by_provider() {
        String token = provider.generateToken("acc-123");

        assertThat(provider.validateToken(token)).isTrue();
    }

    @Test
    void should_return_false_when_token_is_random_string() {
        assertThat(provider.validateToken("not.a.valid.jwt")).isFalse();
    }

    @Test
    void should_return_false_when_token_is_tampered() {
        String token = provider.generateToken("acc-123");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThat(provider.validateToken(tampered)).isFalse();
    }

    @Test
    void should_extract_account_id_matching_original() {
        String token = provider.generateToken("acc-123");

        assertThat(provider.extractAccountId(token)).isEqualTo("acc-123");
    }

    @Test
    void should_return_false_when_token_is_expired() throws InterruptedException {
        JwtTokenProvider shortLived = new JwtTokenProvider(SECRET, 1L);
        String token = shortLived.generateToken("acc-123");
        Thread.sleep(10);

        assertThat(shortLived.validateToken(token)).isFalse();
    }
}
