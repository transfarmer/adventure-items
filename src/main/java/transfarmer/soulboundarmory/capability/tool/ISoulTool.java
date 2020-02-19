package transfarmer.soulboundarmory.capability.tool;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.data.IEnchantment;
import transfarmer.soulboundarmory.data.IType;

import java.util.Map;

public interface ISoulTool extends ISoulCapability {
    float getEffectiveEfficiency(IType type);

    ItemStack getItemStack(IType type);

    ItemStack getItemStack(ItemStack itemStack);

    AttributeModifier[] getAttributeModifiers(IType type);

    Map<IEnchantment, Integer> getEnchantments(IType type);

    String[] getTooltip(IType type);

    float getEffectiveReachDistance(IType type);
}
