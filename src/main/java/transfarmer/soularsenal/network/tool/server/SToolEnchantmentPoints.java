package transfarmer.soularsenal.network.tool.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soularsenal.capability.tool.ISoulTool;
import transfarmer.soularsenal.capability.tool.SoulToolProvider;
import transfarmer.soularsenal.data.tool.SoulToolEnchantment;
import transfarmer.soularsenal.data.tool.SoulToolType;
import transfarmer.soularsenal.network.tool.client.CToolSpendEnchantmentPoints;

public class SToolEnchantmentPoints implements IMessage {
    private int amount;
    private int enchantmentIndex;
    private int typeIndex;

    public SToolEnchantmentPoints() {}

    public SToolEnchantmentPoints(final int amount, final SoulToolEnchantment enchantment, final SoulToolType type) {
        this.amount = amount;
        this.enchantmentIndex = enchantment.index;
        this.typeIndex = type.index;
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.amount = buffer.readInt();
        this.enchantmentIndex = buffer.readInt();
        this.typeIndex = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.amount);
        buffer.writeInt(this.enchantmentIndex);
        buffer.writeInt(this.typeIndex);
    }

    public static final class Handler implements IMessageHandler<SToolEnchantmentPoints, IMessage> {
        @Override
        public IMessage onMessage(final SToolEnchantmentPoints message, final MessageContext context) {
            final ISoulTool instance = SoulToolProvider.get(context.getServerHandler().player);
            final SoulToolEnchantment enchantment = SoulToolEnchantment.getEnchantment(message.enchantmentIndex);
            final SoulToolType ToolType = SoulToolType.getType(message.typeIndex);

            instance.addEnchantment(message.amount, enchantment, ToolType);

            return new CToolSpendEnchantmentPoints(message.amount, enchantment, ToolType);
        }
    }
}
