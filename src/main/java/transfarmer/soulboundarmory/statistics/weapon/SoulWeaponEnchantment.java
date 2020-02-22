package transfarmer.soulboundarmory.statistics.weapon;

import net.minecraft.enchantment.Enchantment;
import transfarmer.soulboundarmory.statistics.SoulEnchantment;

public class SoulWeaponEnchantment extends SoulEnchantment {
    protected static final SoulEnchantment[] ENCHANTMENTS = {
            SOUL_SHARPNESS,
            SOUL_SWEEPING_EDGE,
            SOUL_LOOTING,
            SOUL_FIRE_ASPECT,
            SOUL_KNOCKBACK,
            SOUL_SMITE,
            SOUL_BANE_OF_ARTHROPODS,
    };

    protected SoulWeaponEnchantment(final int index, final Enchantment enchantment, final String name) {
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
