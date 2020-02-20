package transfarmer.soulboundarmory.statistics;

import net.minecraft.enchantment.Enchantment;

import static net.minecraft.init.Enchantments.*;

public class SoulEnchantment extends Statistic {
    public static final SoulEnchantment SOUL_SHARPNESS = new SoulEnchantment(0, SHARPNESS);
    public static final SoulEnchantment SOUL_SWEEPING_EDGE = new SoulEnchantment(1, SWEEPING);
    public static final SoulEnchantment SOUL_LOOTING = new SoulEnchantment(2, LOOTING);
    public static final SoulEnchantment SOUL_FIRE_ASPECT = new SoulEnchantment(3, FIRE_ASPECT);
    public static final SoulEnchantment SOUL_KNOCKBACK = new SoulEnchantment(4, KNOCKBACK);
    public static final SoulEnchantment SOUL_SMITE = new SoulEnchantment(5, SMITE);
    public static final SoulEnchantment SOUL_BANE_OF_ARTHROPODS = new SoulEnchantment(6, BANE_OF_ARTHROPODS);

    public static final SoulEnchantment SOUL_EFFICIENCY = new SoulEnchantment(0, EFFICIENCY);
    public static final SoulEnchantment SOUL_FORTUNE = new SoulEnchantment(1, FORTUNE);
    public static final SoulEnchantment SOUL_SILK_TOUCH = new SoulEnchantment(2, SILK_TOUCH);

    protected static final SoulEnchantment[] ENCHANTMENTS = {
            SOUL_SHARPNESS,
            SOUL_SWEEPING_EDGE,
            SOUL_LOOTING,
            SOUL_FIRE_ASPECT,
            SOUL_KNOCKBACK,
            SOUL_SMITE,
            SOUL_BANE_OF_ARTHROPODS,
            SOUL_EFFICIENCY,
            SOUL_FORTUNE,
            SOUL_SILK_TOUCH,
    };

    private final Enchantment enchantment;

    protected SoulEnchantment(final int index, final Enchantment enchantment) {
        super(index);
        this.enchantment = enchantment;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof SoulEnchantment && ((SoulEnchantment) obj).index == this.index;
    }

    public static String getName(final int index) {
        return get(index).toString().toLowerCase();
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

    public Enchantment getEnchantment() {
        return this.enchantment;
    }
}
