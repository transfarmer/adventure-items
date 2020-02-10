package transfarmer.soulweapons.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.data.SoulWeaponAttribute;
import transfarmer.soulweapons.data.SoulWeaponType;
import transfarmer.soulweapons.network.client.CSpendAttributePoints;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class SAttributePoints implements IMessage {
    private int amount;
    private int attributeIndex;
    private int weaponIndex;

    public SAttributePoints() {}

    public SAttributePoints(final int amount, final SoulWeaponAttribute attribute, final SoulWeaponType type) {
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

    public static final class Handler implements IMessageHandler<SAttributePoints, IMessage> {
        @Override
        public IMessage onMessage(final SAttributePoints message, final MessageContext context) {
            final SoulWeaponAttribute attribute = SoulWeaponAttribute.getAttribute(message.attributeIndex);
            final SoulWeaponType weaponType = SoulWeaponType.getType(message.weaponIndex);
            final ISoulWeapon instance = context.getServerHandler().player.getCapability(CAPABILITY, null);

            instance.addAttribute(message.amount, attribute, weaponType);

            return new CSpendAttributePoints(message.amount, attribute, weaponType);
        }
    }
}
