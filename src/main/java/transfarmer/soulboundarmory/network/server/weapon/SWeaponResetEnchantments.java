package transfarmer.soulboundarmory.network.server.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.data.IType;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponType;
import transfarmer.soulboundarmory.network.client.weapon.CWeaponResetEnchantments;

public class SWeaponResetEnchantments implements IMessage {
    private int index;

    public SWeaponResetEnchantments() {
    }

    public SWeaponResetEnchantments(final IType type) {
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

    public static final class Handler implements IMessageHandler<SWeaponResetEnchantments, IMessage> {
        @Override
        public IMessage onMessage(final SWeaponResetEnchantments message, final MessageContext context) {
            final ISoulWeapon capability = SoulWeaponProvider.get(context.getServerHandler().player);
            final IType type = SoulWeaponType.getType(message.index);

            capability.addDatum(capability.getDatum(capability.getEnumSpentEnchantmentPoints(), type), capability.getEnumEnchantmentPoints(), type);
            capability.setDatum(0, capability.getEnumSpentEnchantmentPoints(), type);
            capability.setEnchantments(new int[capability.getEnchantmentAmount()], type);

            return new CWeaponResetEnchantments(type);
        }
    }
}
