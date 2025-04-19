package dev.nautchkafe.nbt;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

final class NbtContainer {

    public static final NbtType<Byte, Byte> BYTE = NbtType.of(PersistentDataType.BYTE, byte -> byte >= 0 && byte <= 1);
    public static final NbtType<String, String> STRING = NbtType.of(PersistentDataType.STRING, string -> !string.isEmpty());
    public static final NbtType<Integer, Integer> INTEGER = NbtType.of(PersistentDataType.INTEGER);
    public static final NbtType<Long, Long> LONG = NbtType.of(PersistentDataType.LONG);
    public static final NbtType<Double, Double> DOUBLE = NbtType.of(PersistentDataType.DOUBLE);
    public static final NbtType<Float, Float> FLOAT = NbtType.of(PersistentDataType.FLOAT);
    public static final NbtType<Short, Short> SHORT = NbtType.of(PersistentDataType.SHORT);

    public static final NbtType<List<String>, List<String>> STRING_LIST = NbtType.of(PersistentDataType.STRING_LIST);
    public static final NbtType<List<Integer>, List<Integer>> INTEGER_LIST = NbtType.of(PersistentDataType.INTEGER_LIST);
    public static final NbtType<Map<String, String>, Map<String, String>> STRING_MAP = NbtType.of(PersistentDataType.TAG_CONTAINER);
    
    public static final NbtType<int[], int[]> INTEGER_ARRAY = NbtType.of(PersistentDataType.INTEGER_ARRAY);
    public static final NbtType<long[], long[]> LONG_ARRAY = NbtType.of(PersistentDataType.LONG_ARRAY);
    public static final NbtType<byte[], byte[]> BYTE_ARRAY = NbtType.of(PersistentDataType.BYTE_ARRAY);
    public static final NbtType<PersistentDataContainer, PersistentDataContainer> CONTAINER = NbtType.of(PersistentDataType.TAG_CONTAINER);

    private NbtContainer() {
    }
}
