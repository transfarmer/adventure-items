package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class ServerTab implements IMessage {
    int tab;

    public ServerTab() {}

    public ServerTab(final int tab) {
        this.tab = tab;
    }

    public void fromBytes(ByteBuf buffer) {
        this.tab = buffer.readInt();
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.tab);
    }

    public static final class Handler implements IMessageHandler<ServerTab, IMessage> {
        @Override
        public IMessage onMessage(ServerTab message, MessageContext context) {
            context.getServerHandler().player.getCapability(CAPABILITY, null).setCurrentTab(message.tab);

            return null;
        }
    }
}
