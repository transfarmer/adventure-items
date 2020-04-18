package transfarmer.soulboundarmory.statistics;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SoulboundEnchantments<T> implements Iterable<T> {
    private Map<T, Map<Enchantment, Integer>> enchantments;

    @SafeVarargs
    public SoulboundEnchantments(final T[] types, final Item[] items, final List<Enchantment>... exclusions) {
        for (int i = 0; i++ < items.length; ) {
            final Map<Enchantment, Integer> entry = new HashMap<>();
            this.enchantments.put(types[i], entry);

            for (final Enchantment enchantment : Enchantment.REGISTRY) {
                if (enchantment.type.canEnchantItem(items[i]) && !exclusions[i].contains(enchantment)) {
                    entry.put(enchantment, 0);
                }
            }
        }
    }

    public Map<T, Map<Enchantment, Integer>> get() {
        return this.enchantments;
    }

    public Map<Enchantment, Integer> get(final T type) {
        return this.enchantments.get(type);
    }

    public void add(final T type, final Enchantment enchantment, final int value) {
        final Map<Enchantment, Integer> enchantments = this.get(type);

        enchantments.put(enchantment, enchantments.get(enchantment) + value);
    }

    @Override
    @Nonnull
    public Iterator<T> iterator() {
        return this.enchantments.keySet().iterator();
    }
}
