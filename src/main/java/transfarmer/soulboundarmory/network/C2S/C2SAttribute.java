package transfarmer.soulboundarmory.network.C2S;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.IExtendedMessage;
import transfarmer.soulboundarmory.network.common.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;

public class C2SAttribute implements IExtendedMessage {
    private String capability;
    private String item;
    private String statistic;
    private int amount;

    public C2SAttribute() {}

    public C2SAttribute(final ICapabilityType capability, final IItem item, final IStatistic statistic, final int amount) {
        this.capability = capability.toString();
        this.item = item.toString();
        this.statistic = statistic.toString();
        this.amount = amount;
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.capability = buffer.readString();
        this.item = buffer.readString();
        this.statistic = buffer.readString();
        this.amount = buffer.readInt();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.capability);
        buffer.writeString(this.item);
        buffer.writeString(this.statistic);
        buffer.writeInt(this.amount);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SAttribute> {
        @Override
        public IExtendedMessage onMessage(final C2SAttribute message, final MessageContext context) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                final IStatistic statistic = IStatistic.get(message.statistic);
                final IItem type = IItem.get(message.item);
                final ISoulboundComponent capability = context.getServerHandler().player.getCapability(ICapabilityType.get(message.capability).getCapability(), null);

                capability.addAttribute(type, statistic, message.amount);
                capability.sync();
                capability.refresh();
            });

            return null;
        }
    }
}
