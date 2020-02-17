package transfarmer.soularsenal.network.tool.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soularsenal.capability.tool.ISoulTool;
import transfarmer.soularsenal.capability.tool.SoulToolProvider;
import transfarmer.soularsenal.client.gui.SoulToolMenu;
import transfarmer.soularsenal.data.tool.SoulToolType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soularsenal.capability.tool.SoulToolHelper.ATTRIBUTES;
import static transfarmer.soularsenal.data.tool.SoulToolDatum.ATTRIBUTE_POINTS;
import static transfarmer.soularsenal.data.tool.SoulToolDatum.SPENT_ATTRIBUTE_POINTS;

public class CToolResetAttributes implements IMessage {
    private int index;

    public CToolResetAttributes() {}

    public CToolResetAttributes(final SoulToolType type) {
        this.index = type.index;
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
                final SoulToolType type = SoulToolType.getType(message.index);

                capability.addDatum(capability.getDatum(SPENT_ATTRIBUTE_POINTS, type), ATTRIBUTE_POINTS, type);
                capability.setDatum(0, SPENT_ATTRIBUTE_POINTS, type);
                capability.setAttributes(new float[ATTRIBUTES], type);
                Minecraft.getMinecraft().displayGuiScreen(new SoulToolMenu());
            });

            return null;
        }
    }
}
