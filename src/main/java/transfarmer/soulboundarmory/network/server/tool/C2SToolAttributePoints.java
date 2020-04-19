package transfarmer.soulboundarmory.network.server.tool;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.IItemCapability;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.network.client.tool.S2CToolSpendAttributePoints;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;

public class C2SToolAttributePoints implements IExtendedMessage {
    private int amount;
    private String statistic;
    private String item;

    public C2SToolAttributePoints() {}

    public C2SToolAttributePoints(final IItem type, final IStatistic attribute, final int amount) {
        this.amount = amount;
        this.statistic = attribute.toString();
        this.item = type.toString();
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.amount = buffer.readInt();
        this.statistic = buffer.readString();
        this.item = buffer.readString();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeInt(this.amount);
        buffer.writeString(this.statistic);
        buffer.writeString(this.item);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SToolAttributePoints> {
        @Override
        public IExtendedMessage onMessage(final C2SToolAttributePoints message, final MessageContext context) {
            final IStatistic attribute = IStatistic.get(message.statistic);
            final IItem type = IItem.get(message.item);
            final IItemCapability instance = ToolProvider.get(context.getServerHandler().player);

            instance.addAttribute(type, attribute, message.amount);

            return new S2CToolSpendAttributePoints(message.amount, attribute, type);
        }
    }
}
