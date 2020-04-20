package transfarmer.soulboundarmory.network.server;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;

public class C2STab implements IExtendedMessage {
    private String capability;
    private int tab;

    public C2STab() {}

    public C2STab(final ICapabilityType capability, final int tab) {
        this.capability = capability.toString();
        this.tab = tab;
    }

    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.capability = buffer.readString();
        this.tab = buffer.readInt();
    }

    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.capability);
        buffer.writeInt(this.tab);
    }

    public static final class Handler implements IExtendedMessageHandler<C2STab> {
        @Override
        public IExtendedMessage onMessage(final C2STab message, final MessageContext context) {
            context.getServerHandler().player.getCapability(ICapabilityType.get(message.capability).getCapability(), null).setCurrentTab(message.tab);

            return null;
        }
    }
}
