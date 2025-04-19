package dev.nautchkafe.nbt;

import org.bukkit.persistence.PersistentDataType;
import java.util.function.Function;
import java.util.function.Predicate;

final class NbtType<RAW, VALUE> {

    private final PersistentDataType<RAW, VALUE> dataType;
    private final Predicate<VALUE> validator;
    private final Function<VALUE, RAW> serialize;
    private final Function<RAW, VALUE> deserialize;

    NbtType(
        final PersistentDataType<RAW, VALUE> dataType,
        final Predicate<VALUE> validator,
        final Function<VALUE, RAW> serialize,
        final Function<RAW, VALUE> deserialize
    ) {
        this.dataType = dataType;
        this.validator = validator;
        this.serialize = serialize;
        this.deserialize = deserialize;
    }

    public PersistentDataType<RAW, VALUE> dataType() {
        return dataType;
    }

    public boolean isValid(final VALUE value) {
        return validator.test(value);
    }

    public RAW toRaw(final VALUE value) {
        return serialize.apply(value);
    }

    public VALUE fromRaw(final RAW raw) {
        return deserialize.apply(raw);
    }

    public static <RAW, VALUE> NbtType<RAW, VALUE> of(final PersistentDataType<RAW, VALUE> type) {
        return new NbtType<>(type, v -> true, Function.identity(), Function.identity());
    }

    public static <RAW, VALUE> NbtType<RAW, VALUE> of(final PersistentDataType<RAW, VALUE> type, final Predicate<VALUE> validator) {
        return new NbtType<>(type, validator, Function.identity(), Function.identity());
    }
}
