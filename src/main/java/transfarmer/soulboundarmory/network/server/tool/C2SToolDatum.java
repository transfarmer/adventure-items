package transfarmer.soulboundarmory.network.server.tool;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.IItemCapability;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;

public class C2SToolDatum implements IExtendedMessage {
    private int value;
    private String statistic;
    private String item;

    public C2SToolDatum() {}

    public C2SToolDatum(final IItem type, final IStatistic datum, final int value) {
        this.statistic = datum.toString();
        this.item = type.toString();
        this.value = value;
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.value = buffer.readInt();
        this.statistic = buffer.readString();
        this.item = buffer.readString();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.item);
        buffer.writeString(this.statistic);
        buffer.writeInt(this.value);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SToolDatum> {
        @Override
        public IExtendedMessage onMessage(C2SToolDatum message, MessageContext context) {
            final EntityPlayer player = Minecraft.getMinecraft().player;
            final IItemCapability instance = ToolProvider.get(player);
            final IStatistic datum = IStatistic.get(message.statistic);
            final IItem type = IItem.get(message.item);

            instance.addDatum(type, datum, message.value);
            instance.sync();

            return null;
        }
    }
}
