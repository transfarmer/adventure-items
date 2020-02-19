package transfarmer.soulboundarmory.network.server.tool;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.data.IType;
import transfarmer.soulboundarmory.data.tool.SoulToolType;
import transfarmer.soulboundarmory.network.client.tool.CToolResetAttributes;

public class SToolResetAttributes implements IMessage {
    private int index;

    public SToolResetAttributes() {}

    public SToolResetAttributes(final IType type) {
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
            final ISoulTool capability = SoulToolProvider.get(context.getServerHandler().player);
            final IType type = SoulToolType.getType(message.index);

            capability.addDatum(capability.getDatum(capability.getEnumSpentAttributePoints(), type), capability.getEnumAttributePoints(), type);
            capability.setDatum(0, capability.getEnumSpentAttributePoints(), type);
            capability.setAttributes(new float[capability.getAttributeAmount()], type);

            return new CToolResetAttributes(type);
        }
    }
}
