package transfarmer.soulboundarmory.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.config.MainConfig;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CConfig implements IMessage {
    private NBTTagCompound config;

    public S2CConfig() {
        this.config = MainConfig.instance().writeToNBT();
    }

    @SideOnly(CLIENT)
    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.config = ByteBufUtils.readTag(buffer);
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        ByteBufUtils.writeTag(buffer, this.config);
    }

    public static final class Handler implements IMessageHandler<S2CConfig, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final S2CConfig message, final MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> MainConfig.instance().readFromNBT(message.config));

            return null;
        }
    }
}
