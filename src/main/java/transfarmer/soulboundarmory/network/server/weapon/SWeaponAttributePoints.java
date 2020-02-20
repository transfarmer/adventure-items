package transfarmer.soulboundarmory.network.server.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.statistics.IAttribute;
import transfarmer.soulboundarmory.statistics.IType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;
import transfarmer.soulboundarmory.network.client.weapon.CWeaponSpendAttributePoints;

public class SWeaponAttributePoints implements IMessage {
    private int amount;
    private int attributeIndex;
    private int weaponIndex;

    public SWeaponAttributePoints() {}

    public SWeaponAttributePoints(final int amount, final IAttribute attribute, final IType type) {
        this.amount = amount;
        this.attributeIndex = attribute.getIndex();
        this.weaponIndex = type.getIndex();
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
            final IAttribute attribute = SoulWeaponAttribute.getAttribute(message.attributeIndex);
            final IType weaponType = SoulWeaponType.getType(message.weaponIndex);
            final ISoulWeapon instance = SoulWeaponProvider.get(context.getServerHandler().player);

            instance.addAttribute(message.amount, attribute, weaponType);

            return new CWeaponSpendAttributePoints(message.amount, attribute, weaponType);
        }
    }
}
