package com.bank.sentinel.infrastructure.security;

import com.bank.sentinel.domain.model.TradeFailure;
import com.leakyabstractions.result.api.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BolaGuardTest {

    private final BolaGuard guard = new BolaGuard();

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void should_return_success_when_account_id_matches_authenticated_user() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("acc-123", null, List.of()));

        Result<Boolean, TradeFailure> result = guard.validateAccountAccess("acc-123");

        assertThat(result.hasSuccess()).isTrue();
    }

    @Test
    void should_return_validation_failure_when_account_id_does_not_match() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("acc-123", null, List.of()));

        Result<Boolean, TradeFailure> result = guard.validateAccountAccess("acc-999");

        assertThat(result.hasFailure()).isTrue();
        assertThat(result.getFailure().get()).isInstanceOf(TradeFailure.ValidationFailure.class);
    }

    @Test
    void should_return_failure_when_user_is_anonymous() {
        SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken("key", "anonymousUser",
                        List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

        Result<Boolean, TradeFailure> result = guard.validateAccountAccess("acc-123");

        assertThat(result.hasFailure()).isTrue();
        assertThat(result.getFailure().get()).isInstanceOf(TradeFailure.ValidationFailure.class);
    }
}
