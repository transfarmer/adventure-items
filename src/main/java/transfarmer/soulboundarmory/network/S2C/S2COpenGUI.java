package transfarmer.soulboundarmory.network.S2C;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.client.gui.screen.common.SoulboundTab;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2COpenGUI implements IExtendedMessage {
    String component;
    int tab;

    public S2COpenGUI() {
    }

    public S2COpenGUI(final IComponentType component, final int tab) {
        this.component = component.toString();
        this.tab = tab;
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.component = buffer.readString();
        this.tab = buffer.readInt();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.component);
        buffer.writeInt(this.tab);
    }

    public static final class Handler implements IExtendedMessageHandler<S2COpenGUI> {
        @Override
        @Environment(CLIENT)
        public IExtendedMessage onMessage(final S2COpenGUI message, final MessageContext context) {
            final Minecraft minecraft = CLIENT;

            minecraft.addScheduledTask(() -> {
                if (minecraft.currentScreen instanceof SoulboundTab) {
                    minecraft.player.getComponent(IComponentType.get(component).getComponent(), null).openGUI(tab);
                }
            });

            return null;
        }
    }
}
