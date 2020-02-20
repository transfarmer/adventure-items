package transfarmer.soulboundarmory.network.server.tool;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.network.client.tool.CToolBindSlot;

public class SToolBindSlot implements IMessage {
    public SToolBindSlot() {}

    @Override
    public void fromBytes(final ByteBuf buffer) {}

    @Override
    public void toBytes(final ByteBuf buffer) {}

    public static final class Handler implements IMessageHandler<SToolBindSlot, IMessage> {
        @Override
        public IMessage onMessage(final SToolBindSlot message, final MessageContext context) {
            final EntityPlayer player = context.getServerHandler().player;
            final ISoulCapability capability = SoulToolProvider.get(player);
            final int slot = player.inventory.currentItem;

            if (capability.getBoundSlot() == slot) {
                capability.unbindSlot();
            } else {
                capability.bindSlot(slot);
            }

            return new CToolBindSlot(slot);
        }
    }
}
