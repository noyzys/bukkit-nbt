package dev.nautchkafe.nbt;

import dev.nautchkafe.nbt.NbtEffect;
import dev.nautchkafe.nbt.NbtOperation;
import dev.nautchkafe.nbt.NbtDef;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.function.Function;
import java.util.function.Predicate;

final class NbtItem {

    private final ItemStack item;
    private final JavaPlugin plugin;

    private NbtItem(final ItemStack item, final JavaPlugin plugin) {
        this.item = item.clone();
        this.plugin = plugin;
    }

    public static NbtItem modify(final ItemStack item, final JavaPlugin plugin) {
        return new NbtItem(item, plugin);
    }

    public NbtItem apply(final NbtOperation op) {
        return new NbtItem(execute(op).fold(Function.identity(), err -> {
            plugin.getLogger().warning(err);
            return item;
        }), plugin);
    }

    public <TYPE> NbtEffect<TYPE> inspect(final Function<PersistentDataContainer, TYPE> extractor) {
        return withMeta(meta -> NbtEffect.success(extractor.apply(meta.getPersistentDataContainer())));
    }

    public ItemStack done() {
        return item.clone();
    }

    private NbtEffect<ItemStack> execute(final NbtOperation op) {
        return withMeta(meta -> op.execute(meta.getPersistentDataContainer())
            .map(c -> updateItem(meta)));
    }

    private ItemStack updateItem(final ItemMeta meta) {
        ItemStack result = item.clone();
        result.setItemMeta(meta);
        return result;
    }

    private <TYPE> NbtEffect<TYPE> withMeta(final Function<ItemMeta, NbtEffect<TYPE>> action) {
        return NbtEffect.of(() -> {
            final ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                throw new IllegalStateException("No item meta");
            }

            return action.apply(meta);
        });
    }

    public static Function<ItemStack, ItemStack> makeSpecial(final JavaPlugin plugin, final String id) {
        return item -> NbtItem.modify(item, plugin)
            .apply(NbtOperation.write(NbtDef.IS_SPECIAL, (byte) 1) 
                .andThen(NbtOperation.write(NbtDef.ITEM_ID, id)))
                .done();
    }

    public static Predicate<ItemStack> isSpecial(final JavaPlugin plugin) {
        return item -> NbtItem.modify(item, plugin)
            .inspect(container -> container.has(NbtDef.IS_SPECIAL.path(), NbtDef.IS_SPECIAL.type().dataType())) 
            .fold(Function.identity(), e -> false);
    }
}
