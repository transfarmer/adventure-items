package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.data.SoulWeaponAttribute;
import transfarmer.soulweapons.capability.ISoulWeapon;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.POINTS;

public class ServerAddAttribute implements IMessage {
    private int index;

    public ServerAddAttribute() {}

    public ServerAddAttribute(SoulWeaponAttribute attribute) {
        this.index = attribute.index;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.index = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.index);
    }

    public static final class Handler implements IMessageHandler<ServerAddAttribute, IMessage> {
        @Override
        public IMessage onMessage(ServerAddAttribute message, MessageContext context) {
            final ISoulWeapon instance = context.getServerHandler().player.getCapability(CAPABILITY, null);
            final SoulWeaponAttribute attribute = SoulWeaponAttribute.getAttribute(message.index);

            if (instance.getDatum(POINTS, instance.getCurrentType()) > 0 ) {
                instance.addAttribute(attribute, instance.getCurrentType());

                return new ClientAddAttribute(attribute);
            }

            return null;
        }
    }
}