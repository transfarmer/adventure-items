package transfarmer.soulboundarmory.network.client.tool;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CToolBindSlot implements IMessage {
    private int slot;

    public CToolBindSlot() {}

    public CToolBindSlot(final int slot) {
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

    public static final class Handler implements IMessageHandler<CToolBindSlot, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CToolBindSlot message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final ISoulCapability capability = SoulToolProvider.get(minecraft.player);

            minecraft.addScheduledTask(() -> capability.bindSlot(message.slot));

            return null;
        }
    }
}
