package transfarmer.soulboundarmory.network.client.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CWeaponBindSlot implements IMessage {
    private int slot;

    public CWeaponBindSlot() {}

    public CWeaponBindSlot(final int slot) {
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

    public static final class Handler implements IMessageHandler<CWeaponBindSlot, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CWeaponBindSlot message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final ISoulWeapon capability = SoulWeaponProvider.get(minecraft.player);

            minecraft.addScheduledTask(() -> capability.bindSlot(message.slot));

            return null;
        }
    }
}
