package transfarmer.soulboundarmory.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.config.PlayerConfigProvider;
import transfarmer.soulboundarmory.config.MainConfig;

public class C2SConfig implements IMessage {
    private boolean addToOffhand;

    public C2SConfig() {
        this.addToOffhand = MainConfig.instance().getAddToOffhand();
    }

    public void fromBytes(final ByteBuf buffer) {
        this.addToOffhand = buffer.readBoolean();
    }

    public void toBytes(final ByteBuf buffer) {
        buffer.writeBoolean(this.addToOffhand);
    }

    public static final class Handler implements IMessageHandler<C2SConfig, IMessage> {
        @Override
        public IMessage onMessage(final C2SConfig message, final MessageContext context) {
            final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

            server.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    PlayerConfigProvider.get(context.getServerHandler().player).setAddToOffhand(message.addToOffhand);
                }
            });

            return null;
        }
    }
}
