package transfarmer.soulboundarmory.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class SoulItemHelper {
    public static ISoulCapability getCapability(final Class<ISoulCapability> cls, final EntityPlayer player) {
        try {
            if (cls.newInstance() instanceof ISoulWeapon) {
                return SoulWeaponProvider.get(player);
            } else if (cls.newInstance() instanceof ISoulTool) {
                return SoulToolProvider.get(player);
            }
        } catch (InstantiationException | IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static ISoulCapability getCapability(final EntityPlayer player, @Nullable Item item) {
        if (item == null) {
            item = player.getHeldItemMainhand().getItem();
        }

        return item instanceof ItemSoulWeapon
                ? SoulWeaponProvider.get(player)
                : item instanceof IItemSoulTool
                ? SoulToolProvider.get(player)
                : null;
    }

    public static boolean isSoulItem(final ItemStack itemStack) {
        return itemStack.getItem() instanceof ISoulItem;
    }

    public static boolean addItemStack(final ItemStack itemStack, final EntityPlayer player, boolean hasReservedSlot) {
        final int weaponBoundSlot = SoulWeaponProvider.get(player).getBoundSlot();
        final int toolBoundSlot = SoulToolProvider.get(player).getBoundSlot();

        if (!isSoulItem(itemStack)) {
            hasReservedSlot = false;
        }

        final InventoryPlayer inventory = player.inventory;
        final ItemStack[] mainInventory = inventory.mainInventory.toArray(new ItemStack[40]);
        mainInventory[mainInventory.length - 1] = player.getHeldItemOffhand();

        if (!hasReservedSlot) {
            final int slot = inventory.storeItemStack(itemStack);

            if (slot != -1) {
                final ItemStack slotStack = inventory.getStackInSlot(slot);
                final int transferred = Math.min(slotStack.getMaxStackSize() - slotStack.getCount(), itemStack.getCount());

                itemStack.setCount(itemStack.getCount() - transferred);
                slotStack.setCount(slotStack.getCount() + transferred);

                if (itemStack.getCount() > 0) {
                    return addItemStack(itemStack, player, false);
                }

                return true;
            }

            for (int index = 0; index < mainInventory.length; index++) {
                if (index != weaponBoundSlot && index != toolBoundSlot && mainInventory[index].isEmpty()) {
                    return inventory.add(index, itemStack);
                }
            }

            return false;
        }

        final ISoulCapability capability = getCapability(player, itemStack.getItem());
        final int boundSlot = capability.getBoundSlot();

        if (boundSlot >= 0 && inventory.getStackInSlot(boundSlot).isEmpty()) {
            return inventory.add(boundSlot, itemStack);
        }

        return addItemStack(itemStack, player, false);
    }

    public static void forEach(final ISoulCapability capability,
                               final BiConsumer<Integer, Integer> data,
                               final BiConsumer<Integer, Integer> attributes,
                               final BiConsumer<Integer, Integer> enchantments) {
        for (int itemIndex = 0; itemIndex < capability.getItemAmount(); itemIndex++) {
            for (int valueIndex = 0; valueIndex < Math.max(capability.getDatumAmount(), Math.max(capability.getAttributeAmount(), capability.getEnchantmentAmount())); valueIndex++) {
                if (valueIndex < capability.getDatumAmount()) {
                    data.accept(itemIndex, valueIndex);
                }

                if (valueIndex < capability.getAttributeAmount()) {
                    attributes.accept(itemIndex, valueIndex);
                }

                if (valueIndex < capability.getEnchantmentAmount()) {
                    enchantments.accept(itemIndex, valueIndex);
                }
            }
        }
    }

    public static boolean areEmpty(final ISoulCapability capability, final int[][] data, final float[][] attributes, final int[][] enchantments) {
        for (int index = 0; index < capability.getItemAmount(); index++) {
            if (data[index].length == 0 || attributes[index].length == 0 || enchantments[index].length == 0) {
                return true;
            }
        }

        return false;
    }

    public static boolean addItemStack(final ItemStack itemStack, final EntityPlayer player) {
        return addItemStack(itemStack, player, true);
    }
}
