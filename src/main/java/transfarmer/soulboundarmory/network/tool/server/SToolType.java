package transfarmer.soulboundarmory.network.tool.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolHelper;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.data.tool.SoulToolType;

public class SToolType implements IMessage {
    private int index;

    public SToolType() {}

    public SToolType(final SoulToolType type) {
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

    public static final class Handler implements IMessageHandler<SToolType, IMessage> {
        @Override
        public IMessage onMessage(final SToolType message, final MessageContext context) {
            final SoulToolType toolType = SoulToolType.getType(message.index);
            final EntityPlayerMP player = context.getServerHandler().player;
            final ISoulTool instance = SoulToolProvider.get(player);
            instance.setCurrentType(toolType);

            if (!SoulToolHelper.hasSoulTool(player)) {
                player.inventory.deleteStack(player.getHeldItemMainhand());
            }

            SoulItemHelper.addItemStack(new ItemStack(instance.getCurrentType().getItem()), player);

            return null;
        }
    }
}
