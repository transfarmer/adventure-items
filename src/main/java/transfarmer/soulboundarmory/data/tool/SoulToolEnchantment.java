package transfarmer.soulboundarmory.data.tool;

import net.minecraft.enchantment.Enchantment;
import transfarmer.soulboundarmory.data.IEnchantment;

import static net.minecraft.init.Enchantments.*;

public enum SoulToolEnchantment implements IEnchantment {
    SOUL_EFFICIENCY_ENCHANTMENT(0, EFFICIENCY),
    SOUL_FORTUNE(1, FORTUNE),
    SOUL_SILK_TOUCH(2, SILK_TOUCH);

    private final int index;
    private final Enchantment enchantment;

    public static final SoulToolEnchantment[] ENCHANTMENTS = {SOUL_EFFICIENCY_ENCHANTMENT, SOUL_FORTUNE, SOUL_SILK_TOUCH};

    SoulToolEnchantment(final int index, final Enchantment enchantment) {
        this.index = index;
        this.enchantment = enchantment;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static SoulToolEnchantment[] getEnchantments() {
        return ENCHANTMENTS;
    }

    public static SoulToolEnchantment getEnchantment(final int index) {
        return ENCHANTMENTS[index];
    }

    public static String getName(final int index) {
        return getEnchantment(index).toString();
    }
}
