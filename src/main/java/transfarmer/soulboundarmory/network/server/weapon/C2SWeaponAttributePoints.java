package transfarmer.soulboundarmory.network.server.weapon;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.network.client.weapon.S2CWeaponSpentAttributePoints;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;

public class C2SWeaponAttributePoints implements IExtendedMessage {
    private String item;
    private String statistic;
    private int amount;

    public C2SWeaponAttributePoints() {}

    public C2SWeaponAttributePoints(final IItem item, final IStatistic statistic, final int amount) {
        this.item = item.toString();
        this.statistic = statistic.toString();
        this.amount = amount;
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.amount = buffer.readInt();
        this.statistic = buffer.readString();
        this.item = buffer.readString();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeInt(this.amount);
        buffer.writeString(this.statistic);
        buffer.writeString(this.item);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SWeaponAttributePoints> {
        @Override
        public IExtendedMessage onMessage(final C2SWeaponAttributePoints message, final MessageContext context) {
            final IStatistic attribute = IStatistic.get(message.statistic);
            final IItem type = IItem.get(message.item);
            final IWeapon instance = WeaponProvider.get(context.getServerHandler().player);

            instance.addAttribute(type, attribute, message.amount);

            return new S2CWeaponSpentAttributePoints(type, attribute, message.amount);
        }
    }
}
