package transfarmer.soulboundarmory.enchantment;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;
import transfarmer.soulboundarmory.item.StaffItem;

public class EnchantmentTypes {
    public static final EnumEnchantmentType STAFF = EnumHelper.addEnchantmentType("staff", (final Item item) -> item instanceof StaffItem);
}
