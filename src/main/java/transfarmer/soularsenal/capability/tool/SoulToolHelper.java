package transfarmer.soularsenal.capability.tool;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import transfarmer.soularsenal.data.tool.SoulToolType;
import transfarmer.soularsenal.item.ItemSoulTool;

import java.util.UUID;
import java.util.function.BiConsumer;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraft.util.EnumHand.MAIN_HAND;
import static net.minecraft.util.EnumHand.OFF_HAND;

public class SoulToolHelper {
    public static final UUID REACH_DISTANCE_UUID = UUID.fromString("CD407CC4-2214-4ECA-B4B6-7DCEE2DABA33");
    public static final int SOUL_TOOLS = 1;
    public static final int DATA = 7;
    public static final int ATTRIBUTES = 3;
    public static final int ENCHANTMENTS = 3;
    private static final String[][] skills = {{}};
    private static boolean datumEquality;

    public static boolean areEmpty(final int[][] data, final float[][] attributes, final int[][] enchantments) {
        for (int toolIndex = 0; toolIndex < SOUL_TOOLS; toolIndex++) {
            if (data[toolIndex].length == 0 || attributes[toolIndex].length == 0 || enchantments[toolIndex].length == 0) {
                return true;
            }
        }

        return false;
    }

    public static void forEach(final BiConsumer<Integer, Integer> data,
                               final BiConsumer<Integer, Integer> attributes,
                               final BiConsumer<Integer, Integer> enchantments) {
        for (int toolIndex = 0; toolIndex < SOUL_TOOLS; toolIndex++) {
            for (int valueIndex = 0; valueIndex < Math.max(DATA, Math.max(ATTRIBUTES, ENCHANTMENTS)); valueIndex++) {
                if (valueIndex < DATA) {
                    data.accept(toolIndex, valueIndex);
                }

                if (valueIndex < ATTRIBUTES) {
                    attributes.accept(toolIndex, valueIndex);
                }

                if (valueIndex < ENCHANTMENTS) {
                    enchantments.accept(toolIndex, valueIndex);
                }
            }
        }
    }

    public static boolean isSoulTool(final ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemSoulTool;
    }

    public static boolean isSoulToolEquipped(final EntityPlayer player) {
        return isSoulTool(player.getHeldItem(MAIN_HAND)) || isSoulTool(player.getHeldItem(OFF_HAND));
    }

    public static boolean hasSoulTool(final EntityPlayer player) {
        for (final ItemStack itemStack : player.inventory.mainInventory) {
            if (itemStack.getItem() instanceof ItemSoulTool) return true;
        }

        return player.getHeldItemOffhand().getItem() instanceof ItemSoulTool;
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

    public static void removeSoulTools(final EntityPlayer player) {
        for (final ItemStack itemStack : player.inventory.mainInventory) {
            if (isSoulTool(itemStack)) {
                player.inventory.deleteStack(itemStack);
            }
        }
    }

    public static boolean addItemStack(final ItemStack itemStack, final EntityPlayer player) {
        final ISoulTool capability = SoulToolProvider.get(player);
        final InventoryPlayer inventory = player.inventory;
        final NonNullList<ItemStack> mainInventory = inventory.mainInventory;

        if (!isSoulTool(itemStack)) {
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

    public static int getMaxSkills(SoulToolType type) {
        return SoulToolHelper.getSkills()[type.index].length;
    }

    public static String[][] getSkills() {
        return skills;
    }
}
