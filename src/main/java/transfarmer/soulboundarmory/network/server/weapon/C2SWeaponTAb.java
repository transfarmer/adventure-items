package transfarmer.soulboundarmory.network.server.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.weapon.SoulWeaponProvider;

public class C2SWeaponTAb implements IMessage {
    int tab;

    public C2SWeaponTAb() {}

    public C2SWeaponTAb(final int tab) {
        this.tab = tab;
    }

    public void fromBytes(final ByteBuf buffer) {
        this.tab = buffer.readInt();
    }

    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.tab);
    }

    public static final class Handler implements IMessageHandler<C2SWeaponTAb, IMessage> {
        @Override
        public IMessage onMessage(C2SWeaponTAb message, MessageContext context) {
            SoulWeaponProvider.get(context.getServerHandler().player).setCurrentTab(message.tab);

            return null;
        }
    }
}
