package transfarmer.soulweapons.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.capability.SoulWeaponHelper;
import transfarmer.soulweapons.data.SoulWeaponType;
import transfarmer.soulweapons.network.client.CBindSlot;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class SBindSlot implements IMessage {
    private int slot;

    public SBindSlot() {
    }

    public SBindSlot(final int slot) {
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

    public static final class Handler implements IMessageHandler<SBindSlot, IMessage> {
        @Override
        public IMessage onMessage(final SBindSlot message, final MessageContext context) {
            final EntityPlayer player = context.getServerHandler().player;
            final ISoulWeapon capability = context.getServerHandler().player.getCapability(CAPABILITY, null);

            if (capability.getBoundSlot() == message.slot) {
                capability.unbindSlot();
            } else {
                capability.setBoundSlot(message.slot);

                if (SoulWeaponHelper.hasSoulWeapon(player)) {
                    if (player.inventory.mainInventory.get(message.slot).getItem() == Items.AIR) {

                        for (final ItemStack itemStack : player.inventory.mainInventory) {
                            if (SoulWeaponType.getType(itemStack) == capability.getCurrentType()) {
                                player.replaceItemInInventory(message.slot, itemStack);
                                player.inventory.deleteStack(itemStack);

                                return new CBindSlot(message.slot, true);
                            }
                        }
                    }
                }
            }

            return new CBindSlot(message.slot, false);
        }
    }
}
