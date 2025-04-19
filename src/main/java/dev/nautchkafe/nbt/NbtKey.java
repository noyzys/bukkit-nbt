package dev.nautchkafe.nbt;

import dev.nautchkafe.nbt.NbtType;
import dev.nautchkafe.nbt.NbtEffect;
import java.util.Objects;
import java.util.function.Predicate;

record NbtKey<RAW, VALUE>(
    String path,
    NbtType<RAW, VALUE> type,
    Predicate<VALUE> validator
) {
    NbtKey {
        validator = validator != null ? validator : value -> true;
    }

    NbtEffect<Value> validate(final VALUE value) {
        return validator.test(value)
            ? NbtEffect.success(value)
            : NbtEffect.failure("Invalid value for key: " + path);
    }
}
