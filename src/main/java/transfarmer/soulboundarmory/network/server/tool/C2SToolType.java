package transfarmer.soulboundarmory.network.server.tool;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.ISoulCapability;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.capability.soulbound.tool.SoulToolProvider;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

public class C2SToolType implements IMessage {
    private int index;

    public C2SToolType() {}

    public C2SToolType(final SoulType type) {
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

    public static final class Handler implements IMessageHandler<C2SToolType, IMessage> {
        @Override
        public IMessage onMessage(final C2SToolType message, final MessageContext context) {
            final SoulType toolType = SoulToolType.get(message.index);
            final EntityPlayerMP player = context.getServerHandler().player;
            final ISoulCapability capability = SoulToolProvider.get(player);
            capability.setCurrentType(toolType);

            if (!capability.hasSoulItem()) {
                player.inventory.deleteStack(player.getHeldItemMainhand());
            }

            SoulItemHelper.addItemStack(new ItemStack(capability.getCurrentType().getItem()), player);

            return null;
        }
    }
}
