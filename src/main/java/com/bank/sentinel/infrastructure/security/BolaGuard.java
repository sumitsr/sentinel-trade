package com.bank.sentinel.infrastructure.security;

import com.bank.sentinel.domain.model.Result;
import com.bank.sentinel.domain.model.TradeFailure;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class BolaGuard {

    public Result<Void, TradeFailure> validateAccountAccess(String resourceAccountId) {
        String currentAccountId = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        if (resourceAccountId.equals(currentAccountId)) {
            return Result.success(null);
        }
        return Result.failure(new TradeFailure.ValidationFailure("Access denied"));
    }
}
