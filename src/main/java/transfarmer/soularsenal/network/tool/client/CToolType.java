package transfarmer.soularsenal.network.tool.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soularsenal.capability.tool.SoulToolProvider;
import transfarmer.soularsenal.data.tool.SoulToolType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CToolType implements IMessage {
    private int slot;
    private int ToolIndex;

    public CToolType() {}

    public CToolType(final int slot, final SoulToolType ToolType) {
        this.slot = slot;
        this.ToolIndex = ToolType.index;
    }

    public void fromBytes(final ByteBuf buffer) {
        this.slot = buffer.readInt();
        this.ToolIndex = buffer.readInt();
    }

    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.slot);
        buffer.writeInt(this.ToolIndex);
    }

    public static final class Handler implements IMessageHandler<CToolType, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CToolType message, final MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final EntityPlayer player = Minecraft.getMinecraft().player;

                SoulToolProvider.get(player).setCurrentType(message.ToolIndex);
                player.inventory.setInventorySlotContents(message.slot,
                    new ItemStack(SoulToolProvider.get(player).getCurrentType().getItem()));
            });

            return null;
        }
    }
}
