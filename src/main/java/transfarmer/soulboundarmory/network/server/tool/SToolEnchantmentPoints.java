package transfarmer.soulboundarmory.network.server.tool;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.data.IEnchantment;
import transfarmer.soulboundarmory.data.IType;
import transfarmer.soulboundarmory.data.tool.SoulToolEnchantment;
import transfarmer.soulboundarmory.data.tool.SoulToolType;
import transfarmer.soulboundarmory.network.client.tool.CToolSpendEnchantmentPoints;

public class SToolEnchantmentPoints implements IMessage {
    private int amount;
    private int enchantmentIndex;
    private int typeIndex;

    public SToolEnchantmentPoints() {}

    public SToolEnchantmentPoints(final int amount, final IEnchantment enchantment, final IType type) {
        this.amount = amount;
        this.enchantmentIndex = enchantment.getIndex();
        this.typeIndex = type.getIndex();
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
            final IEnchantment enchantment = SoulToolEnchantment.getEnchantment(message.enchantmentIndex);
            final IType toolType = SoulToolType.getType(message.typeIndex);

            instance.addEnchantment(message.amount, enchantment, toolType);

            return new CToolSpendEnchantmentPoints(message.amount, enchantment, toolType);
        }
    }
}
