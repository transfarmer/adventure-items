package transfarmer.soulboundarmory.capability.weapon;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;

import static transfarmer.soulboundarmory.statistics.SoulEnchantment.SOUL_FIRE_ASPECT;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.DAGGER;

public class SoulWeaponHelper {
    public static boolean hasSoulWeapon(final EntityPlayer player) {
        final ItemStack[] inventory = player.inventory.mainInventory.toArray(new ItemStack[player.inventory.getSizeInventory() + 1]);
        inventory[player.inventory.getSizeInventory()] = player.getHeldItemOffhand();

        for (final ItemStack itemStack : inventory) {
            if (itemStack != null && isSoulWeapon(itemStack)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSoulWeapon(final ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemSoulWeapon;
    }

    public static boolean isSoulWeaponEquipped(final EntityPlayer player) {
        return player.getHeldItemMainhand().getItem() instanceof ItemSoulWeapon
                || player.getHeldItemOffhand().getItem() instanceof ItemSoulWeapon;
    }

    public static void removeSoulWeapons(final EntityPlayer player) {
        for (final ItemStack itemStack : player.inventory.mainInventory) {
            if (isSoulWeapon(itemStack)) {
                player.inventory.deleteStack(itemStack);
            }
        }
    }

    public static void delegateAttack(final Entity target, final Entity delegate, final EntityPlayer player,
                                      final ItemStack itemStack, final float attackDamage) {
        final ISoulWeapon capability = SoulWeaponProvider.get(player);

        final DamageSource damageSource = player == null
                ? DamageSource.causeThrownDamage(delegate, delegate)
                : DamageSource.causeThrownDamage(delegate, player);

        int burnTime = 0;

        final float attackDamageModifier = target instanceof EntityLivingBase
                ? EnchantmentHelper.getModifierForCreature(itemStack, ((EntityLivingBase) target).getCreatureAttribute())
                : EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED);

        if (attackDamage > 0 || attackDamageModifier > 0) {
            final int knockbackModifier = EnchantmentHelper.getKnockbackModifier(player);
            float initialHealth = 0;

            burnTime += EnchantmentHelper.getFireAspectModifier(player);

            if (target instanceof EntityLivingBase) {
                initialHealth = ((EntityLivingBase) target).getHealth();

                if (delegate.isBurning()) {
                    burnTime += 5;
                }

                if (capability.getEnchantments(DAGGER).containsKey(SOUL_FIRE_ASPECT) && !(target instanceof EntityEnderman)) {
                    burnTime += capability.getEnchantment(SOUL_FIRE_ASPECT, DAGGER) * 4;
                }

                if (burnTime > 0 && !target.isBurning()) {
                    target.setFire(1);
                }
            }

            if (target.attackEntityFrom(damageSource, attackDamage)) {
                if (knockbackModifier > 0) {
                    if (target instanceof EntityLivingBase) {
                        ((EntityLivingBase) target).knockBack(player, knockbackModifier * 0.5F, MathHelper.sin(player.rotationYaw * 0.017453292F), -MathHelper.cos(player.rotationYaw * 0.017453292F));
                    } else {
                        target.addVelocity(-MathHelper.sin(player.rotationYaw * 0.017453292F) * knockbackModifier * 0.5F, 0.1D, MathHelper.cos(player.rotationYaw * 0.017453292F) * knockbackModifier * 0.5F);
                    }
                }

                if (attackDamageModifier > 0) {
                    player.onEnchantmentCritical(target);
                }

                player.setLastAttackedEntity(target);

                if (target instanceof EntityLivingBase) {
                    EnchantmentHelper.applyThornEnchantments((EntityLivingBase) target, player);
                }

                EnchantmentHelper.applyArthropodEnchantments(player, target);

                if (target instanceof EntityLivingBase) {
                    final float damageDealt = initialHealth - ((EntityLivingBase) target).getHealth();
                    player.addStat(StatList.DAMAGE_DEALT, Math.round(damageDealt * 10));

                    if (burnTime > 0) {
                        target.setFire(burnTime);
                    }

                    if (player.world instanceof WorldServer && damageDealt > 2) {
                        int k = (int) ((double) damageDealt * 0.5);
                        ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, target.posX, target.posY + target.height * 0.5, target.posZ, k, 0.1, 0, 0.1, 0.2);
                    }
                }
            }
        } else {
            if (burnTime > 0) {
                target.extinguish();
            }
        }
    }
}
