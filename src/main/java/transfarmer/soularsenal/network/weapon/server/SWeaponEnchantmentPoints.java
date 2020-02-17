package transfarmer.soularsenal.network.weapon.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soularsenal.capability.weapon.ISoulWeapon;
import transfarmer.soularsenal.data.weapon.SoulWeaponEnchantment;
import transfarmer.soularsenal.data.weapon.SoulWeaponType;
import transfarmer.soularsenal.network.weapon.client.CWeaponSpendEnchantmentPoints;

import static transfarmer.soularsenal.capability.weapon.SoulWeaponProvider.CAPABILITY;

public class SWeaponEnchantmentPoints implements IMessage {
    private int amount;
    private int enchantmentIndex;
    private int typeIndex;

    public SWeaponEnchantmentPoints() {}

    public SWeaponEnchantmentPoints(final int amount, final SoulWeaponEnchantment enchantment, final SoulWeaponType type) {
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

    public static final class Handler implements IMessageHandler<SWeaponEnchantmentPoints, IMessage> {
        @Override
        public IMessage onMessage(final SWeaponEnchantmentPoints message, final MessageContext context) {
            final ISoulWeapon instance = context.getServerHandler().player.getCapability(CAPABILITY, null);
            final SoulWeaponEnchantment enchantment = SoulWeaponEnchantment.getEnchantment(message.enchantmentIndex);
            final SoulWeaponType weaponType = SoulWeaponType.getType(message.typeIndex);

            instance.addEnchantment(message.amount, enchantment, weaponType);

            return new CWeaponSpendEnchantmentPoints(message.amount, enchantment, weaponType);
        }
    }
}
