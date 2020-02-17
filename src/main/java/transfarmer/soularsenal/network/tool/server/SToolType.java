package transfarmer.soularsenal.network.tool.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soularsenal.capability.tool.ISoulTool;
import transfarmer.soularsenal.capability.tool.SoulToolHelper;
import transfarmer.soularsenal.capability.tool.SoulToolProvider;
import transfarmer.soularsenal.data.tool.SoulToolType;
import transfarmer.soularsenal.network.tool.client.CToolType;

public class SToolType implements IMessage {
    private int index;

    public SToolType() {
        this.index = -1;
    }

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
                player.inventory.clearMatchingItems(Items.WOODEN_PICKAXE, 0, 37, null);
            } else {
                SoulToolHelper.removeSoulTools(player);
            }

            if (slot >= 0) {
                final ItemStack boundSlotItem = player.inventory.getStackInSlot(slot);

                if (boundSlotItem.getItem() != Items.AIR && !SoulToolHelper.isSoulTool(boundSlotItem)) {
                    slot = -1;
                }
            }

            if (slot < 0) {
                slot = player.inventory.getFirstEmptyStack();
            }

            player.inventory.setInventorySlotContents(slot, new ItemStack(instance.getCurrentType().getItem()));

            return new CToolType(slot, toolType);
        }
    }
}
