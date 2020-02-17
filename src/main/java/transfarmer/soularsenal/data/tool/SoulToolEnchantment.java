package transfarmer.soularsenal.data.tool;

import net.minecraft.enchantment.Enchantment;

import static net.minecraft.init.Enchantments.*;

public enum SoulToolEnchantment {
    SOUL_EFFICIENCY_ENCHANTMENT(EFFICIENCY, 0),
    SOUL_FORTUNE(FORTUNE, 1),
    SOUL_SILK_TOUCH(SILK_TOUCH, 2);

    private final Enchantment enchantment;
    public final int index;

    public static final SoulToolEnchantment[] ENCHANTMENTS = {SOUL_EFFICIENCY_ENCHANTMENT, SOUL_FORTUNE, SOUL_SILK_TOUCH};

    SoulToolEnchantment(final Enchantment enchantment, final int index) {
        this.enchantment = enchantment;
        this.index = index;
    }

    public static SoulToolEnchantment[] getEnchantments() {
        return ENCHANTMENTS;
    }

    public static SoulToolEnchantment getEnchantment(final int index) {
        return ENCHANTMENTS[index];
    }

    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    public static String getName(final int index) {
        return getEnchantment(index).toString().toLowerCase();
    }
}
