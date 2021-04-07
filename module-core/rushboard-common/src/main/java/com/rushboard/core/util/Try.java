package com.rushboard.core.util;

import java.util.Optional;
import java.util.function.Function;

public abstract class Try<T> {

  protected final T value;

  protected Try(T value) {
    this.value = value;
  }

  private static final class Failure<T> extends Try<T> {

    public Failure(T value) {
      super(value);
    }

    @Override
    public T getValue() throws Throwable {
      throw (Throwable) value;
    }

    @Override
    public T getOrElse(T defaultValue) {
      return defaultValue;
    }

    @Override
    public T getOrElseNull() {
      return null;
    }

    @Override
    public Optional<T> toOptional() {
      return Optional.empty();
    }
  }

  private static final class Success<T> extends Try<T> {

    public Success(T value) {
      super(value);
    }

    @Override
    public T getValue() {
      return value;
    }

    @Override
    public T getOrElse(T defaultValue) {
      return value;
    }

    @Override
    public T getOrElseNull() {
      return value;
    }

    @Override
    public Optional<T> toOptional() {
      return Optional.ofNullable(value);
    }
  }

  @SuppressWarnings("rawtypes")
  public static <T, R> Try<R> of(Function<T, R> func, T arg) {
    try {
      return new Success<>(func.apply(arg));
    } catch (Throwable t) {
      return new Failure(t);
    }
  }

  public abstract T getValue() throws Throwable;

  public abstract T getOrElse(T defaultValue);

  public abstract T getOrElseNull();

  public abstract Optional<T> toOptional();
}
