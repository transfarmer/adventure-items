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

public class ClientSpendAttributePoint implements IMessage {
    private int attributeIndex;
    private int weaponIndex;

    public ClientSpendAttributePoint() {}

    public ClientSpendAttributePoint(final SoulWeaponAttribute attribute, final SoulWeaponType type) {
        this.attributeIndex = attribute.index;
        this.weaponIndex = type.index;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.attributeIndex = buffer.readInt();
        this.weaponIndex = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.attributeIndex);
        buffer.writeInt(this.weaponIndex);
    }

    public static final class Handler implements IMessageHandler<ClientSpendAttributePoint, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(ClientSpendAttributePoint message, MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final SoulWeaponType weaponType = SoulWeaponType.getType(message.weaponIndex);
            final SoulWeaponAttribute attribute = SoulWeaponAttribute.getAttribute(message.attributeIndex);
            final ISoulWeapon instance = minecraft.player.getCapability(CAPABILITY, null);

            minecraft.addScheduledTask(() -> {
                instance.addAttribute(attribute, weaponType);
                instance.addDatum(-1, ATTRIBUTE_POINTS, weaponType);
                instance.addDatum(1, SPENT_ATTRIBUTE_POINTS, weaponType);
                minecraft.displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}