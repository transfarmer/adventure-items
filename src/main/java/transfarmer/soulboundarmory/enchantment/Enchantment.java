package transfarmer.soulboundarmory.enchantment;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class Enchantment extends net.minecraft.enchantment.Enchantment {
    protected Enchantment(final Rarity rarityIn, final EnumEnchantmentType typeIn,
                          final EntityEquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
    }
}
