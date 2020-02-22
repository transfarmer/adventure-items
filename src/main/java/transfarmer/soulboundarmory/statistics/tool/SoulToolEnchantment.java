package transfarmer.soulboundarmory.statistics.tool;

import net.minecraft.enchantment.Enchantment;
import transfarmer.soulboundarmory.statistics.SoulEnchantment;

public class SoulToolEnchantment extends SoulEnchantment {
    protected static final SoulEnchantment[] ENCHANTMENTS = {
            SOUL_EFFICIENCY,
            SOUL_FORTUNE,
            SOUL_SILK_TOUCH,
    };

    protected SoulToolEnchantment(final int index, final Enchantment enchantment, final String name) {
        super(index, enchantment, name);
    }

    public static SoulEnchantment[] get() {
        return ENCHANTMENTS;
    }

    public static SoulEnchantment get(final int index) {
        return ENCHANTMENTS[index];
    }

    public static int getAmount() {
        return ENCHANTMENTS.length;
    }
}
