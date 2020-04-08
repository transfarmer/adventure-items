package transfarmer.soulboundarmory.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CSync implements IMessage {
    private String capabilityType;
    private NBTTagCompound tag;

    public S2CSync() {}

    public S2CSync(final String capabilityType, final NBTTagCompound tag) {
        this.capabilityType = capabilityType;
        this.tag = tag;
    }

    @SideOnly(CLIENT)
    public void fromBytes(final ByteBuf buffer) {
        this.capabilityType = ByteBufUtils.readUTF8String(buffer);
        this.tag = ByteBufUtils.readTag(buffer);
    }

    public void toBytes(final ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, this.capabilityType);
        ByteBufUtils.writeTag(buffer, this.tag);
    }

    public static final class Handler implements IMessageHandler<S2CSync, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final S2CSync message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();

            minecraft.addScheduledTask(() -> {
                switch (message.capabilityType) {
                    case "tool":
                        SoulToolProvider.get(minecraft.player).readNBT(message.tag);
                        break;
                    case "weapon":
                        SoulWeaponProvider.get(minecraft.player).readNBT(message.tag);
                }
            });

            return null;
        }
    }
}
