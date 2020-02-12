package transfarmer.soulweapons.capability;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import transfarmer.soulweapons.data.SoulWeaponAttribute;
import transfarmer.soulweapons.data.SoulWeaponDatum;
import transfarmer.soulweapons.data.SoulWeaponEnchantment;
import transfarmer.soulweapons.data.SoulWeaponType;

import java.util.UUID;
import java.util.function.BiConsumer;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class SoulWeaponHelper {
    public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    public static final UUID ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    public static final UUID REACH_DISTANCE_UUID = UUID.fromString("CD407CC4-2214-4ECA-B4B6-7DCEE2DABA33");
    public static final int DATA_LENGTH = SoulWeaponDatum.getData().length;
    public static final int ATTRIBUTES = SoulWeaponAttribute.getAttributes().length;
    public static final int ENCHANTMENTS = SoulWeaponEnchantment.getEnchantments().length;
    private static final String[][] skills = {
        {"charge"},
        {}, // lightning strike?
        {"throwing", "perforation", "return", "sneak return"}
    };
    private static boolean datumEquality;

    public static void forEach(BiConsumer<Integer, Integer> datumConsumer,
                               BiConsumer<Integer, Integer> attributeConsumer,
                               BiConsumer<Integer, Integer> enchantmentConsumer) {
        for (int weaponIndex = 0; weaponIndex <= 2; weaponIndex++) {
            for (int valueIndex = 0; valueIndex <= Math.max(DATA_LENGTH, Math.max(ATTRIBUTES, ENCHANTMENTS)); valueIndex++) {
                if (valueIndex <= DATA_LENGTH - 1) {
                    datumConsumer.accept(weaponIndex, valueIndex);
                }

                if (valueIndex <= ATTRIBUTES - 1) {
                    attributeConsumer.accept(weaponIndex, valueIndex);
                }

                if (valueIndex <= ENCHANTMENTS - 1) {
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
        for (final SoulWeaponType type : SoulWeaponType.getTypes()) {
            if (player.inventory.hasItemStack(player.getCapability(CAPABILITY, null).getItemStack(type))) {
                return true;
            }
        }

        return false;
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

    public static boolean isSoulWeapon(final ItemStack itemStack) {
        return SoulWeaponType.getItems().contains(itemStack.getItem());
    }

    public static boolean addItemStack(final ItemStack itemStack, final EntityPlayer player) {
        final ISoulWeapon capability = player.getCapability(CAPABILITY, null);
        final InventoryPlayer inventory = player.inventory;
        final NonNullList<ItemStack> mainInventory = inventory.mainInventory;

        if (!isSoulWeapon(itemStack)) {
            final int slot = inventory.storeItemStack(itemStack);

            if (slot != -1) {
                final ItemStack slotStack = mainInventory.get(slot);
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
        }

        return inventory.add(mainInventory.get(capability.getBoundSlot()).getItem() == Items.AIR
            ? capability.getBoundSlot() : inventory.getFirstEmptyStack(), itemStack);
    }

    public static int getMaxSkills(SoulWeaponType type) {
        return SoulWeaponHelper.getSkills()[type.index].length;
    }

    public static String[][] getSkills() {
        return skills;
    }
}
