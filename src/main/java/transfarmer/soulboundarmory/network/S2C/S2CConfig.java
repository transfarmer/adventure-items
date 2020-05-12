package transfarmer.soulboundarmory.network.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.IExtendedMessage;
import transfarmer.soulboundarmory.network.common.IExtendedMessageHandler;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CConfig implements IExtendedMessage {
    private CompoundTag config;

    public S2CConfig() {
        this.config = MainConfig.instance().writeToNBT();
    }

    @Environment(CLIENT)
    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.config = buffer.readCompoundTag();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeCompoundTag(this.config);
    }

    public static final class Handler implements IExtendedMessageHandler<S2CConfig> {
        @Environment(CLIENT)
        @Override
        public IExtendedMessage onMessage(final S2CConfig message, final MessageContext context) {
            CLIENT.addScheduledTask(() -> MainConfig.instance().readFromNBT(message.config));

            return null;
        }
    }
}
