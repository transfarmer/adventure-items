package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.data.SoulWeaponEnchantment;
import transfarmer.soulweapons.data.SoulWeaponType;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.ENCHANTMENT_POINTS;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SPENT_ENCHANTMENT_POINTS;

public class ServerSpendEnchantmentPoint implements IMessage {
    private int enchantmentIndex;
    private int typeIndex;

    public ServerSpendEnchantmentPoint() {}

    public ServerSpendEnchantmentPoint(final SoulWeaponEnchantment enchantment, final SoulWeaponType type) {
        this.enchantmentIndex = enchantment.index;
        this.typeIndex = type.index;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.enchantmentIndex = buffer.readInt();
        this.typeIndex = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.enchantmentIndex);
        buffer.writeInt(this.typeIndex);
    }

    public static final class Handler implements IMessageHandler<ServerSpendEnchantmentPoint, IMessage> {
        @Override
        public IMessage onMessage(final ServerSpendEnchantmentPoint message, final MessageContext context) {
            final ISoulWeapon instance = context.getServerHandler().player.getCapability(CAPABILITY, null);
            final SoulWeaponEnchantment enchantment = SoulWeaponEnchantment.getEnchantment(message.enchantmentIndex);
            final SoulWeaponType weaponType = SoulWeaponType.getType(message.typeIndex);

            if (instance.getDatum(ENCHANTMENT_POINTS, weaponType) > 0) {
                instance.addEnchantment(enchantment, weaponType);
                instance.addDatum(-1, ENCHANTMENT_POINTS, weaponType);
                instance.addDatum(1, SPENT_ENCHANTMENT_POINTS, weaponType);

                return new ClientSpendEnchantmentPoint(enchantment, weaponType);
            }

            return null;
        }
    }
}
