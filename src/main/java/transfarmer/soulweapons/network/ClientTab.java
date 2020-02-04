package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class ClientTab implements IMessage {
    private int tab;

    public ClientTab() {}

    public ClientTab(final int tab) {
        this.tab = tab;
    }

    public void fromBytes(ByteBuf buffer) {
        this.tab = buffer.readInt();
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.tab);
    }

    public static final class Handler implements IMessageHandler<ClientTab, IMessage> {
        @SideOnly(CLIENT)
        public IMessage onMessage(ClientTab message, MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();

            minecraft.addScheduledTask(() -> minecraft.player.getCapability(CAPABILITY, null).setCurrentTab(message.tab));

            return null;
        }
    }
}
