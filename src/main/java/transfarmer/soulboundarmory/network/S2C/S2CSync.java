package transfarmer.soulboundarmory.network.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CSync implements IExtendedMessage {
    private String component;
    private CompoundTag tag;

    public S2CSync() {}

    public S2CSync(final IComponentType component, final CompoundTag tag) {
        this.component = component.toString();
        this.tag = tag;
    }

    @Environment(CLIENT)
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.component = buffer.readString();
        this.tag = buffer.readCompoundTag();
    }

    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.component);
        buffer.writeCompoundTag(this.tag);
    }

    public static final class Handler implements IExtendedMessageHandler<S2CSync> {
        @Environment(CLIENT)
        @Override
        public IExtendedMessage onMessage(final S2CSync message, final MessageContext context) {
            final Minecraft minecraft = CLIENT;

            minecraft.addScheduledTask(() -> {
                final ISoulboundComponent component = minecraft.player.getComponent(IComponentType.get(component).getComponent(), null);

                component.fromTag(tag);
                component.sync();
            });

            return null;
        }
    }
}
