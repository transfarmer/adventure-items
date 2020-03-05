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
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.statistics.SoulDatum.DATA;

public class CToolResetEnchantments implements IMessage {
    private int index;

    public CToolResetEnchantments() {}

    public CToolResetEnchantments(final SoulType type) {
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
                final ISoulCapability capability = SoulToolProvider.get(Minecraft.getMinecraft().player);
                final SoulType type = SoulToolType.get(message.index);

                capability.addDatum(capability.getDatum(DATA.spentEnchantmentPoints, type), DATA.enchantmentPoints, type);
                capability.setDatum(0, DATA.spentEnchantmentPoints, type);
                capability.setEnchantments(new int[capability.getEnchantmentAmount()], type);
                Minecraft.getMinecraft().displayGuiScreen(new SoulToolMenu());
            });

            return null;
        }
    }
}
