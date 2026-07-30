package com.mikosik.stork.common;

import static com.mikosik.stork.common.Result.Failure.failure;
import static com.mikosik.stork.common.Result.Success.success;

import java.util.List;

import com.mikosik.stork.common.func.Functions.Fab;

public sealed interface Result<S, F> {
  record Success<S, F>(S item) implements Result<S, F> {
    public static <S, F> Success<S, F> success(S item) {
      return new Success<>(item);
    }
  }

  record Failure<S, F>(F item) implements Result<S, F> {
    public static <S, F> Failure<S, F> failure(F item) {
      return new Failure<>(item);
    }
  }

  default <S2, F2> Result<S2, F2> map(
      Fab<? super S, ? extends S2> mappingSuccess,
      Fab<? super F, ? extends F2> mappingFailure) {
    return switch (this) {
      case Success<S, F> success -> success(mappingSuccess.apply(success.item()));
      case Failure<S, F> failure -> failure(mappingFailure.apply(failure.item()));
    };
  }

  default <S2> Result<S2, F> mapSuccess(Fab<? super S, ? extends S2> mapping) {
    return map(mapping, x -> x);
  }

  default <F2> Result<S, F2> mapFailure(Fab<? super F, ? extends F2> mapping) {
    return map(x -> x, mapping);
  }

  default S unwrap(Fab<F, RuntimeException> handler) {
    return switch (this) {
      case Success<S, F> success -> success.item();
      case Failure<S, F> failure -> throw handler.apply(failure.item);
    };
  }

  static <S, F> Result<List<S>, List<F>> combine(List<Result<S, F>> results) {
    return results.stream().allMatch(Success.class::isInstance)
        ? new Success<>(results.stream()
            .map(result -> (Success<S, F>) result)
            .map(Success::item)
            .toList())
        : new Failure<>(results.stream()
            .filter(Failure.class::isInstance)
            .map(result -> (Failure<S, F>) result)
            .map(Failure::item)
            .toList());
  }
}
