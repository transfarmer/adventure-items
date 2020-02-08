package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.data.SoulWeaponType;

import static transfarmer.soulweapons.capability.SoulWeaponHelper.ATTRIBUTES_LENGTH;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.ATTRIBUTE_POINTS;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SPENT_ATTRIBUTE_POINTS;

public class ServerResetAttributes implements IMessage {
    private int index;

    public ServerResetAttributes() {}

    public ServerResetAttributes(final SoulWeaponType type) {
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

    public static final class Handler implements IMessageHandler<ServerResetAttributes, IMessage> {
        @Override
        public IMessage onMessage(final ServerResetAttributes message, final MessageContext context) {
            final ISoulWeapon capability = context.getServerHandler().player.getCapability(CAPABILITY, null);
            final SoulWeaponType type = SoulWeaponType.getType(message.index);

            capability.addDatum(capability.getDatum(SPENT_ATTRIBUTE_POINTS, type), ATTRIBUTE_POINTS, type);
            capability.setDatum(0, SPENT_ATTRIBUTE_POINTS, type);
            capability.setAttributes(new float[3][ATTRIBUTES_LENGTH]);

            return new ClientResetAttributes(type);
        }
    }
}
