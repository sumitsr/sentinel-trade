package com.bank.sentinel.infrastructure.aop;

import com.bank.sentinel.domain.model.TradeFailure;
import com.leakyabstractions.result.Results;
import com.leakyabstractions.result.api.Result;
import jakarta.validation.ConstraintViolationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DatabaseResultAspect {

    private final Logger log;

    public DatabaseResultAspect() {
        this.log = LoggerFactory.getLogger(DatabaseResultAspect.class);
    }

    @Around("@annotation(com.bank.sentinel.infrastructure.aop.DatabaseResult)")
    public Object handle(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return Results.success(pjp.proceed());
        } catch (ConstraintViolationException ex) {
            return handleFailure("Constraint violation: " + ex.getMessage(), ex);
        } catch (DataAccessException ex) {
            return handleFailure(ex.getMessage(), ex);
        }
    }

    private Result<?, TradeFailure> handleFailure(String message, Exception ex) {
        log.error("DB operation failed [{}]: {}", ex.getClass().getSimpleName(), message);
        return Results.failure(new TradeFailure.PersistenceFailure(message, ex));
    }
}
