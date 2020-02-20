package transfarmer.soulboundarmory.network.client.tool;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.client.gui.SoulToolMenu;
import transfarmer.soulboundarmory.statistics.IType;
import transfarmer.soulboundarmory.statistics.SoulAttribute;
import transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CToolSpendAttributePoints implements IMessage {
    private int amount;
    private int attributeIndex;
    private int ToolIndex;

    public CToolSpendAttributePoints() {}

    public CToolSpendAttributePoints(final int amount, final SoulAttribute attribute, final IType type) {
        this.amount = amount;
        this.attributeIndex = attribute.getIndex();
        this.ToolIndex = type.getIndex();
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.amount = buffer.readInt();
        this.attributeIndex = buffer.readInt();
        this.ToolIndex = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.amount);
        buffer.writeInt(this.attributeIndex);
        buffer.writeInt(this.ToolIndex);
    }

    public static final class Handler implements IMessageHandler<CToolSpendAttributePoints, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CToolSpendAttributePoints message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final IType type = SoulToolType.getType(message.ToolIndex);
            final SoulAttribute attribute = SoulToolAttribute.get(message.attributeIndex);
            final ISoulCapability instance = SoulToolProvider.get(Minecraft.getMinecraft().player);

            minecraft.addScheduledTask(() -> {
                instance.addAttribute(message.amount, attribute, type);
                minecraft.displayGuiScreen(new SoulToolMenu());
            });

            return null;
        }
    }
}
