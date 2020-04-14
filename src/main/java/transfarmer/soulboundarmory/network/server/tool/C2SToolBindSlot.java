package transfarmer.soulboundarmory.network.server.tool;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.ISoulCapability;
import transfarmer.soulboundarmory.capability.soulbound.tool.SoulToolProvider;
import transfarmer.soulboundarmory.network.client.tool.S2CToolBindSlot;

public class C2SToolBindSlot implements IMessage {
    private int slot;

    public C2SToolBindSlot() {}

    public C2SToolBindSlot(final int slot) {
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

    public static final class Handler implements IMessageHandler<C2SToolBindSlot, IMessage> {
        @Override
        public IMessage onMessage(final C2SToolBindSlot message, final MessageContext context) {
            final EntityPlayer player = context.getServerHandler().player;
            final ISoulCapability capability = SoulToolProvider.get(player);

            if (capability.getBoundSlot() == message.slot) {
                capability.unbindSlot();
            } else {
                capability.bindSlot(message.slot);
            }

            return new S2CToolBindSlot(message.slot);
        }
    }
}
