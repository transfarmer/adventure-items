package transfarmer.soulboundarmory.network.client.weapon;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CWeaponBindSlot implements IExtendedMessage {
    private int slot;

    public S2CWeaponBindSlot() {}

    public S2CWeaponBindSlot(final int slot) {
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

    public static final class Handler implements IExtendedMessageHandler<S2CWeaponBindSlot> {
        @SideOnly(CLIENT)
        @Override
        public IExtendedMessage onMessage(final S2CWeaponBindSlot message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final IWeapon capability = WeaponProvider.get(minecraft.player);

            minecraft.addScheduledTask(() -> capability.bindSlot(message.slot));

            return null;
        }
    }
}
