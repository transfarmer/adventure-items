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

public class ClientSpendEnchantmentPoint implements IMessage {
    private int enchantmentIndex;
    private int weaponIndex;

    public ClientSpendEnchantmentPoint() {}

    public ClientSpendEnchantmentPoint(final SoulWeaponEnchantment enchantment, final SoulWeaponType type) {
        this.enchantmentIndex = enchantment.index;
        this.weaponIndex = type.index;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.enchantmentIndex = buffer.readInt();
        this.weaponIndex = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.enchantmentIndex);
        buffer.writeInt(this.weaponIndex);
    }

    public static final class Handler implements IMessageHandler<ClientSpendEnchantmentPoint, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final ClientSpendEnchantmentPoint message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final SoulWeaponType weaponType = SoulWeaponType.getType(message.weaponIndex);
            final ISoulWeapon instance = minecraft.player.getCapability(CAPABILITY, null);

            minecraft.addScheduledTask(() -> {
                instance.addEnchantment(SoulWeaponEnchantment.getEnchantment(message.enchantmentIndex), weaponType);
                instance.addDatum(-1, ENCHANTMENT_POINTS, weaponType);
                instance.addDatum(1, SPENT_ENCHANTMENT_POINTS, weaponType);
                minecraft.displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}
