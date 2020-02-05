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
    int tab;

    public ClientTab() {}

    public ClientTab(final int tab) {
        this.tab = tab;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.tab = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.tab);
    }

    public static final class Handler implements IMessageHandler<ClientTab, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(ClientTab message, MessageContext context) {
            Minecraft.getMinecraft().player.getCapability(CAPABILITY, null).setCurrentTab(message.tab);

            return null;
        }
    }
}
