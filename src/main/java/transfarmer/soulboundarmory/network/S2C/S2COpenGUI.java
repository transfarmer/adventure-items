package transfarmer.soulboundarmory.network.S2C;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTabSoulbound;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.IExtendedMessage;
import transfarmer.soulboundarmory.network.common.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2COpenGUI implements IExtendedMessage {
    String capability;
    int tab;

    public S2COpenGUI() {
    }

    public S2COpenGUI(final ICapabilityType capability, final int tab) {
        this.capability = capability.toString();
        this.tab = tab;
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.capability = buffer.readString();
        this.tab = buffer.readInt();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.capability);
        buffer.writeInt(this.tab);
    }

    public static final class Handler implements IExtendedMessageHandler<S2COpenGUI> {
        @Override
        @SideOnly(CLIENT)
        public IExtendedMessage onMessage(final S2COpenGUI message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();

            minecraft.addScheduledTask(() -> {
                if (minecraft.currentScreen instanceof GuiTabSoulbound) {
                    minecraft.player.getCapability(ICapabilityType.get(message.capability).getCapability(), null).openGUI(message.tab);
                }
            });

            return null;
        }
    }
}
