package transfarmer.soulboundarmory.network.client.tool;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.client.gui.SoulToolMenu;
import transfarmer.soulboundarmory.data.IType;
import transfarmer.soulboundarmory.data.tool.SoulToolType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CToolResetAttributes implements IMessage {
    private int index;

    public CToolResetAttributes() {}

    public CToolResetAttributes(final IType type) {
        this.index = type.getIndex();
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.index = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.index);
    }

    public static final class Handler implements IMessageHandler<CToolResetAttributes, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CToolResetAttributes message, final MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final ISoulTool capability = SoulToolProvider.get(Minecraft.getMinecraft().player);
                final IType type = SoulToolType.getType(message.index);

                capability.addDatum(capability.getDatum(capability.getEnumSpentAttributePoints(), type), capability.getEnumAttributePoints(), type);
                capability.setDatum(0, capability.getEnumSpentAttributePoints(), type);
                capability.setAttributes(new float[capability.getAttributeAmount()], type);
                Minecraft.getMinecraft().displayGuiScreen(new SoulToolMenu());
            });

            return null;
        }
    }
}
