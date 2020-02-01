package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.weapon.SoulWeaponAttribute;
import transfarmer.soulweapons.capability.ISoulWeapon;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

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
            ISoulWeapon instance = context.getServerHandler().player.getCapability(CAPABILITY, null);
            SoulWeaponAttribute attribute = SoulWeaponAttribute.getAttribute(message.index);

            if (instance.getPoints() == 0 ) return null;

            instance.addAttribute(attribute);
            return new ClientAddAttribute(attribute);
        }
    }
}
