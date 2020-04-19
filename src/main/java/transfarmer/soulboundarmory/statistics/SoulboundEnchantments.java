package transfarmer.soulboundarmory.statistics;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.util.IndexedLinkedHashMap;
import transfarmer.soulboundarmory.util.IndexedMap;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

public class SoulboundEnchantments implements Iterable<IItem>, INBTSerializable<NBTTagCompound> {
    private final Map<IItem, IndexedMap<Enchantment, Integer>> enchantments;

    public SoulboundEnchantments(final IItem[] types, final Item[] items, final Predicate<Enchantment> condition) {
        this.enchantments = new HashMap<>();

        for (int i = 0; i < items.length; i++) {
            final IndexedMap<Enchantment, Integer> entry = new IndexedLinkedHashMap<>();

            this.enchantments.put(types[i], entry);

            for (final Enchantment enchantment : Enchantment.REGISTRY) {
                final EnumEnchantmentType type = enchantment.type;

                if (type != null && type.canEnchantItem(items[i]) && condition.test(enchantment)) {
                    entry.put(enchantment, 0);
                }
            }
        }
    }

    public Map<IItem, IndexedMap<Enchantment, Integer>> get() {
        return this.enchantments;
    }

    public IndexedMap<Enchantment, Integer> get(final IItem type) {
        return this.enchantments.get(type);
    }

    public void add(final IItem type, final Enchantment enchantment, final int value) {
        final IndexedMap<Enchantment, Integer> enchantments = this.get(type);

        enchantments.put(enchantment, enchantments.get(enchantment) + value);
    }

    @Override
    @Nonnull
    public Iterator<IItem> iterator() {
        return this.enchantments.keySet().iterator();
    }

    public void reset(final IItem item) {
        final IndexedMap<Enchantment, Integer> enchantments = this.get(item);

        for (final Enchantment enchantment : enchantments) {
            enchantments.put(enchantment, 0);
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound tag = new NBTTagCompound();

        for (final IItem item : this) {
            tag.setTag(item.toString(), this.serializeNBT(item));
        }

        return tag;
    }

    public NBTTagCompound serializeNBT(final IItem item) {
        final NBTTagCompound tag = new NBTTagCompound();
        final IndexedMap<Enchantment, Integer> enchantments = this.get(item);

        for (final Enchantment enchantment : enchantments.keySet()) {
            tag.setInteger(enchantment.getRegistryName().toString(), enchantments.get(enchantment));
        }

        return tag;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound tag) {
        for (final String key : tag.getKeySet()) {
            this.deserializeNBT(tag.getCompoundTag(key), IItem.get(key));
        }
    }

    public void deserializeNBT(final NBTTagCompound tag, final IItem item) {
        for (final String key : tag.getKeySet()) {
            final Enchantment enchantment = Enchantment.getEnchantmentByLocation(key);

            if (enchantment != null) {
                this.enchantments.get(item).put(enchantment, tag.getInteger(key));
            }
        }
    }
}
