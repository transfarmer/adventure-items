package transfarmer.soulboundarmory.network.C2S;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.IExtendedMessage;
import transfarmer.soulboundarmory.network.common.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;
import transfarmer.soulboundarmory.statistics.base.iface.ICategory;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ENCHANTMENT;

public class C2SReset extends C2SSoulbound {
    private String item;
    private String category;

    public C2SReset() {
    }

    public C2SReset(final ICapabilityType capability) {
        super(capability);
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
        super.fromBytes(buffer);

        this.item = buffer.readString();
        this.category = buffer.readString();
    }

    @Override
    @SideOnly(CLIENT)
    public void toBytes(final ExtendedPacketBuffer buffer) {
        super.toBytes(buffer);

        buffer.writeString(this.item);
        buffer.writeString(this.category);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SReset> {
        @Override
        public IExtendedMessage onMessage(final C2SReset message, final MessageContext context) {
            final SoulboundCapability capability = context.getServerHandler().player.getCapability(message.capability, null);

            if (message.item != null) {
                final IItem item = IItem.get(message.item);

                if (item != null) {
                    if (message.category != null) {
                        final ICategory category = ICategory.get(message.category);

                        if (category == ENCHANTMENT) {
                            capability.resetEnchantments(item);
                        } else if (category != null) {
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
            capability.refresh();

            return null;
        }
    }
}
