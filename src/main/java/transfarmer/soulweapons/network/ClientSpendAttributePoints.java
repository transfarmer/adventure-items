package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.data.SoulWeaponAttribute;
import transfarmer.soulweapons.data.SoulWeaponType;
import transfarmer.soulweapons.gui.SoulWeaponMenu;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.ATTRIBUTE_POINTS;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SPENT_ATTRIBUTE_POINTS;

public class ClientSpendAttributePoints implements IMessage {
    private int amount;
    private int attributeIndex;
    private int weaponIndex;

    public ClientSpendAttributePoints() {}

    public ClientSpendAttributePoints(final int amount, final SoulWeaponAttribute attribute, final SoulWeaponType type) {
        this.amount = amount;
        this.attributeIndex = attribute.index;
        this.weaponIndex = type.index;
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.amount = buffer.readInt();
        this.attributeIndex = buffer.readInt();
        this.weaponIndex = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.amount);
        buffer.writeInt(this.attributeIndex);
        buffer.writeInt(this.weaponIndex);
    }

    public static final class Handler implements IMessageHandler<ClientSpendAttributePoints, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(ClientSpendAttributePoints message, MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final SoulWeaponType weaponType = SoulWeaponType.getType(message.weaponIndex);
            final SoulWeaponAttribute attribute = SoulWeaponAttribute.getAttribute(message.attributeIndex);
            final ISoulWeapon instance = minecraft.player.getCapability(CAPABILITY, null);

            minecraft.addScheduledTask(() -> {
                instance.addAttribute(message.amount, attribute, weaponType);
                instance.addDatum(-message.amount, ATTRIBUTE_POINTS, weaponType);
                instance.addDatum(message.amount, SPENT_ATTRIBUTE_POINTS, weaponType);
                minecraft.displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}
