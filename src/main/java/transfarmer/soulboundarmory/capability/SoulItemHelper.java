package transfarmer.soulboundarmory.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;

import static transfarmer.soulboundarmory.capability.tool.SoulToolHelper.isSoulTool;
import static transfarmer.soulboundarmory.capability.weapon.SoulWeaponHelper.isSoulWeapon;

public class SoulItemHelper {
    public static boolean addItemStack(final ItemStack itemStack, final EntityPlayer player) {
        final ISoulWeapon weaponCapability = SoulWeaponProvider.get(player);
        final ISoulTool toolCapablity = SoulToolProvider.get(player);
        final InventoryPlayer inventory = player.inventory;
        final ItemStack[] mainInventory = inventory.mainInventory.toArray(new ItemStack[40]);
        mainInventory[mainInventory.length - 1] = player.getHeldItemOffhand();

        if (!isSoulWeapon(itemStack) && !isSoulTool(itemStack)) {
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
                if (index != weaponCapability.getBoundSlot() && index != toolCapablity.getBoundSlot() && mainInventory[index].isEmpty()) {
                    return inventory.add(index, itemStack);
                }
            }

            return false;
        }

        if (isSoulWeapon(itemStack)) {
            return inventory.add(weaponCapability.getBoundSlot() >= 0 && inventory.getStackInSlot(weaponCapability.getBoundSlot()).isEmpty()
                    ? weaponCapability.getBoundSlot()
                    : inventory.getFirstEmptyStack(), itemStack);
        }

        return inventory.add(toolCapablity.getBoundSlot() >= 0 && inventory.getStackInSlot(toolCapablity.getBoundSlot()).isEmpty()
                ? toolCapablity.getBoundSlot()
                : inventory.getFirstEmptyStack(), itemStack);
    }

}
