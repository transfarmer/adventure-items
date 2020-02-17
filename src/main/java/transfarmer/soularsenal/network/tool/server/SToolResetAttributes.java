package transfarmer.soularsenal.network.tool.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soularsenal.capability.tool.ISoulTool;
import transfarmer.soularsenal.capability.tool.SoulToolProvider;
import transfarmer.soularsenal.data.tool.SoulToolType;
import transfarmer.soularsenal.network.tool.client.CToolResetAttributes;

import static transfarmer.soularsenal.capability.tool.SoulToolHelper.ATTRIBUTES;
import static transfarmer.soularsenal.data.tool.SoulToolDatum.ATTRIBUTE_POINTS;
import static transfarmer.soularsenal.data.tool.SoulToolDatum.SPENT_ATTRIBUTE_POINTS;

public class SToolResetAttributes implements IMessage {
    private int index;

    public SToolResetAttributes() {}

    public SToolResetAttributes(final SoulToolType type) {
        this.index = type.index;
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
            final ISoulTool capability = SoulToolProvider.get(context.getServerHandler().player);
            final SoulToolType type = SoulToolType.getType(message.index);

            capability.addDatum(capability.getDatum(SPENT_ATTRIBUTE_POINTS, type), ATTRIBUTE_POINTS, type);
            capability.setDatum(0, SPENT_ATTRIBUTE_POINTS, type);
            capability.setAttributes(new float[ATTRIBUTES], type);

            return new CToolResetAttributes(type);
        }
    }
}
