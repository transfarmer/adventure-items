package transfarmer.soulboundarmory.network.C2S;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.IExtendedMessage;
import transfarmer.soulboundarmory.network.common.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;

public class C2SBindSlot implements IExtendedMessage {
    private String capability;
    private int slot;

    public C2SBindSlot() {
    }

    public C2SBindSlot(final ICapabilityType capability, final int slot) {
        this.capability = capability.toString();
        this.slot = slot;
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.capability = buffer.readString();
        this.slot = buffer.readInt();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.capability);
        buffer.writeInt(this.slot);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SBindSlot> {
        @Override
        public IExtendedMessage onMessage(final C2SBindSlot message, final MessageContext context) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                final SoulboundCapability capability = context.getServerHandler().player.getCapability(ICapabilityType.get(message.capability).getCapability(), null);

                if (capability.getBoundSlot() == message.slot) {
                    capability.unbindSlot();
                } else {
                    capability.bindSlot(message.slot);
                }

                capability.sync();
                capability.refresh();
            });

            return null;
        }
    }
}
