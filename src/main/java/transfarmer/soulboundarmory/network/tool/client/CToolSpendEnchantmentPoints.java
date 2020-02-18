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
import transfarmer.soulboundarmory.data.tool.SoulToolEnchantment;
import transfarmer.soulboundarmory.data.tool.SoulToolType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CToolSpendEnchantmentPoints implements IMessage {
    private int amount;
    private int enchantmentIndex;
    private int ToolIndex;

    public CToolSpendEnchantmentPoints() {}

    public CToolSpendEnchantmentPoints(final int amount, final SoulToolEnchantment enchantment, final SoulToolType type) {
        this.amount = amount;
        this.enchantmentIndex = enchantment.index;
        this.ToolIndex = type.index;
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.amount = buffer.readInt();
        this.enchantmentIndex = buffer.readInt();
        this.ToolIndex = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.amount);
        buffer.writeInt(this.enchantmentIndex);
        buffer.writeInt(this.ToolIndex);
    }

    public static final class Handler implements IMessageHandler<CToolSpendEnchantmentPoints, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CToolSpendEnchantmentPoints message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final SoulToolEnchantment enchantment = SoulToolEnchantment.getEnchantment(message.enchantmentIndex);
            final SoulToolType ToolType = SoulToolType.getType(message.ToolIndex);
            final ISoulTool instance = SoulToolProvider.get(Minecraft.getMinecraft().player);

            minecraft.addScheduledTask(() -> {
                instance.addEnchantment(message.amount, enchantment, ToolType);
                minecraft.displayGuiScreen(new SoulToolMenu());
            });

            return null;
        }
    }
}
