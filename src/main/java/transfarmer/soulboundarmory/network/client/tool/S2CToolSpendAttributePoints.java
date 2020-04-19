package transfarmer.soulboundarmory.network.client.tool;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.IItemCapability;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.client.gui.SoulToolMenu;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CToolSpendAttributePoints implements IExtendedMessage {
    private int amount;
    private String statistic;
    private String item;

    public S2CToolSpendAttributePoints() {}

    public S2CToolSpendAttributePoints(final int amount, final IStatistic attribute, final IItem type) {
        this.amount = amount;
        this.statistic = attribute.toString();
        this.item = type.toString();
    }

    @SideOnly(CLIENT)
    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.amount = buffer.readInt();
        this.statistic = buffer.readString();
        this.item = buffer.readString();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeInt(this.amount);
        buffer.writeString(this.statistic);
        buffer.writeString(this.item);
    }

    public static final class Handler implements IExtendedMessageHandler<S2CToolSpendAttributePoints> {
        @SideOnly(CLIENT)
        @Override
        public IExtendedMessage onMessage(final S2CToolSpendAttributePoints message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final IItem type = IItem.get(message.item);
            final IStatistic attribute = IStatistic.get(message.statistic);
            final IItemCapability instance = ToolProvider.get(Minecraft.getMinecraft().player);

            minecraft.addScheduledTask(() -> {
                instance.addAttribute(type, attribute, message.amount);
                minecraft.displayGuiScreen(new SoulToolMenu());
            });

            return null;
        }
    }
}
