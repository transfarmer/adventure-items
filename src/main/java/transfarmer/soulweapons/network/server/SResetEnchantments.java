package transfarmer.soulweapons.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.data.SoulWeaponType;
import transfarmer.soulweapons.network.client.CResetEnchantments;

import static transfarmer.soulweapons.capability.SoulWeaponHelper.ENCHANTMENTS;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.ENCHANTMENT_POINTS;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SPENT_ENCHANTMENT_POINTS;

public class SResetEnchantments implements IMessage {
    private int index;

    public SResetEnchantments() {
    }

    public SResetEnchantments(final SoulWeaponType type) {
        this.index = type.index;
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.index = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.index);
    }

    public static final class Handler implements IMessageHandler<SResetEnchantments, IMessage> {
        @Override
        public IMessage onMessage(final SResetEnchantments message, final MessageContext context) {
            final ISoulWeapon capability = context.getServerHandler().player.getCapability(CAPABILITY, null);
            final SoulWeaponType type = SoulWeaponType.getType(message.index);

            capability.addDatum(capability.getDatum(SPENT_ENCHANTMENT_POINTS, type), ENCHANTMENT_POINTS, type);
            capability.setDatum(0, SPENT_ENCHANTMENT_POINTS, type);
            capability.setEnchantments(new int[ENCHANTMENTS], type);

            return new CResetEnchantments(type);
        }
    }
}
