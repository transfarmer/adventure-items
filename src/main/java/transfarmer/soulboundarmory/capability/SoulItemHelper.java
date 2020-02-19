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

    public static boolean addItemStack(final ItemStack itemStack, final EntityPlayer player) {
        final ISoulCapability capability = getCapability(player, itemStack.getItem());
        final InventoryPlayer inventory = player.inventory;
        final ItemStack[] mainInventory = inventory.mainInventory.toArray(new ItemStack[40]);
        mainInventory[mainInventory.length - 1] = player.getHeldItemOffhand();

        if (isSoulItem(itemStack)) {
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

            for (int index = 0; index < mainInventory.length; index++) {
                if (index != capability.getBoundSlot() && index != capability.getBoundSlot() && mainInventory[index].isEmpty()) {
                    return inventory.add(index, itemStack);
                }
            }

            return false;
        }

        return inventory.add(capability.getBoundSlot() >= 0 && inventory.getStackInSlot(capability.getBoundSlot()).isEmpty()
                ? capability.getBoundSlot()
                : inventory.getFirstEmptyStack(), itemStack);
    }

    public static void forEach(final ISoulCapability capability,
                               final BiConsumer<Integer, Integer> data,
                               final BiConsumer<Integer, Integer> attributes,
                               final BiConsumer<Integer, Integer> enchantments) {
        for (int toolIndex = 0; toolIndex < capability.getItemAmount(); toolIndex++) {
            for (int valueIndex = 0; valueIndex < Math.max(capability.getDatumAmount(), Math.max(capability.getAttributeAmount(), capability.getEnchantmentAmount())); valueIndex++) {
                if (valueIndex < capability.getDatumAmount()) {
                    data.accept(toolIndex, valueIndex);
                }

                if (valueIndex < capability.getAttributeAmount()) {
                    attributes.accept(toolIndex, valueIndex);
                }

                if (valueIndex < capability.getEnchantmentAmount()) {
                    enchantments.accept(toolIndex, valueIndex);
                }
            }
        }
    }

}
