package transfarmer.soulboundarmory.network.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.IExtendedMessage;
import transfarmer.soulboundarmory.network.common.IExtendedMessageHandler;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CConfig implements IExtendedMessage {
    private NBTTagCompound config;

    public S2CConfig() {
        this.config = MainConfig.instance().writeToNBT();
    }

    @SideOnly(CLIENT)
    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.config = buffer.readCompoundTag();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeCompoundTag(this.config);
    }

    public static final class Handler implements IExtendedMessageHandler<S2CConfig> {
        @SideOnly(CLIENT)
        @Override
        public IExtendedMessage onMessage(final S2CConfig message, final MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> MainConfig.instance().readFromNBT(message.config));

            return null;
        }
    }
}
