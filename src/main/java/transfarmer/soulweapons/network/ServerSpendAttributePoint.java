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

public class ServerSpendAttributePoint implements IMessage {
    private int attributeIndex;
    private int weaponIndex;

    public ServerSpendAttributePoint() {}

    public ServerSpendAttributePoint(final SoulWeaponAttribute attribute, final SoulWeaponType type) {
        this.attributeIndex = attribute.index;
        this.weaponIndex = type.index;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.attributeIndex = buffer.readInt();
        this.weaponIndex = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.attributeIndex);
        buffer.writeInt(this.weaponIndex);
    }

    public static final class Handler implements IMessageHandler<ServerSpendAttributePoint, IMessage> {
        @Override
        public IMessage onMessage(final ServerSpendAttributePoint message, final MessageContext context) {
            final SoulWeaponAttribute attribute = SoulWeaponAttribute.getAttribute(message.attributeIndex);
            final SoulWeaponType weaponType = SoulWeaponType.getType(message.weaponIndex);
            final ISoulWeapon instance = context.getServerHandler().player.getCapability(CAPABILITY, null);

            if (instance.getDatum(ATTRIBUTE_POINTS, weaponType) > 0 ) {
                instance.addAttribute(attribute, weaponType);
                instance.addDatum(-1, ATTRIBUTE_POINTS, weaponType);
                instance.addDatum(1, SPENT_ATTRIBUTE_POINTS, weaponType);

                return new ClientSpendAttributePoint(attribute, weaponType);
            }

            return null;
        }
    }
}
