package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.data.SoulWeaponEnchantment;
import transfarmer.soulweapons.data.SoulWeaponType;
import transfarmer.soulweapons.gui.SoulWeaponMenu;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.ENCHANTMENT_POINTS;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SPENT_ENCHANTMENT_POINTS;

public class ClientSpendEnchantmentPoints implements IMessage {
    private int amount;
    private int enchantmentIndex;
    private int weaponIndex;

    public ClientSpendEnchantmentPoints() {}

    public ClientSpendEnchantmentPoints(final int amount, final SoulWeaponEnchantment enchantment, final SoulWeaponType type) {
        this.amount = amount;
        this.enchantmentIndex = enchantment.index;
        this.weaponIndex = type.index;
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.amount = buffer.readInt();
        this.enchantmentIndex = buffer.readInt();
        this.weaponIndex = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.amount);
        buffer.writeInt(this.enchantmentIndex);
        buffer.writeInt(this.weaponIndex);
    }

    public static final class Handler implements IMessageHandler<ClientSpendEnchantmentPoints, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final ClientSpendEnchantmentPoints message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final SoulWeaponEnchantment enchantment = SoulWeaponEnchantment.getEnchantment(message.enchantmentIndex);
            final SoulWeaponType weaponType = SoulWeaponType.getType(message.weaponIndex);
            final ISoulWeapon instance = minecraft.player.getCapability(CAPABILITY, null);

            minecraft.addScheduledTask(() -> {
                instance.addEnchantment(message.amount, enchantment, weaponType);
                instance.addDatum(-message.amount, ENCHANTMENT_POINTS, weaponType);
                instance.addDatum(message.amount, SPENT_ENCHANTMENT_POINTS, weaponType);
                minecraft.displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}
