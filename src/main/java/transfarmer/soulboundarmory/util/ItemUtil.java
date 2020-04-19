package transfarmer.soulboundarmory.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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

    public static ItemStack getClassEquippedItemStack(final EntityPlayer player, final Class<?> cls) {
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
}