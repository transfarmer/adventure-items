package transfarmer.soulboundarmory.network.C2S;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.IExtendedMessage;
import transfarmer.soulboundarmory.network.common.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

public class C2SEnchant extends C2SSoulbound {
    private String item;
    private Identifier enchantment;
    private int amount;

    public C2SEnchant() {
    }

    public C2SEnchant(final ICapabilityType capability, final IItem item, final Enchantment enchantment, final int amount) {
        super(capability);

        this.item = item.toString();
        this.enchantment = enchantment.getRegistryName();
        this.amount = amount;
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        super.fromBytes(buffer);

        this.item = buffer.readString();
        this.enchantment = buffer.readIdentifier();
        this.amount = buffer.readInt();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        super.toBytes(buffer);

        buffer.writeString(this.item);
        buffer.writeIdentifier(this.enchantment);
        buffer.writeInt(this.amount);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SEnchant> {
        @Override
        public IExtendedMessage onMessage(final C2SEnchant message, final MessageContext context) {
            final ISoulboundComponent capability = context.getServerHandler().player.getCapability(message.capability, null);
            final Enchantment enchantment = Enchantment.getEnchantmentByLocation(message.enchantment.toString());
            final IItem weaponType = capability.getItemType(message.item);

            capability.addEnchantment(weaponType, enchantment, message.amount);
            capability.sync();
            capability.refresh();

            return null;
        }
    }
}
