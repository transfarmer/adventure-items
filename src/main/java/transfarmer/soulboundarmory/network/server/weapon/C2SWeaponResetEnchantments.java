package transfarmer.soulboundarmory.network.server.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.network.client.weapon.S2CWeaponResetEnchantments;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;

public class C2SWeaponResetEnchantments implements IMessage {
    private int index;

    public C2SWeaponResetEnchantments() {
    }

    public C2SWeaponResetEnchantments(final SoulType type) {
        this.index = type.getIndex();
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.index = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.index);
    }

    public static final class Handler implements IMessageHandler<C2SWeaponResetEnchantments, IMessage> {
        @Override
        public IMessage onMessage(final C2SWeaponResetEnchantments message, final MessageContext context) {
            final ISoulWeapon capability = SoulWeaponProvider.get(context.getServerHandler().player);
            final SoulType type = SoulWeaponType.get(message.index);

            capability.addDatum(capability.getDatum(SoulDatum.DATA.spentEnchantmentPoints, type), SoulDatum.DATA.enchantmentPoints, type);
            capability.setDatum(0, SoulDatum.DATA.spentEnchantmentPoints, type);
            capability.setEnchantments(new int[capability.getEnchantmentAmount()], type);

            return new S2CWeaponResetEnchantments(type);
        }
    }
}
