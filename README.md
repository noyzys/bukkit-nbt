## Example:

```java
static final NbtKey<String, UUID> OWNER_UUID = new NbtKey<>(
    "owner_uuid",
    new NbtType<>(
        PersistentDataType.STRING,
        uuid -> uuid != null,
        UUID::toString,
        UUID::fromString
    ), uuid -> uuid.version() == 4
);

ItemStack createSpecialItem(ItemStack baseItem, String id) {
    return NbtItem.modify(baseItem, plugin)
        .applyOperation(NbtOperation.write(NbtDef.IS_SPECIAL, (byte) 1)
        .andThen(NbtOperation.write(NbtDef.ITEM_ID, id)))
        .done();
}

Predicate<ItemStack> getSpecialItemChecker() {
    return NbtItem.isSpecialItem(plugin);
}

Function<ItemStack, ItemStack> createUpgrader() {
    return item -> NbtItem.modify(item, plugin)
        .applyOperation(NbtOperation.modify(NbtDef.ITEM_ID, value -> "upgraded_" + value)
        .andThen(NbtOperation.write(NbtDef.IS_SPECIAL, (byte) 1)))
        .done();
}

void inspectItem(ItemStack item) {
    NbtItem.modify(item, plugin)
        .inspectContainer(container -> Map.of(
            "special", container.get(NbtDef.IS_SPECIAL.path(), NbtDef.IS_SPECIAL.type()), 
            "id", container.get(NbtDef.ITEM_ID.path(), NbtDef.ITEM_ID.type()) 
        
        )).run(
            data -> plugin.getLogger().info("NBT Data: " + data),
            error -> plugin.getLogger().warning("NBT inspection failed: " + error)
        );
}

String readItemId(ItemStack item) {
    return NbtItem.modify(item, plugin)
        .inspectContainer(container -> container.get(NbtDef.ITEM_ID.path(), NbtDef.ITEM_ID.type()))
        .fold(Function.identity(), error -> null);
}

ItemStack createNpcItem(UUID ownerId, String jsonMetadata) {
    return NbtItem.modify(new ItemStack(Material.PLAYER_HEAD), plugin)
        .applyOperation(NbtOperation.write(OWNER_UUID, ownerId)
                .andThen(NbtOperation.write(NbtDef.ITEM_ID, "npc"))
                .andThen(NbtOperation.write(NbtDef.IS_SPECIAL, (byte) 1))
                .andThen(NbtOperation.write(NbtDef.METADATA, jsonMetadata)))
                .done();
}

void inspectNpc(ItemStack item) {
    NbtItem.modify(item, plugin)
        .inspectContainer(container -> Map.of(
            "uuid", container.get(OWNER_UUID.path(), OWNER_UUID.type()),
            "id", container.get(NbtDef.ITEM_ID.path(), NbtDef.ITEM_ID.type()),
            "meta", container.get(NbtDef.METADATA.path(), NbtDef.METADATA.type())
        
        )).run(
            map -> plugin.getLogger().info("NPC NBT: " + map),
            err -> plugin.getLogger().warning("Failed to read NPC NBT: " + err)
        );
}
```