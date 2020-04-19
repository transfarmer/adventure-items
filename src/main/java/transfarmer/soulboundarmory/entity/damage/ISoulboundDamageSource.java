package transfarmer.soulboundarmory.entity.damage;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public interface ISoulboundDamageSource {
    ItemStack getItemStack();

    DamageSource setItemStack(ItemStack itemStack);
}
