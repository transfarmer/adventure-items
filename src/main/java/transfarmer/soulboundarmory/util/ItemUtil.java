package transfarmer.soulboundarmory.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.Arrays;
import java.util.List;

public class ItemUtil {
    public static boolean hasItem(final Item item, final EntityPlayer player) {
        for (final ItemStack itemStack : player.inventory.mainInventory) {
            if (itemStack.getItem() == item) {
                return true;
            }
        }

        return false;
    }

    public static ItemStack getEquippedItemStack(final EntityPlayer player, final Item... validItems) {
        final ItemStack mainhandStack = player.getHeldItemMainhand();
        final List<Item> itemList = Arrays.asList(validItems);

        if (itemList.contains(mainhandStack.getItem())) {
            return mainhandStack;
        }

        final ItemStack offhandStack = player.getHeldItemOffhand();

        if (itemList.contains(offhandStack.getItem())) {
            return offhandStack;
        }

        return null;
    }

    public static ItemStack getEquippedItemStack(final EntityPlayer player, final Class<?> cls) {
        final ItemStack mainhandStack = player.getHeldItemMainhand();

        if (cls.isInstance(mainhandStack.getItem())) {
            return mainhandStack;
        }

        final ItemStack offhandStack = player.getHeldItemOffhand();

        if (cls.isInstance(offhandStack.getItem())) {
            return offhandStack;
        }

        return null;
    }

    public static ItemStack getRequiredItemStack(final EntityPlayer player, final EntityEquipmentSlot slot, final Class<?>... classes) {
        final ItemStack itemStack = player.getHeldItemMainhand();
        final Item item = itemStack.getItem();

        for (final Class<?> clazz : classes) {
            if (clazz.isInstance(item)) {
                return itemStack;
            }
        }

        return null;
    }

    public static boolean hasItem(final EntityPlayer player, final Item item, final int min, final int max) {
        final InventoryPlayer inventory = player.inventory;
        final NonNullList<ItemStack> merged = CollectionUtil.merge(inventory.mainInventory, inventory.offHandInventory);

        for (int i = min; i < max; i++) {
            final ItemStack itemStack = merged.get(i);

            if (itemStack.getItem() == item) {
                return true;
            }
        }

        return false;
    }
}