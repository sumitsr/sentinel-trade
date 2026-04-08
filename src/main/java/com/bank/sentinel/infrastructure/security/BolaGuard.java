package com.bank.sentinel.infrastructure.security;

import com.bank.sentinel.domain.model.TradeFailure;
import com.leakyabstractions.result.core.Results;
import com.leakyabstractions.result.api.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class BolaGuard {

    public Result<Boolean, TradeFailure> validateAccountAccess(String resourceAccountId) {
        String currentAccountId = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        if (resourceAccountId.equals(currentAccountId)) {
            return Results.success(true);
        }
        return Results.failure(new TradeFailure.ValidationFailure("Access denied"));
    }
}
