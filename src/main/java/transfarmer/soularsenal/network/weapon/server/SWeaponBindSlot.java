package transfarmer.soularsenal.network.weapon.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soularsenal.capability.weapon.ISoulWeapon;
import transfarmer.soularsenal.data.weapon.SoulWeaponType;
import transfarmer.soularsenal.network.weapon.client.CWeaponBindSlot;

import static transfarmer.soularsenal.capability.weapon.SoulWeaponProvider.CAPABILITY;

public class SWeaponBindSlot implements IMessage {
    private int slot;

    public SWeaponBindSlot() {
    }

    public SWeaponBindSlot(final int slot) {
        this.slot = slot;
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.slot = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.slot);
    }

    public static final class Handler implements IMessageHandler<SWeaponBindSlot, IMessage> {
        @Override
        public IMessage onMessage(final SWeaponBindSlot message, final MessageContext context) {
            final EntityPlayer player = context.getServerHandler().player;
            final ISoulWeapon capability = context.getServerHandler().player.getCapability(CAPABILITY, null);
            final NonNullList<ItemStack> inventory = player.inventory.mainInventory;

            if (capability.getBoundSlot() == message.slot) {
                capability.unbindSlot();
            } else {
                if (inventory.get(message.slot).isEmpty()) {
                    for (final ItemStack itemStack : inventory) {
                        if (SoulWeaponType.getType(itemStack) == capability.getCurrentType()) {
                            inventory.set(capability.getBoundSlot(), ItemStack.EMPTY);
                            player.inventory.setInventorySlotContents(message.slot, itemStack);
                        }
                    }
                }

                capability.setBoundSlot(message.slot);
            }

            return new CWeaponBindSlot(message.slot);
        }
    }
}
