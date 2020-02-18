package transfarmer.soulboundarmory.network.weapon.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponType;
import transfarmer.soulboundarmory.network.weapon.client.CWeaponResetAttributes;

import static transfarmer.soulboundarmory.capability.weapon.SoulWeaponHelper.ATTRIBUTES;
import static transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponDatum.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponDatum.SPENT_ATTRIBUTE_POINTS;

public class SWeaponResetAttributes implements IMessage {
    private int index;

    public SWeaponResetAttributes() {}

    public SWeaponResetAttributes(final SoulWeaponType type) {
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

    public static final class Handler implements IMessageHandler<SWeaponResetAttributes, IMessage> {
        @Override
        public IMessage onMessage(final SWeaponResetAttributes message, final MessageContext context) {
            final ISoulWeapon capability = context.getServerHandler().player.getCapability(CAPABILITY, null);
            final SoulWeaponType type = SoulWeaponType.getType(message.index);

            capability.addDatum(capability.getDatum(SPENT_ATTRIBUTE_POINTS, type), ATTRIBUTE_POINTS, type);
            capability.setDatum(0, SPENT_ATTRIBUTE_POINTS, type);
            capability.setAttributes(new float[ATTRIBUTES], type);

            return new CWeaponResetAttributes(type);
        }
    }
}
