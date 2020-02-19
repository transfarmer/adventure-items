package transfarmer.soulboundarmory.capability.weapon;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.data.IEnchantment;
import transfarmer.soulboundarmory.data.IType;

import java.util.Map;

public interface ISoulWeapon extends ISoulCapability {
    float getAttackSpeed(IType type);

    float getEffectiveAttackSpeed(IType type);

    float getAttackDamage(IType type);

    float getEffectiveAttackDamage(IType type);

    ItemStack getItemStack(ItemStack itemStack);

    ItemStack getItemStack(IType weaponType);

    AttributeModifier[] getAttributeModifiers(IType weaponType);

    Map<IEnchantment, Integer> getEnchantments(IType weaponType);

    String[] getTooltip(IType weaponType);

    void resetCooldown(IType type);

    void addCooldown(int ticks);

    int getAttackCooldown();

    int getCooldown(IType type);

    float getAttackRatio(IType type);

    void setAttackCooldown(int ticks);

    int getLightningCooldown();

    void resetLightningCooldown();

    void decrementLightningCooldown();
}
