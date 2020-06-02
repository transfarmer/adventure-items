package user11681.soulboundarmory.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ImpactEnchantment extends Enchantment {
    public ImpactEnchantment() {
        super(Weight.COMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaximumLevel() {
        return 5;
    }

    @Override
    public int getMinimumPower(final int level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaximumPower(final int level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public float getAttackDamage(final int level, @Nonnull final EntityGroup group) {
        return 1 + Math.max(0, level - 1) / 2F;
    }

    @Override
    public boolean isAcceptableItem(final ItemStack stack) {
        return super.isAcceptableItem(stack);
    }
}
