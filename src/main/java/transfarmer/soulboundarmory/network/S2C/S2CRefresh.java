package transfarmer.soulboundarmory.network.S2C;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.IExtendedMessage;
import transfarmer.soulboundarmory.network.common.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CRefresh extends S2CSoulbound {
    public S2CRefresh() {}

    public S2CRefresh(final ISoulboundComponent capability, final IItem item) {
        super(capability, item);
    }

    @Override
    @Environment(CLIENT)
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        super.fromBytes(buffer);
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        super.toBytes(buffer);
    }

    public static final class Handler implements IExtendedMessageHandler<S2CRefresh> {
        @Environment(CLIENT)
        @Override
        public IExtendedMessage onMessage(final S2CRefresh message, final MessageContext context) {
            CLIENT.addScheduledTask(message.capability::refresh);

            return null;
        }
    }
}
