package transfarmer.soularsenal.network.tool.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soularsenal.capability.tool.ISoulTool;
import transfarmer.soularsenal.capability.tool.SoulToolProvider;
import transfarmer.soularsenal.data.tool.SoulToolType;
import transfarmer.soularsenal.network.tool.client.CToolResetEnchantments;

import static transfarmer.soularsenal.capability.tool.SoulToolHelper.ENCHANTMENTS;
import static transfarmer.soularsenal.data.tool.SoulToolDatum.ENCHANTMENT_POINTS;
import static transfarmer.soularsenal.data.tool.SoulToolDatum.SPENT_ENCHANTMENT_POINTS;


public class SToolResetEnchantments implements IMessage {
    private int index;

    public SToolResetEnchantments() {
    }

    public SToolResetEnchantments(final SoulToolType type) {
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

    public static final class Handler implements IMessageHandler<SToolResetEnchantments, IMessage> {
        @Override
        public IMessage onMessage(final SToolResetEnchantments message, final MessageContext context) {
            final ISoulTool capability = SoulToolProvider.get(context.getServerHandler().player);
            final SoulToolType type = SoulToolType.getType(message.index);

            capability.addDatum(capability.getDatum(SPENT_ENCHANTMENT_POINTS, type), ENCHANTMENT_POINTS, type);
            capability.setDatum(0, SPENT_ENCHANTMENT_POINTS, type);
            capability.setEnchantments(new int[ENCHANTMENTS], type);

            return new CToolResetEnchantments(type);
        }
    }
}
