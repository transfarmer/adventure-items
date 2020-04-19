package transfarmer.soulboundarmory.network.client.tool;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.ICapabilityEnchantable;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.client.gui.SoulToolMenu;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CToolSpendEnchantmentPoints implements IExtendedMessage {
    private String item;
    private String enchantment;
    private int amount;

    public S2CToolSpendEnchantmentPoints() {}

    public S2CToolSpendEnchantmentPoints(final IItem type, final Enchantment enchantment, final int amount) {
        this.amount = amount;
        this.enchantment = enchantment.toString();
        this.item = type.toString();
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.amount = buffer.readInt();
        this.enchantment = buffer.readString();
        this.item = buffer.readString();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.item);
        buffer.writeString(this.enchantment);
        buffer.writeInt(this.amount);
    }

    public static final class Handler implements IExtendedMessageHandler<S2CToolSpendEnchantmentPoints> {
        @SideOnly(CLIENT)
        @Override
        public IExtendedMessage onMessage(final S2CToolSpendEnchantmentPoints message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final ICapabilityEnchantable instance = ToolProvider.get(Minecraft.getMinecraft().player);
            final IItem type = IItem.get(message.item);
            final Enchantment enchantment = Enchantment.getEnchantmentByLocation(message.enchantment);

            minecraft.addScheduledTask(() -> {
                instance.addEnchantment(type, enchantment, message.amount);
                minecraft.displayGuiScreen(new SoulToolMenu());
            });

            return null;
        }
    }
}
