package com.bank.sentinel.domain.model;

public sealed interface Result<V, E> {
    record Success<V, E>(V value) implements Result<V, E> {}
    record Failure<V, E>(E error) implements Result<V, E> {}

    static <V, E> Result<V, E> success(V value) { return new Success<>(value); }
    static <V, E> Result<V, E> failure(E error)  { return new Failure<>(error); }
}
