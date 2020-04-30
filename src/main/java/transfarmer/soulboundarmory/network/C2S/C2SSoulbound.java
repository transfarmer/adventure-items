package transfarmer.soulboundarmory.network.C2S;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.IExtendedMessage;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public abstract class C2SSoulbound implements IExtendedMessage {
    protected Capability<? extends SoulboundCapability> capability;

    public C2SSoulbound() {}

    public C2SSoulbound(final ICapabilityType type) {
        this.capability = type.getCapability();
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.capability = ICapabilityType.get(buffer.readString()).getCapability();
    }

    @SideOnly(CLIENT)
    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(Minecraft.getMinecraft().player.getCapability(this.capability, null).getType().toString());
    }
}
