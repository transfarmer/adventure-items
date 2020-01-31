package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.SoulWeaponAttribute;
import transfarmer.soulweapons.gui.SoulWeaponMenu;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class ClientAddAttribute implements IMessage {
    private int index;

    public ClientAddAttribute() {}

    public ClientAddAttribute(SoulWeaponAttribute attribute) {
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

    public static final class Handler implements IMessageHandler<ClientAddAttribute, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(ClientAddAttribute message, MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                SoulWeaponAttribute attribute = SoulWeaponAttribute.getAttribute(message.index);
                Minecraft.getMinecraft().player.getCapability(CAPABILITY, null)
                    .addAttribute(SoulWeaponAttribute.getAttribute(message.index));
                Minecraft.getMinecraft().displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}
