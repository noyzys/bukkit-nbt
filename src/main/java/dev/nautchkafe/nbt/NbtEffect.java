package dev.nautchkafe.nbt;

import java.util.function.Function;
import java.util.function.Consumer;

sealed interface NbtEffect<SUCCESS> permits NbtEffect.SuccessEffect, NbtEffect.FailureEffect {

    <NEXT> NbtEffect<NEXT> map(final Function<SUCCESS, NEXT> mapper);

    <NEXT> NbtEffect<NEXT> flatMap(final Function<SUCCESS, NbtEffect<NEXT>> binder);

    void run(final Consumer<SUCCESS> onSuccess, final Consumer<String> onFailure);

    <RESULT> RESULT fold(final Function<SUCCESS, RESULT> onSuccess, final Function<String, RESULT> onFailure);

    static <TYPE> NbtEffect<TYPE> success(final TYPE value) {
        return new SuccessEffect<>(value);
    }

    static <TYPE> NbtEffect<TYPE> failure(final String error) {
        return new FailureEffect<>(error);
    }

    record SuccessEffect<TYPE>(TYPE value) implements NbtEffect<TYPE> {
        @Override
        public <RESULT> NbtEffect<RESULT> map(final Function<TYPE, RESULT> mapper) {
            return new SuccessEffect<>(mapper.apply(value));
        }

        @Override
        public <RESULT> NbtEffect<RESULT> flatMap(final Function<TYPE, NbtEffect<RESULT>> binder) {
            return binder.apply(value);
        }

        @Override
        public void run(final Consumer<TYPE> onSuccess, final Consumer<String> onFailure) {
            onSuccess.accept(value);
        }

        @Override
        public <RESULT> RESULT fold(final Function<TYPE, RESULT> onSuccess, final Function<String, RESULT> onFailure) {
            return onSuccess.apply(value);
        }
    }

    record FailureEffect<TYPE>(String error) implements NbtEffect<TYPE> {
        @Override
        public <RESULT> NbtEffect<RESULT> map(final Function<TYPE, RESULT> mapper) {
            return new FailureEffect<>(error);
        }

        @Override
        public <RESULT> NbtEffect<RESULT> flatMap(final Function<TYPE, NbtEffect<RESULT>> binder) {
            return new FailureEffect<>(error);
        }

        @Override
        public void run(final Consumer<TYPE> onSuccess, final Consumer<String> onFailure) {
            onFailure.accept(error);
        }

        @Override
        public <RESULT> RESULT fold(final Function<TYPE, RESULT> onSuccess, final Function<String, RESULT> onFailure) {
            return onFailure.apply(error);
        }
    }
}
