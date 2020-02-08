package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.gui.SoulWeaponMenu;
import transfarmer.soulweapons.data.SoulWeaponEnchantment;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SPENT_ENCHANTMENT_POINTS;

public class ClientSpentEnchantmentPoint implements IMessage {
    private int index;

    public ClientSpentEnchantmentPoint() {}

    public ClientSpentEnchantmentPoint(final SoulWeaponEnchantment enchantment) {
        this.index = enchantment.index;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.index = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.index);
    }

    public static final class Handler implements IMessageHandler<ClientSpentEnchantmentPoint, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final ClientSpentEnchantmentPoint message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final ISoulWeapon instance = minecraft.player.getCapability(CAPABILITY, null);

            minecraft.addScheduledTask(() -> {
                instance.addEnchantment(SoulWeaponEnchantment.getEnchantment(message.index), instance.getCurrentType());
                instance.addDatum(1, SPENT_ENCHANTMENT_POINTS, instance.getCurrentType());
                minecraft.displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}
