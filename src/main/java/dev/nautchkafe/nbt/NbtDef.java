package dev.nautchkafe.nbt;

import dev.nautchkafe.NbtTypes;

final class NbtDef {

    private NbtDef() {
    }

    public static final NbtKey<Byte, Byte> IS_SPECIAL =
        createByteKey("is_special", value -> value >= 0 && value <= 1);

    public static final NbtKey<String, String> ITEM_ID =
        createStringKey("item_id", value -> value != null && !value.isBlank());

    private static NbtKey<Byte, Byte> createByteKey(final String path, final Predicate<Byte> validator) {
        return new NbtKey<>(path, NbtTypes.BYTE, validator);
    }

    private static NbtKey<String, String> createStringKey(final String path, final Predicate<String> validator) {
        return new NbtKey<>(path, NbtTypes.STRING, validator);
    }

}