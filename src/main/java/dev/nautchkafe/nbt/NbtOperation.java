package dev.nautchkafe.nbt;

import dev.nautchkafe.nbt.NbtEffect;
import dev.nautchkafe.nbt.NbtKey;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.function.UnaryOperator;

@FunctionalInterface
interface NbtOperation {

    NbtEffect<PersistentDataContainer> execute(final PersistentDataContainer container);

    default NbtOperation andThen(final NbtOperation next) {
        return container -> this.execute(container).flatMap(next::execute);
    }

    static <RAW, VALUE> NbtOperation write(final NbtKey<RAW, VALUE> key, final VALUE value) {
        return container -> key.validate(value).flatMap(valid -> {
            container.set(key.path(), key.type().dataType(), valid);
            return NbtEffect.success(container);
        });
    }

    public static <TYPE> NbtOperation write(final NbtKey<List<TYPE>, final List<TYPE>> key, final List<TYPE> value) {
        return new NbtOperation(container -> {
            container.set(key.path(), PersistentDataType.STRING_LIST, value);
            return NbtEffect.success(container);
        });
    }

    public static <TYPE, U> NbtOperation write(final NbtKey<Map<String, String>, final Map<String, String>> key, final Map<String, String> value) {
        return new NbtOperation(container -> {
            final PersistentDataContainer dataContainer = container.get(key.path(), PersistentDataType.TAG_CONTAINER);
            if (dataContainer == null) {
                dataContainer = new PersistentDataContainer();
            }
            
            value.forEach(dataContainer::set); 
            container.set(key.path(), PersistentDataType.TAG_CONTAINER, dataContainer);
            return NbtEffect.success(container);
        });
    }

    static <RAW, VALUE> NbtEffect<VALUE> read(final NbtKey<RAW, VALUE> key, final PersistentDataContainer container) {
        return NbtEffect.of(() -> {
            VALUE value = container.get(key.path(), key.type().dataType());
            if (value == null) {
                throw new IllegalStateException("Key not found: " + key.path());
            }

            return value;
        });
    }

    static <RAW, VALUE> NbtOperation modify(final NbtKey<RAW, VALUE> key, final UnaryOperator<VALUE> operator) {
        return container -> {
            VALUE value = container.get(key.path(), key.type().dataType());
            if (value != null) {
                VALUE transformed = operator.apply(value);
                container.set(key.path(), key.type().dataType(), transformed);
            }
            
            return NbtEffect.success(container);
        };
    }
}

