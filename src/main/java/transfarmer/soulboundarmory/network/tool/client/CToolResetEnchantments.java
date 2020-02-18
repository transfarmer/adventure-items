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
import transfarmer.soulboundarmory.data.tool.SoulToolType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.capability.tool.SoulToolHelper.ENCHANTMENTS;
import static transfarmer.soulboundarmory.data.tool.SoulToolDatum.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.data.tool.SoulToolDatum.SPENT_ENCHANTMENT_POINTS;

public class CToolResetEnchantments implements IMessage {
    private int index;

    public CToolResetEnchantments() {}

    public CToolResetEnchantments(final SoulToolType type) {
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

    public static final class Handler implements IMessageHandler<CToolResetEnchantments, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CToolResetEnchantments message, final MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final ISoulTool capability = SoulToolProvider.get(Minecraft.getMinecraft().player);
                final SoulToolType type = SoulToolType.getType(message.index);

                capability.addDatum(capability.getDatum(SPENT_ENCHANTMENT_POINTS, type), ENCHANTMENT_POINTS, type);
                capability.setDatum(0, SPENT_ENCHANTMENT_POINTS, type);
                capability.setEnchantments(new int[ENCHANTMENTS], type);
                Minecraft.getMinecraft().displayGuiScreen(new SoulToolMenu());
            });

            return null;
        }
    }
}
