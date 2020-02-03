package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.data.SoulWeaponEnchantment;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.ENCHANTMENT_POINTS;

public class ServerAddEnchantment implements IMessage {
    private int index;

    public ServerAddEnchantment() {}

    public ServerAddEnchantment(final SoulWeaponEnchantment enchantment) {
        this.index = enchantment.index;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.index = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.index);
    }

    public static final class Handler implements IMessageHandler<ServerAddEnchantment, IMessage> {
        @Override
        public IMessage onMessage(final ServerAddEnchantment message, final MessageContext context) {
            final ISoulWeapon instance = context.getServerHandler().player.getCapability(CAPABILITY, null);
            final SoulWeaponEnchantment enchantment = SoulWeaponEnchantment.getEnchantment(message.index);

            if (instance.getDatum(ENCHANTMENT_POINTS, instance.getCurrentType()) > 0) {
                instance.addEnchantment(enchantment, instance.getCurrentType());

                return new ClientAddEnchantment(enchantment);
            }

            return null;
        }
    }
}
