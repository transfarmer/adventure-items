package transfarmer.soularsenal.network.tool.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soularsenal.capability.tool.ISoulTool;
import transfarmer.soularsenal.capability.tool.SoulToolProvider;
import transfarmer.soularsenal.data.tool.SoulToolType;
import transfarmer.soularsenal.network.tool.client.CToolBindSlot;

public class SToolBindSlot implements IMessage {
    private int slot;

    public SToolBindSlot() {
    }

    public SToolBindSlot(final int slot) {
        this.slot = slot;
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.slot = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.slot);
    }

    public static final class Handler implements IMessageHandler<SToolBindSlot, IMessage> {
        @Override
        public IMessage onMessage(final SToolBindSlot message, final MessageContext context) {
            final EntityPlayer player = context.getServerHandler().player;
            final ISoulTool capability = SoulToolProvider.get(player);
            final NonNullList<ItemStack> inventory = player.inventory.mainInventory;

            if (capability.getBoundSlot() == message.slot) {
                capability.unbindSlot();
            } else {
                if (inventory.get(message.slot).isEmpty()) {
                    for (final ItemStack itemStack : inventory) {
                        if (SoulToolType.getType(itemStack) == capability.getCurrentType()) {
                            inventory.set(capability.getBoundSlot(), ItemStack.EMPTY);
                            player.inventory.setInventorySlotContents(message.slot, itemStack);
                        }
                    }
                }

                capability.setBoundSlot(message.slot);
            }

            return new CToolBindSlot(message.slot);
        }
    }
}
