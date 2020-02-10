package transfarmer.soulweapons.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.capability.ISoulWeapon;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class CBindSlot implements IMessage {
    private int slot;

    public CBindSlot() {}

    public CBindSlot(final int slot, final boolean moveToSlot) {
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

    public static final class Handler implements IMessageHandler<CBindSlot, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CBindSlot message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final ISoulWeapon capability = minecraft.player.getCapability(CAPABILITY, null);

            minecraft.addScheduledTask(() -> {
                if (capability.getBoundSlot() == message.slot) {
                    capability.unbindSlot();
                } else {
                    capability.setBoundSlot(message.slot);
                }
            });

            return null;
        }
    }
}
