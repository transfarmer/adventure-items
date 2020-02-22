package transfarmer.soulboundarmory.network.server.tool;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.network.client.tool.CToolResetAttributes;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

public class SToolResetAttributes implements IMessage {
    private int index;

    public SToolResetAttributes() {}

    public SToolResetAttributes(final SoulType type) {
        this.index = type.getIndex();
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.index = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.index);
    }

    public static final class Handler implements IMessageHandler<SToolResetAttributes, IMessage> {
        @Override
        public IMessage onMessage(final SToolResetAttributes message, final MessageContext context) {
            final ISoulCapability capability = SoulToolProvider.get(context.getServerHandler().player);
            final SoulType type = SoulToolType.get(message.index);

            capability.addDatum(capability.getDatum(SoulDatum.SPENT_ATTRIBUTE_POINTS, type), SoulDatum.ATTRIBUTE_POINTS, type);
            capability.setDatum(0, SoulDatum.SPENT_ATTRIBUTE_POINTS, type);
            capability.setAttributes(new float[capability.getAttributeAmount()], type);

            return new CToolResetAttributes(type);
        }
    }
}
