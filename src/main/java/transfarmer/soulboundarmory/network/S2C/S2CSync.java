package transfarmer.soulboundarmory.network.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.IExtendedMessage;
import transfarmer.soulboundarmory.network.common.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CSync implements IExtendedMessage {
    private String capability;
    private CompoundTag tag;

    public S2CSync() {}

    public S2CSync(final ICapabilityType capability, final CompoundTag tag) {
        this.capability = capability.toString();
        this.tag = tag;
    }

    @Environment(CLIENT)
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.capability = buffer.readString();
        this.tag = buffer.readCompoundTag();
    }

    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.capability);
        buffer.writeCompoundTag(this.tag);
    }

    public static final class Handler implements IExtendedMessageHandler<S2CSync> {
        @Environment(CLIENT)
        @Override
        public IExtendedMessage onMessage(final S2CSync message, final MessageContext context) {
            final Minecraft minecraft = CLIENT;

            minecraft.addScheduledTask(() -> {
                final ISoulboundComponent capability = minecraft.player.getCapability(ICapabilityType.get(message.capability).getCapability(), null);

                capability.deserializeNBT(message.tag);
                capability.sync();
            });

            return null;
        }
    }
}
