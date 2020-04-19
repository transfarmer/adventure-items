package transfarmer.soulboundarmory.network.server.tool;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;

public class C2SToolTab implements IExtendedMessage {
    int tab;

    public C2SToolTab() {}

    public C2SToolTab(final int tab) {
        this.tab = tab;
    }

    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.tab = buffer.readInt();
    }

    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeInt(this.tab);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SToolTab> {
        @Override
        public IExtendedMessage onMessage(C2SToolTab message, MessageContext context) {
            ToolProvider.get(context.getServerHandler().player).setCurrentTab(message.tab);

            return null;
        }
    }
}
