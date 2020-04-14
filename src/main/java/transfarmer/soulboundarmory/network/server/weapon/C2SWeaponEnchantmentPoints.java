package transfarmer.soulboundarmory.network.server.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.network.client.weapon.S2CWeaponSpendEnchantmentPoints;
import transfarmer.soulboundarmory.statistics.SoulEnchantment;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponEnchantment;

public class C2SWeaponEnchantmentPoints implements IMessage {
    private int amount;
    private int enchantmentIndex;
    private int typeIndex;

    public C2SWeaponEnchantmentPoints() {}

    public C2SWeaponEnchantmentPoints(final int amount, final SoulEnchantment enchantment, final SoulType type) {
        this.amount = amount;
        this.enchantmentIndex = enchantment.getIndex();
        this.typeIndex = type.getIndex();
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

    public static final class Handler implements IMessageHandler<C2SWeaponEnchantmentPoints, IMessage> {
        @Override
        public IMessage onMessage(final C2SWeaponEnchantmentPoints message, final MessageContext context) {
            final ISoulWeapon instance = SoulWeaponProvider.get(context.getServerHandler().player);
            final SoulEnchantment enchantment = SoulWeaponEnchantment.get(message.enchantmentIndex);
            final SoulType weaponType = instance.getType(message.typeIndex);

            instance.addEnchantment(message.amount, enchantment, weaponType);

            return new S2CWeaponSpendEnchantmentPoints(message.amount, enchantment, weaponType);
        }
    }
}
