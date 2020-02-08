package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.data.SoulWeaponAttribute;
import transfarmer.soulweapons.gui.SoulWeaponMenu;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SPENT_ATTRIBUTE_POINTS;

public class ClientSpendAttributePoint implements IMessage {
    private int index;

    public ClientSpendAttributePoint() {}

    public ClientSpendAttributePoint(SoulWeaponAttribute attribute) {
        this.index = attribute.index;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.index = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.index);
    }

    public static final class Handler implements IMessageHandler<ClientSpendAttributePoint, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(ClientSpendAttributePoint message, MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final ISoulWeapon instance = minecraft.player.getCapability(CAPABILITY, null);

            minecraft.addScheduledTask(() -> {
                instance.addAttribute(SoulWeaponAttribute.getAttribute(message.index), instance.getCurrentType());
                instance.addDatum(1, SPENT_ATTRIBUTE_POINTS, instance.getCurrentType());
                minecraft.displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}
