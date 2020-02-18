package transfarmer.soulboundarmory.data.weapon;

import net.minecraft.enchantment.Enchantment;

public enum SoulWeaponEnchantment {
    SHARPNESS(0, Enchantment.getEnchantmentByLocation("sharpness")),
    SWEEPING_EDGE(1, Enchantment.getEnchantmentByLocation("sweeping")),
    LOOTING(2, Enchantment.getEnchantmentByLocation("looting")),
    FIRE_ASPECT(3, Enchantment.getEnchantmentByLocation("fire_aspect")),
    KNOCKBACK(4, Enchantment.getEnchantmentByLocation("knockback")),
    SMITE(5, Enchantment.getEnchantmentByLocation("smite")),
    BANE_OF_ARTHROPODS(6, Enchantment.getEnchantmentByLocation("bane_of_arthropods"));

    private static final SoulWeaponEnchantment[] enchantments = {SHARPNESS, SWEEPING_EDGE, LOOTING, FIRE_ASPECT, KNOCKBACK, SMITE, BANE_OF_ARTHROPODS};

    public final int index;
    public final Enchantment enchantment;

    SoulWeaponEnchantment(final int index, final Enchantment enchantment) {
        this.index = index;
        this.enchantment = enchantment;
    }

    public static SoulWeaponEnchantment getEnchantment(int index) {
        return enchantments[index];
    }

    public static String getName(int index) {
        return getEnchantment(index).toString();
    }

    public static SoulWeaponEnchantment[] getEnchantments() {
        return enchantments;
    }
}
