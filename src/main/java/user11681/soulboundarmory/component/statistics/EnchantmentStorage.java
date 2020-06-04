package user11681.soulboundarmory.component.statistics;

import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import nerdhub.cardinal.components.api.util.NbtSerializable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import user11681.usersmanual.collections.ArrayMap;
import user11681.usersmanual.collections.OrderedArrayMap;

public class EnchantmentStorage implements Iterable<Enchantment>, NbtSerializable {
    protected final ArrayMap<Enchantment, Integer> enchantments;

    public EnchantmentStorage(final Predicate<Enchantment> predicate) {
        this.enchantments = new OrderedArrayMap<>(() -> 0, Registry.ENCHANTMENT);
        this.enchantments.removeIf(predicate.negate());
    }

    public ArrayMap<Enchantment, Integer> get() {
        return this.enchantments;
    }

    public void add(final Enchantment enchantment, final int value) {
        this.enchantments.put(enchantment, this.enchantments.get(enchantment) + value);
    }

    @Override
    @Nonnull
    public Iterator<Enchantment> iterator() {
        return this.enchantments.iterator();
    }

    public void reset() {
        for (final Enchantment enchantment : this.enchantments) {
            this.enchantments.put(enchantment, 0);
        }
    }

    public void fromTag(final CompoundTag tag) {
        final Registry<Enchantment> registry = Registry.ENCHANTMENT;
        final ArrayMap<Enchantment, Integer> enchantments = this.enchantments;

        for (final String key : tag.getKeys()) {
            final Enchantment enchantment = registry.get(new Identifier(key));

            if (enchantments.containsKey(enchantment)) {
                enchantments.put(enchantment, tag.getInt(key));
            }
        }
    }

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull final CompoundTag tag) {
        final ArrayMap<Enchantment, Integer> enchantments = this.enchantments;
        final Registry<Enchantment> registry = Registry.ENCHANTMENT;

        for (final Enchantment enchantment : this) {
            final Integer level = enchantments.get(enchantment);

            if (level != null) {
                final Identifier identifier = registry.getId(enchantment);

                if (identifier != null) {
                    tag.putInt(identifier.toString(), enchantments.get(enchantment));
                }
            }
        }

        return tag;
    }
}
