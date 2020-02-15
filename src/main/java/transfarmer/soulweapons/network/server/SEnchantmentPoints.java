package transfarmer.soulweapons.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.data.SoulWeaponEnchantment;
import transfarmer.soulweapons.data.SoulWeaponType;
import transfarmer.soulweapons.network.client.CSpendEnchantmentPoints;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class SEnchantmentPoints implements IMessage {
    private int amount;
    private int enchantmentIndex;
    private int typeIndex;

    public SEnchantmentPoints() {}

    public SEnchantmentPoints(final int amount, final SoulWeaponEnchantment enchantment, final SoulWeaponType type) {
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

    public static final class Handler implements IMessageHandler<SEnchantmentPoints, IMessage> {
        @Override
        public IMessage onMessage(final SEnchantmentPoints message, final MessageContext context) {
            final ISoulWeapon instance = context.getServerHandler().player.getCapability(CAPABILITY, null);
            final SoulWeaponEnchantment enchantment = SoulWeaponEnchantment.getEnchantment(message.enchantmentIndex);
            final SoulWeaponType weaponType = SoulWeaponType.getType(message.typeIndex);

            instance.addEnchantment(message.amount, enchantment, weaponType);

            return new CSpendEnchantmentPoints(message.amount, enchantment, weaponType);
        }
    }
}
