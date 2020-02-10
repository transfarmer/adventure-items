package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.capability.ISoulWeapon;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class ServerBindSlot implements IMessage {
    private int slot;

    public ServerBindSlot() {}

    public ServerBindSlot(final int slot) {
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

    public static final class Handler implements IMessageHandler<ServerBindSlot, IMessage> {
        @Override
        public IMessage onMessage(final ServerBindSlot message, final MessageContext context) {
            final ISoulWeapon capability = context.getServerHandler().player.getCapability(CAPABILITY, null);

            if (capability.getBoundSlot() == message.slot) {
                capability.unbindSlot();
            } else {
                capability.setBoundSlot(message.slot);
            }

            return null;
        }
    }
}
