package transfarmer.soulboundarmory.capability.weapon;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;

import java.util.UUID;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponEnchantment.FIRE_ASPECT;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponType.DAGGER;

public class SoulWeaponHelper {
    public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    public static final UUID ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    public static final UUID REACH_DISTANCE_UUID = UUID.fromString("CD407CC4-2214-4ECA-B4B6-7DCEE2DABA33");
    private static boolean datumEquality;

    public static boolean areDataEqual(ItemStack itemStack0, ItemStack itemStack1) {
        datumEquality = true;

        itemStack0.getAttributeModifiers(MAINHAND).forEach((String key, AttributeModifier modifier0) -> {
            if (!datumEquality) return;

            itemStack1.getAttributeModifiers(MAINHAND).get(key).forEach((AttributeModifier modifier1) -> {
                if (!datumEquality) return;

                if (!modifier0.getID().equals(modifier1.getID()) || modifier0.getAmount() != modifier1.getAmount()) {
                    datumEquality = false;
                }
            });
        });

        if (datumEquality) {
            final NBTTagList enchantmentTagList0 = itemStack0.getEnchantmentTagList();
            final NBTTagList enchantmentTagList1 = itemStack1.getEnchantmentTagList();
            final int listEntries = Math.max(enchantmentTagList0.tagCount(), enchantmentTagList1.tagCount());

            for (int i = 0; i < listEntries; i++) {
                final NBTTagCompound tagCompound0 = enchantmentTagList0.getCompoundTagAt(i);
                final NBTTagCompound tagCompound1 = enchantmentTagList1.getCompoundTagAt(i);

                if (tagCompound0.getSize() == tagCompound1.getSize()) {
                    final int compoundEntries = Math.max(tagCompound0.getSize(), tagCompound1.getSize());

                    for (int j = 0; j < compoundEntries; j++) {
                        for (final String key : tagCompound1.getKeySet()) {
                            if (tagCompound0.getShort(key) != tagCompound1.getShort(key)) {
                                return false;
                            }
                        }
                    }
                } else {
                    return false;
                }
            }
        }

        return datumEquality;
    }

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

                if (capability.getEnchantments(DAGGER).containsKey(FIRE_ASPECT) && !(target instanceof EntityEnderman)) {
                    burnTime += capability.getEnchantment(FIRE_ASPECT, DAGGER) * 4;
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
