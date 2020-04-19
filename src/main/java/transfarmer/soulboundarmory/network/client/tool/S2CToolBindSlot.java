package transfarmer.soulboundarmory.network.client.tool;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.IItemCapability;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CToolBindSlot implements IExtendedMessage {
    private int slot;

    public S2CToolBindSlot() {}

    public S2CToolBindSlot(final int slot) {
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

    public static final class Handler implements IExtendedMessageHandler<S2CToolBindSlot> {
        @SideOnly(CLIENT)
        @Override
        public IExtendedMessage onMessage(final S2CToolBindSlot message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final IItemCapability capability = ToolProvider.get(minecraft.player);

            minecraft.addScheduledTask(() -> capability.bindSlot(message.slot));

            return null;
        }
    }
}
