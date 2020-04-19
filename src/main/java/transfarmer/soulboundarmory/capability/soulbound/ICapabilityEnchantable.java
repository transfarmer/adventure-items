package transfarmer.soulboundarmory.capability.soulbound;

import net.minecraft.enchantment.Enchantment;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.util.IndexedMap;

public interface ICapabilityEnchantable extends IItemCapability {
    int getEnchantment(IItem type, Enchantment enchantment);

    IndexedMap<Enchantment, Integer> getEnchantments();

    IndexedMap<Enchantment, Integer> getEnchantments(IItem type);

    void addEnchantment(IItem type, Enchantment enchantment, int amount);

    void resetEnchantments(IItem item);
}
