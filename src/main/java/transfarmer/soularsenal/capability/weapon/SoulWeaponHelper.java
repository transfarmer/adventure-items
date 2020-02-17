package transfarmer.soularsenal.capability.weapon;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import transfarmer.soularsenal.data.weapon.SoulWeaponAttribute;
import transfarmer.soularsenal.data.weapon.SoulWeaponDatum;
import transfarmer.soularsenal.data.weapon.SoulWeaponEnchantment;
import transfarmer.soularsenal.data.weapon.SoulWeaponType;

import java.util.UUID;
import java.util.function.BiConsumer;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static transfarmer.soularsenal.capability.weapon.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soularsenal.data.weapon.SoulWeaponEnchantment.FIRE_ASPECT;
import static transfarmer.soularsenal.data.weapon.SoulWeaponType.DAGGER;

public class SoulWeaponHelper {
    public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    public static final UUID ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    public static final UUID REACH_DISTANCE_UUID = UUID.fromString("CD407CC4-2214-4ECA-B4B6-7DCEE2DABA33");
    public static final int DATA = SoulWeaponDatum.getData().length;
    public static final int ATTRIBUTES = SoulWeaponAttribute.getAttributes().length;
    public static final int ENCHANTMENTS = SoulWeaponEnchantment.getEnchantments().length;
    private static final String[][] skills = {
        {"charge"},
        {"lightning bolt"},
        {"throwing", "perforation", "return", "sneak return"}
    };
    private static boolean datumEquality;

    public static void forEach(BiConsumer<Integer, Integer> datumConsumer,
                               BiConsumer<Integer, Integer> attributeConsumer,
                               BiConsumer<Integer, Integer> enchantmentConsumer) {
        for (int weaponIndex = 0; weaponIndex <= 2; weaponIndex++) {
            for (int valueIndex = 0; valueIndex < Math.max(DATA, Math.max(ATTRIBUTES, ENCHANTMENTS)); valueIndex++) {
                if (valueIndex < DATA) {
                    datumConsumer.accept(weaponIndex, valueIndex);
                }

                if (valueIndex < ATTRIBUTES) {
                    attributeConsumer.accept(weaponIndex, valueIndex);
                }

                if (valueIndex < ENCHANTMENTS) {
                    enchantmentConsumer.accept(weaponIndex, valueIndex);
                }
            }
        }
    }

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
        inventory[player.inventory.getSizeInventory()] = (player.inventory.getStackInSlot(40));

        for (final ItemStack itemStack : inventory) {
            if (itemStack != null && isSoulWeapon(itemStack)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSoulWeapon(final ItemStack itemStack) {
        return SoulWeaponType.getItems().contains(itemStack.getItem());
    }

    public static boolean isSoulWeaponEquipped(final EntityPlayer player) {
        return SoulWeaponType.getItems().contains(player.inventory.getCurrentItem().getItem());
    }

    public static void removeSoulWeapons(final EntityPlayer player) {
        for (final ItemStack itemStack : player.inventory.mainInventory) {
            if (isSoulWeapon(itemStack)) {
                player.inventory.deleteStack(itemStack);
            }
        }
    }

    public static boolean addItemStack(final ItemStack itemStack, final EntityPlayer player) {
        final ISoulWeapon capability = player.getCapability(CAPABILITY, null);
        final InventoryPlayer inventory = player.inventory;
        final NonNullList<ItemStack> mainInventory = inventory.mainInventory;

        if (!isSoulWeapon(itemStack)) {
            final int slot = inventory.storeItemStack(itemStack);

            if (slot != -1) {
                final ItemStack slotStack = inventory.getStackInSlot(slot);
                final int transferred = Math.min(slotStack.getMaxStackSize() - slotStack.getCount(), itemStack.getCount());

                itemStack.setCount(itemStack.getCount() - transferred);
                slotStack.setCount(slotStack.getCount() + transferred);

                if (itemStack.getCount() > 0) {
                    return addItemStack(itemStack, player);
                }

                return true;
            }

            for (int index = 0; index < mainInventory.size(); index++) {
                if (index != capability.getBoundSlot() && mainInventory.get(index).isEmpty()) {
                    return inventory.add(index, itemStack);
                }
            }

            return false;
        }

        return inventory.add(capability.getBoundSlot() >= 0 && inventory.getStackInSlot(capability.getBoundSlot()).isEmpty()
            ? capability.getBoundSlot() : inventory.getFirstEmptyStack(), itemStack);
    }

    public static int getMaxSkills(SoulWeaponType type) {
        return SoulWeaponHelper.getSkills()[type.index].length;
    }

    public static String[][] getSkills() {
        return skills;
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
