package transfarmer.soulboundarmory.network.server.weapon;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

public class C2SWeaponType implements IExtendedMessage {
    private String item;

    public C2SWeaponType() {
    }

    public C2SWeaponType(final IItem item) {
        this.item = item.toString();
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buf) {
        this.item = buf.readString();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buf) {
        buf.writeString(item);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SWeaponType> {
        @Override
        public IExtendedMessage onMessage(C2SWeaponType message, MessageContext context) {
            final EntityPlayerMP player = context.getServerHandler().player;
            final IWeapon instance = WeaponProvider.get(player);
            final IItem type = instance.getItemType(message.item);
            int slot = instance.getBoundSlot();
            instance.setItemType(type);

            if (!SoulItemHelper.hasSoulWeapon(player)) {
                player.inventory.clearMatchingItems(Items.WOODEN_SWORD, -1, 37, null);
            } else {
                SoulItemHelper.removeSoulWeapons(player);
            }

            if (slot >= 0) {
                final ItemStack boundSlotItem = player.inventory.getStackInSlot(slot);

                if (boundSlotItem.getItem() != Items.AIR && !(boundSlotItem.getItem() instanceof ItemSoulWeapon)) {
                    slot = -1;
                } else if (boundSlotItem.getItem() instanceof ItemSoulWeapon) {
                    player.inventory.deleteStack(boundSlotItem);
                }
            }

            if (slot < 0) {
                slot = player.inventory.getFirstEmptyStack();
            }

            player.inventory.setInventorySlotContents(slot, new ItemStack(instance.getItem()));

            return null;
        }
    }
}
