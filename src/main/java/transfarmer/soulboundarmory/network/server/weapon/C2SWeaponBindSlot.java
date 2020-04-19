package transfarmer.soulboundarmory.network.server.weapon;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.network.client.weapon.S2CWeaponBindSlot;

public class C2SWeaponBindSlot implements IExtendedMessage {
    private int slot;

    public C2SWeaponBindSlot() {
    }

    public C2SWeaponBindSlot(final int slot) {
        this.slot = slot;
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.slot = buffer.readInt();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeInt(this.slot);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SWeaponBindSlot> {
        @Override
        public IExtendedMessage onMessage(final C2SWeaponBindSlot message, final MessageContext context) {
            final EntityPlayer player = context.getServerHandler().player;
            final IWeapon capability = WeaponProvider.get(player);
            final NonNullList<ItemStack> inventory = player.inventory.mainInventory;

            if (capability.getBoundSlot() == message.slot) {
                capability.unbindSlot();
            } else {
                if (inventory.get(message.slot).isEmpty()) {
                    for (final ItemStack itemStack : inventory) {
                        if (capability.getItemType(itemStack) == capability.getItemType()) {
                            inventory.set(capability.getBoundSlot(), ItemStack.EMPTY);
                            player.inventory.setInventorySlotContents(message.slot, itemStack);
                        }
                    }
                }

                capability.bindSlot(message.slot);
            }

            return new S2CWeaponBindSlot(message.slot);
        }
    }
}
