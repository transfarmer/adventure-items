package transfarmer.soulboundarmory.network.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.statistics.IItem;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CEnchant implements IExtendedMessage {
    private String component;
    private String item;
    private Identifier enchantment;
    private int amount;

    public S2CEnchant() {}

    public S2CEnchant(final IComponentType component,
                      final IItem item, final Enchantment enchantment, final int amount) {
        this.component = component.toString();
        this.item = item.toString();
        this.enchantment = enchantment.getRegistryName();
        this.amount = amount;
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.component = buffer.readString();
        this.item = buffer.readString();
        this.enchantment = buffer.readIdentifier();
        this.amount = buffer.readInt();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.component);
        buffer.writeString(this.item);
        buffer.writeIdentifier(this.enchantment);
        buffer.writeInt(this.amount);
    }

    public static final class Handler implements IExtendedMessageHandler<S2CEnchant> {
        @Environment(CLIENT)
        @Override
        public IExtendedMessage onMessage(final S2CEnchant message, final MessageContext context) {
            final Minecraft minecraft = CLIENT;
            final Enchantment enchantment = Enchantment.getEnchantmentByLocation(enchantment.toString());
            final IItem item = IItem.get(item);
            final ISoulboundComponent component = minecraft.player.getComponent(IComponentType.get(component).getComponent(), null);

            minecraft.addScheduledTask(() -> {
                component.addEnchantment(item, enchantment, amount);
                component.refresh();
            });

            return null;
        }
    }
}
