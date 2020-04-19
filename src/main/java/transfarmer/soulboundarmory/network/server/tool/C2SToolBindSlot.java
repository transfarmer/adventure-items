package transfarmer.soulboundarmory.network.server.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.IItemCapability;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.network.client.tool.S2CToolBindSlot;

public class C2SToolBindSlot implements IExtendedMessage {
    private int slot;

    public C2SToolBindSlot() {}

    public C2SToolBindSlot(final int slot) {
        this.slot = slot;
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.slot = buffer.readInt();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeInt(this.slot);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SToolBindSlot> {
        @Override
        public IExtendedMessage onMessage(final C2SToolBindSlot message, final MessageContext context) {
            final EntityPlayer player = context.getServerHandler().player;
            final IItemCapability capability = ToolProvider.get(player);

            if (capability.getBoundSlot() == message.slot) {
                capability.unbindSlot();
            } else {
                capability.bindSlot(message.slot);
            }

            return new S2CToolBindSlot(message.slot);
        }
    }
}
