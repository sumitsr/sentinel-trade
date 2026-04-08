package com.bank.sentinel.infrastructure.aop;

import com.bank.sentinel.domain.model.TradeFailure;
import com.leakyabstractions.result.api.Result;
import jakarta.validation.ConstraintViolationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseResultAspectTest {

    @Mock
    private ProceedingJoinPoint pjp;

    private final DatabaseResultAspect aspect = new DatabaseResultAspect();

    @Test
    void should_return_result_unchanged_when_proceed_succeeds() throws Throwable {
        Object expected = "success-value";
        when(pjp.proceed()).thenReturn(expected);

        Object result = aspect.handle(pjp);

        assertThat(result).isSameAs(expected);
    }

    @Test
    void should_return_persistence_failure_when_data_access_exception_thrown() throws Throwable {
        when(pjp.proceed()).thenThrow(new DataRetrievalFailureException("db error"));

        @SuppressWarnings("unchecked")
        Result<?, TradeFailure> result = (Result<?, TradeFailure>) aspect.handle(pjp);

        assertThat(result.hasFailure()).isTrue();
        assertThat(result.getFailure().get()).isInstanceOf(TradeFailure.PersistenceFailure.class);
    }

    @Test
    void should_return_persistence_failure_with_constraint_prefix_when_constraint_violation_thrown() throws Throwable {
        when(pjp.proceed()).thenThrow(new ConstraintViolationException("unique constraint", Set.of()));

        @SuppressWarnings("unchecked")
        Result<?, TradeFailure> result = (Result<?, TradeFailure>) aspect.handle(pjp);

        assertThat(result.hasFailure()).isTrue();
        TradeFailure.PersistenceFailure failure = (TradeFailure.PersistenceFailure) result.getFailure().get();
        assertThat(failure.message()).startsWith("Constraint violation:");
    }

    @Test
    void should_propagate_runtime_exception_not_caught_by_aspect() throws Throwable {
        when(pjp.proceed()).thenThrow(new RuntimeException("unexpected"));

        assertThatThrownBy(() -> aspect.handle(pjp))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("unexpected");
    }
}
