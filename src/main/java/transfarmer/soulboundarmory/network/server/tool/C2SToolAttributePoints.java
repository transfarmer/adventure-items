package transfarmer.soulboundarmory.network.server.tool;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.ISoulCapability;
import transfarmer.soulboundarmory.capability.soulbound.tool.SoulToolProvider;
import transfarmer.soulboundarmory.network.client.tool.S2CToolSpendAttributePoints;
import transfarmer.soulboundarmory.statistics.SoulAttribute;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

public class C2SToolAttributePoints implements IMessage {
    private int amount;
    private int attributeIndex;
    private int ToolIndex;

    public C2SToolAttributePoints() {}

    public C2SToolAttributePoints(final int amount, final SoulAttribute attribute, final SoulType type) {
        this.amount = amount;
        this.attributeIndex = attribute.getIndex();
        this.ToolIndex = type.getIndex();
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

    public static final class Handler implements IMessageHandler<C2SToolAttributePoints, IMessage> {
        @Override
        public IMessage onMessage(final C2SToolAttributePoints message, final MessageContext context) {
            final SoulAttribute attribute = SoulToolAttribute.get(message.attributeIndex);
            final SoulType type = SoulToolType.get(message.ToolIndex);
            final ISoulCapability instance = SoulToolProvider.get(context.getServerHandler().player);

            instance.addAttribute(message.amount, attribute, type);

            return new S2CToolSpendAttributePoints(message.amount, attribute, type);
        }
    }
}
