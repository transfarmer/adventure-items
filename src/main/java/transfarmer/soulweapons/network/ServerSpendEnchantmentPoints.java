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

public class ServerSpendEnchantmentPoints implements IMessage {
    private int amount;
    private int enchantmentIndex;
    private int typeIndex;

    public ServerSpendEnchantmentPoints() {}

    public ServerSpendEnchantmentPoints(final int amount, final SoulWeaponEnchantment enchantment, final SoulWeaponType type) {
        this.amount = amount;
        this.enchantmentIndex = enchantment.index;
        this.typeIndex = type.index;
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.amount = buffer.readInt();
        this.enchantmentIndex = buffer.readInt();
        this.typeIndex = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.amount);
        buffer.writeInt(this.enchantmentIndex);
        buffer.writeInt(this.typeIndex);
    }

    public static final class Handler implements IMessageHandler<ServerSpendEnchantmentPoints, IMessage> {
        @Override
        public IMessage onMessage(final ServerSpendEnchantmentPoints message, final MessageContext context) {
            final ISoulWeapon instance = context.getServerHandler().player.getCapability(CAPABILITY, null);
            final SoulWeaponEnchantment enchantment = SoulWeaponEnchantment.getEnchantment(message.enchantmentIndex);
            final SoulWeaponType weaponType = SoulWeaponType.getType(message.typeIndex);

            if (instance.getDatum(ENCHANTMENT_POINTS, weaponType) > 0) {
                instance.addEnchantment(message.amount, enchantment, weaponType);
                instance.addDatum(-message.amount, ENCHANTMENT_POINTS, weaponType);
                instance.addDatum(message.amount, SPENT_ENCHANTMENT_POINTS, weaponType);

                return new ClientSpendEnchantmentPoints(message.amount, enchantment, weaponType);
            }

            return null;
        }
    }
}
