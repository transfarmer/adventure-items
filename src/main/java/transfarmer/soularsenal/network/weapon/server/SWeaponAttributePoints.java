package transfarmer.soularsenal.network.weapon.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soularsenal.capability.weapon.ISoulWeapon;
import transfarmer.soularsenal.data.weapon.SoulWeaponAttribute;
import transfarmer.soularsenal.data.weapon.SoulWeaponType;
import transfarmer.soularsenal.network.weapon.client.CWeaponSpendAttributePoints;

import static transfarmer.soularsenal.capability.weapon.SoulWeaponProvider.CAPABILITY;

public class SWeaponAttributePoints implements IMessage {
    private int amount;
    private int attributeIndex;
    private int weaponIndex;

    public SWeaponAttributePoints() {}

    public SWeaponAttributePoints(final int amount, final SoulWeaponAttribute attribute, final SoulWeaponType type) {
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

    public static final class Handler implements IMessageHandler<SWeaponAttributePoints, IMessage> {
        @Override
        public IMessage onMessage(final SWeaponAttributePoints message, final MessageContext context) {
            final SoulWeaponAttribute attribute = SoulWeaponAttribute.getAttribute(message.attributeIndex);
            final SoulWeaponType weaponType = SoulWeaponType.getType(message.weaponIndex);
            final ISoulWeapon instance = context.getServerHandler().player.getCapability(CAPABILITY, null);

            instance.addAttribute(message.amount, attribute, weaponType);

            return new CWeaponSpendAttributePoints(message.amount, attribute, weaponType);
        }
    }
}
