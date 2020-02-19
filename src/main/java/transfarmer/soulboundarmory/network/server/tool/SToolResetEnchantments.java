package transfarmer.soulboundarmory.network.server.tool;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.data.IType;
import transfarmer.soulboundarmory.data.tool.SoulToolType;
import transfarmer.soulboundarmory.network.client.tool.CToolResetEnchantments;


public class SToolResetEnchantments implements IMessage {
    private int index;

    public SToolResetEnchantments() {
    }

    public SToolResetEnchantments(final IType type) {
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

    public static final class Handler implements IMessageHandler<SToolResetEnchantments, IMessage> {
        @Override
        public IMessage onMessage(final SToolResetEnchantments message, final MessageContext context) {
            final ISoulTool capability = SoulToolProvider.get(context.getServerHandler().player);
            final IType type = SoulToolType.getType(message.index);

            capability.addDatum(capability.getDatum(capability.getEnumSpentEnchantmentPoints(), type), capability.getEnumEnchantmentPoints(), type);
            capability.setDatum(0, capability.getEnumSpentEnchantmentPoints(), type);
            capability.setEnchantments(new int[capability.getEnchantmentAmount()], type);

            return new CToolResetEnchantments(type);
        }
    }
}
