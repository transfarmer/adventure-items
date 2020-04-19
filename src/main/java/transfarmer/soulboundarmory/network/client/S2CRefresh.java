package transfarmer.soulboundarmory.network.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.client.gui.Menu;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CRefresh implements IExtendedMessage {
    String capability;

    public S2CRefresh() {
    }

    public S2CRefresh(final ICapabilityType capability) {
        this.capability = capability.toString();
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.capability = buffer.readString();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.capability);
    }

    public static final class Handler implements IExtendedMessageHandler<S2CRefresh> {
        @Override
        @SideOnly(CLIENT)
        public IExtendedMessage onMessage(final S2CRefresh message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();

            minecraft.addScheduledTask(() -> {
                if (minecraft.currentScreen instanceof Menu) {
                    minecraft.player.getCapability(ICapabilityType.get(message.capability).getCapability(), null).onKeyPress();
                }
            });

            return null;
        }
    }
}
