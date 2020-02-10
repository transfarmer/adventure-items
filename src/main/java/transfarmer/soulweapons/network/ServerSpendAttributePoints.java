package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.data.SoulWeaponAttribute;
import transfarmer.soulweapons.data.SoulWeaponType;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.ATTRIBUTE_POINTS;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SPENT_ATTRIBUTE_POINTS;

public class ServerSpendAttributePoints implements IMessage {
    private int amount;
    private int attributeIndex;
    private int weaponIndex;

    public ServerSpendAttributePoints() {}

    public ServerSpendAttributePoints(final int amount, final SoulWeaponAttribute attribute, final SoulWeaponType type) {
        this.amount = amount;
        this.attributeIndex = attribute.index;
        this.weaponIndex = type.index;
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.amount = buffer.readInt();
        this.attributeIndex = buffer.readInt();
        this.weaponIndex = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.amount);
        buffer.writeInt(this.attributeIndex);
        buffer.writeInt(this.weaponIndex);
    }

    public static final class Handler implements IMessageHandler<ServerSpendAttributePoints, IMessage> {
        @Override
        public IMessage onMessage(final ServerSpendAttributePoints message, final MessageContext context) {
            final SoulWeaponAttribute attribute = SoulWeaponAttribute.getAttribute(message.attributeIndex);
            final SoulWeaponType weaponType = SoulWeaponType.getType(message.weaponIndex);
            final ISoulWeapon instance = context.getServerHandler().player.getCapability(CAPABILITY, null);

            if (instance.getDatum(ATTRIBUTE_POINTS, weaponType) > 0 ) {
                instance.addAttribute(message.amount, attribute, weaponType);
                instance.addDatum(-message.amount, ATTRIBUTE_POINTS, weaponType);
                instance.addDatum(message.amount, SPENT_ATTRIBUTE_POINTS, weaponType);

                return new ClientSpendAttributePoints(message.amount, attribute, weaponType);
            }

            return null;
        }
    }
}
