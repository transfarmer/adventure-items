package transfarmer.soulboundarmory.network.tool.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolHelper;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.data.tool.SoulToolType;
import transfarmer.soulboundarmory.network.tool.client.CToolType;

public class SToolType implements IMessage {
    private int index;

    public SToolType() {}

    public SToolType(final SoulToolType ToolType) {
        this.index = ToolType.index;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.index = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(index);
    }

    public static final class Handler implements IMessageHandler<SToolType, IMessage> {
        @Override
        public IMessage onMessage(SToolType message, MessageContext context) {
            final SoulToolType toolType = SoulToolType.getType(message.index);
            final EntityPlayerMP player = context.getServerHandler().player;
            final ISoulTool instance = SoulToolProvider.get(player);
            int slot = instance.getBoundSlot();
            instance.setCurrentType(toolType);

            if (!SoulToolHelper.hasSoulTool(player)) {
                player.inventory.clearMatchingItems(Items.WOODEN_PICKAXE, -1, 37, null);
            } else {
                SoulToolHelper.removeSoulTools(player);
            }

            SoulToolHelper.addItemStack(new ItemStack(instance.getCurrentType().getItem()), player);

            return new CToolType(slot, toolType);
        }
    }
}
