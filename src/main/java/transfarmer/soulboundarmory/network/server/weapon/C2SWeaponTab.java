package transfarmer.soulboundarmory.network.server.weapon;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;

public class C2SWeaponTab implements IExtendedMessage {
    int tab;

    public C2SWeaponTab() {}

    public C2SWeaponTab(final int tab) {
        this.tab = tab;
    }

    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.tab = buffer.readInt();
    }

    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeInt(this.tab);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SWeaponTab> {
        @Override
        public IExtendedMessage onMessage(C2SWeaponTab message, MessageContext context) {
            WeaponProvider.get(context.getServerHandler().player).setCurrentTab(message.tab);

            return null;
        }
    }
}
