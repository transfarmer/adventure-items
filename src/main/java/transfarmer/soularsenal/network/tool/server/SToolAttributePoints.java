package transfarmer.soularsenal.network.tool.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soularsenal.capability.tool.ISoulTool;
import transfarmer.soularsenal.capability.tool.SoulToolProvider;
import transfarmer.soularsenal.data.tool.SoulToolAttribute;
import transfarmer.soularsenal.data.tool.SoulToolType;
import transfarmer.soularsenal.network.tool.client.CToolSpendAttributePoints;

public class SToolAttributePoints implements IMessage {
    private int amount;
    private int attributeIndex;
    private int ToolIndex;

    public SToolAttributePoints() {}

    public SToolAttributePoints(final int amount, final SoulToolAttribute attribute, final SoulToolType type) {
        this.amount = amount;
        this.attributeIndex = attribute.index;
        this.ToolIndex = type.index;
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.amount = buffer.readInt();
        this.attributeIndex = buffer.readInt();
        this.ToolIndex = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.amount);
        buffer.writeInt(this.attributeIndex);
        buffer.writeInt(this.ToolIndex);
    }

    public static final class Handler implements IMessageHandler<SToolAttributePoints, IMessage> {
        @Override
        public IMessage onMessage(final SToolAttributePoints message, final MessageContext context) {
            final SoulToolAttribute attribute = SoulToolAttribute.getAttribute(message.attributeIndex);
            final SoulToolType ToolType = SoulToolType.getType(message.ToolIndex);
            final ISoulTool instance = SoulToolProvider.get(context.getServerHandler().player);

            instance.addAttribute(message.amount, attribute, ToolType);

            return new CToolSpendAttributePoints(message.amount, attribute, ToolType);
        }
    }
}
