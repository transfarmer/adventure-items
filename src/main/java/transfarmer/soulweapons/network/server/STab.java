package transfarmer.soulweapons.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class STab implements IMessage {
    int tab;

    public STab() {}

    public STab(final int tab) {
        this.tab = tab;
    }

    public void fromBytes(final ByteBuf buffer) {
        this.tab = buffer.readInt();
    }

    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.tab);
    }

    public static final class Handler implements IMessageHandler<STab, IMessage> {
        @Override
        public IMessage onMessage(STab message, MessageContext context) {
            context.getServerHandler().player.getCapability(CAPABILITY, null).setCurrentTab(message.tab);

            return null;
        }
    }
}
