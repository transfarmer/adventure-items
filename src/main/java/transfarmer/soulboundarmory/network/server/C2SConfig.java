package transfarmer.soulboundarmory.network.server;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.config.PlayerConfigProvider;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;

public class C2SConfig implements IExtendedMessage {
    private boolean addToOffhand;

    public C2SConfig() {
        this.addToOffhand = MainConfig.instance().getAddToOffhand();
    }

    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.addToOffhand = buffer.readBoolean();
    }

    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeBoolean(this.addToOffhand);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SConfig> {
        @Override
        public IExtendedMessage onMessage(final C2SConfig message, final MessageContext context) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    PlayerConfigProvider.get(context.getServerHandler().player).setAddToOffhand(message.addToOffhand);
                }
            });

            return null;
        }
    }
}
