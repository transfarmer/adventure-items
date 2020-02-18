package transfarmer.soulboundarmory.network.tool.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.client.gui.SoulToolMenu;
import transfarmer.soulboundarmory.data.tool.SoulToolAttribute;
import transfarmer.soulboundarmory.data.tool.SoulToolType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CToolSpendAttributePoints implements IMessage {
    private int amount;
    private int attributeIndex;
    private int ToolIndex;

    public CToolSpendAttributePoints() {}

    public CToolSpendAttributePoints(final int amount, final SoulToolAttribute attribute, final SoulToolType type) {
        this.amount = amount;
        this.attributeIndex = attribute.index;
        this.ToolIndex = type.index;
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
            final SoulToolType ToolType = SoulToolType.getType(message.ToolIndex);
            final SoulToolAttribute attribute = SoulToolAttribute.getAttribute(message.attributeIndex);
            final ISoulTool instance = SoulToolProvider.get(Minecraft.getMinecraft().player);

            minecraft.addScheduledTask(() -> {
                instance.addAttribute(message.amount, attribute, ToolType);
                minecraft.displayGuiScreen(new SoulToolMenu());
            });

            return null;
        }
    }
}
