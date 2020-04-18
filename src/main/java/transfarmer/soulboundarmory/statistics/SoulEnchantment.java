package transfarmer.soulboundarmory.statistics;

import net.minecraft.enchantment.Enchantment;
import transfarmer.soulboundarmory.statistics.tool.SoulToolEnchantment;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponEnchantment;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;

import static net.minecraft.init.Enchantments.BANE_OF_ARTHROPODS;
import static net.minecraft.init.Enchantments.EFFICIENCY;
import static net.minecraft.init.Enchantments.FIRE_ASPECT;
import static net.minecraft.init.Enchantments.FORTUNE;
import static net.minecraft.init.Enchantments.KNOCKBACK;
import static net.minecraft.init.Enchantments.LOOTING;
import static net.minecraft.init.Enchantments.SHARPNESS;
import static net.minecraft.init.Enchantments.SILK_TOUCH;
import static net.minecraft.init.Enchantments.SMITE;
import static net.minecraft.init.Enchantments.SWEEPING;

public class SoulEnchantment extends Statistic {
    public static final SoulEnchantment SOUL_SHARPNESS = new SoulEnchantment(0, SHARPNESS, "sharpness");
    public static final SoulEnchantment SOUL_SWEEPING_EDGE = new SoulEnchantment(1, SWEEPING, "sweepingEdge");
    public static final SoulEnchantment SOUL_LOOTING = new SoulEnchantment(2, LOOTING, "looting");
    public static final SoulEnchantment SOUL_FIRE_ASPECT = new SoulEnchantment(3, FIRE_ASPECT, "fireAspect");
    public static final SoulEnchantment SOUL_KNOCKBACK = new SoulEnchantment(4, KNOCKBACK, "knockback");
    public static final SoulEnchantment SOUL_SMITE = new SoulEnchantment(5, SMITE, "smite");
    public static final SoulEnchantment SOUL_BANE_OF_ARTHROPODS = new SoulEnchantment(6, BANE_OF_ARTHROPODS, "baneOfArthropods");

    public static final SoulEnchantment SOUL_EFFICIENCY = new SoulEnchantment(0, EFFICIENCY, "efficiency");
    public static final SoulEnchantment SOUL_FORTUNE = new SoulEnchantment(1, FORTUNE, "fortune");
    public static final SoulEnchantment SOUL_SILK_TOUCH = new SoulEnchantment(2, SILK_TOUCH, "silkTouch");

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

    protected SoulEnchantment(final int index, final Enchantment enchantment, final String name) {
        super(index, name);
        this.enchantment = enchantment;
    }

    public static SoulEnchantment[] get() {
        return ENCHANTMENTS;
    }

    public static SoulEnchantment get(final SoulType type, final int index) {
        return type instanceof SoulWeaponType
                ? SoulWeaponEnchantment.get(index)
                : type instanceof SoulToolType
                ? SoulToolEnchantment.get(index)
                : null;
    }

    public static int getAmount() {
        return ENCHANTMENTS.length;
    }

    public Enchantment getEnchantment() {
        return this.enchantment;
    }
}
