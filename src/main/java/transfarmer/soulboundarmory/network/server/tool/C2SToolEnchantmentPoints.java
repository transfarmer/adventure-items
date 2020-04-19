package transfarmer.soulboundarmory.network.server.tool;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.ICapabilityEnchantable;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.network.client.tool.S2CToolSpendEnchantmentPoints;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

public class C2SToolEnchantmentPoints implements IExtendedMessage {
    private int amount;
    private String enchantmentIndex;
    private String typeIndex;

    public C2SToolEnchantmentPoints() {}

    public C2SToolEnchantmentPoints(final int amount, final Enchantment enchantment, final IItem type) {
        this.amount = amount;
        this.enchantmentIndex = enchantment.getName();
        this.typeIndex = type.toString();
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.amount = buffer.readInt();
        this.enchantmentIndex = buffer.readString();
        this.typeIndex = buffer.readString();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeInt(this.amount);
        buffer.writeString(this.enchantmentIndex);
        buffer.writeString(this.typeIndex);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SToolEnchantmentPoints> {
        @Override
        public IExtendedMessage onMessage(final C2SToolEnchantmentPoints message, final MessageContext context) {
            final ICapabilityEnchantable instance = ToolProvider.get(context.getServerHandler().player);
            final Enchantment enchantment = Enchantment.getEnchantmentByLocation(message.enchantmentIndex);
            final IItem item = IItem.get(message.typeIndex);

            instance.addEnchantment(item, enchantment, message.amount);

            return new S2CToolSpendEnchantmentPoints(item, enchantment, message.amount);
        }
    }
}
