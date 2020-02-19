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

public class CToolResetEnchantments implements IMessage {
    private int index;

    public CToolResetEnchantments() {}

    public CToolResetEnchantments(final IType type) {
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

    public static final class Handler implements IMessageHandler<CToolResetEnchantments, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CToolResetEnchantments message, final MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final ISoulTool capability = SoulToolProvider.get(Minecraft.getMinecraft().player);
                final IType type = SoulToolType.getType(message.index);

                capability.addDatum(capability.getDatum(capability.getEnumSpentEnchantmentPoints(), type), capability.getEnumEnchantmentPoints(), type);
                capability.setDatum(0, capability.getEnumSpentEnchantmentPoints(), type);
                capability.setEnchantments(new int[capability.getEnchantmentAmount()], type);
                Minecraft.getMinecraft().displayGuiScreen(new SoulToolMenu());
            });

            return null;
        }
    }
}
