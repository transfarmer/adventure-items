package transfarmer.soulboundarmory.network.server.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.network.client.weapon.S2CWeaponSpentAttributePoints;
import transfarmer.soulboundarmory.statistics.SoulAttribute;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;

public class C2SWeaponAttributePoints implements IMessage {
    private int amount;
    private int attributeIndex;
    private int weaponIndex;

    public C2SWeaponAttributePoints() {}

    public C2SWeaponAttributePoints(final int amount, final SoulAttribute attribute, final SoulType type) {
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

    public static final class Handler implements IMessageHandler<C2SWeaponAttributePoints, IMessage> {
        @Override
        public IMessage onMessage(final C2SWeaponAttributePoints message, final MessageContext context) {
            final SoulAttribute attribute = SoulWeaponAttribute.get(message.attributeIndex);
            final SoulType type = SoulWeaponType.get(message.weaponIndex);
            final ISoulWeapon instance = SoulWeaponProvider.get(context.getServerHandler().player);

            instance.addAttribute(message.amount, attribute, type);

            return new S2CWeaponSpentAttributePoints(message.amount, attribute, type);
        }
    }
}
