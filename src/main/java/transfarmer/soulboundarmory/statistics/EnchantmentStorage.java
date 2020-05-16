package transfarmer.soulboundarmory.statistics;

import nerdhub.cardinal.components.api.util.NbtSerializable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import transfarmer.farmerlib.util.IndexedLinkedHashMap;
import transfarmer.farmerlib.util.IndexedMap;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.function.Predicate;

public class EnchantmentStorage implements Iterable<Enchantment>, NbtSerializable {
    protected final IndexedMap<Enchantment, Integer> enchantments;

    public EnchantmentStorage(final Predicate<Enchantment> predicate) {
        this.enchantments = new IndexedLinkedHashMap<>();

        for (final Enchantment enchantment : this) {
            if (!predicate.test(enchantment)) {
                this.enchantments.remove(enchantment);
            }
        }
    }

    public IndexedMap<Enchantment, Integer> get() {
        return this.enchantments;
    }

    public void add(final Enchantment enchantment, final int value) {
        this.enchantments.put(enchantment, this.enchantments.get(enchantment) + value);
    }

    @Override
    @Nonnull
    public Iterator<Enchantment> iterator() {
        return this.enchantments.keySet().iterator();
    }

    public void reset() {
        for (final Enchantment enchantment : this.enchantments) {
            this.enchantments.put(enchantment, 0);
        }
    }

    public void fromTag(final CompoundTag tag) {
        for (final String key : tag.getKeys()) {
            final Enchantment enchantment = Registry.ENCHANTMENT.get(new Identifier(key));

            if (this.enchantments.containsKey(enchantment)) {
                this.enchantments.put(enchantment, tag.getInt(key));
            }
        }
    }

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull final CompoundTag tag) {
        for (final Enchantment enchantment : this) {
            final Identifier identifier = Registry.ENCHANTMENT.getId(enchantment);

            if (identifier != null) {
                tag.putInt(identifier.toString(), enchantments.get(enchantment));
            }
        }

        return tag;
    }
}
