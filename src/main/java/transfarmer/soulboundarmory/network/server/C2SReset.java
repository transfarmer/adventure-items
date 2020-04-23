package transfarmer.soulboundarmory.network.server;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.common.ISoulbound;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;
import transfarmer.soulboundarmory.statistics.base.iface.ICategory;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

public class C2SReset implements IExtendedMessage {
    private String capability;
    private String item;
    private String category;

    public C2SReset() {}

    public C2SReset(final ICapabilityType capability) {
        this.capability = capability.toString();
    }

    public C2SReset(final ICapabilityType capability, final IItem item) {
        this(capability);

        this.item = item.toString();
    }

    public C2SReset(final ICapabilityType capability, final IItem item, final ICategory category) {
        this(capability, item);

        this.category = category.toString();
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.capability = buffer.readString();
        this.item = buffer.readString();
        this.category = buffer.readString();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.capability);
        buffer.writeString(this.item);
        buffer.writeString(this.category);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SReset> {
        @Override
        public IExtendedMessage onMessage(final C2SReset message, final MessageContext context) {
            final ICapabilityType capabilityType = ICapabilityType.get(message.capability);

            if (capabilityType != null) {
                final ISoulbound capability = context.getServerHandler().player.getCapability(capabilityType.getCapability(), null);

                if (message.item != null) {
                    final IItem item = IItem.get(message.item);

                    if (item != null) {
                        if (message.category != null) {
                            final ICategory category = ICategory.get(message.category);

                            if (category != null) {
                                capability.reset(item, category);
                            }
                        } else {
                            capability.reset(item);
                        }
                    }
                } else {
                    capability.reset();
                }

                capability.sync();
                capability.openGUI();
            }

            return null;
        }
    }
}
