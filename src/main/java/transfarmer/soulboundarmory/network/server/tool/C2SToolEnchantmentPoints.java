package transfarmer.soulboundarmory.network.server.tool;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.network.client.tool.S2CToolSpendEnchantmentPoints;
import transfarmer.soulboundarmory.statistics.SoulEnchantment;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolEnchantment;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

public class C2SToolEnchantmentPoints implements IMessage {
    private int amount;
    private int enchantmentIndex;
    private int typeIndex;

    public C2SToolEnchantmentPoints() {}

    public C2SToolEnchantmentPoints(final int amount, final SoulEnchantment enchantment, final SoulType type) {
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

    public static final class Handler implements IMessageHandler<C2SToolEnchantmentPoints, IMessage> {
        @Override
        public IMessage onMessage(final C2SToolEnchantmentPoints message, final MessageContext context) {
            final ISoulCapability instance = SoulToolProvider.get(context.getServerHandler().player);
            final SoulEnchantment enchantment = SoulToolEnchantment.get(message.enchantmentIndex);
            final SoulType toolType = SoulToolType.get(message.typeIndex);

            instance.addEnchantment(message.amount, enchantment, toolType);

            return new S2CToolSpendEnchantmentPoints(message.amount, enchantment, toolType);
        }
    }
}
