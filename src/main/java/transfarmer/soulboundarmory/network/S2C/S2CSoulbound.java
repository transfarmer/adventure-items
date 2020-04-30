package transfarmer.soulboundarmory.network.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.IExtendedMessage;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public abstract class S2CSoulbound implements IExtendedMessage {
    protected SoulboundCapability capability;
    protected IItem item;
    protected EntityPlayer player;

    public S2CSoulbound() {}

    public S2CSoulbound(final SoulboundCapability capability, final IItem item) {
        this.capability = capability;
        this.item = item;
    }

    @Override
    @SideOnly(CLIENT)
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.capability = Minecraft.getMinecraft().player.getCapability(ICapabilityType.get(buffer.readString()).getCapability(), null);
        this.item = IItem.get(buffer.readString());
        this.player = this.capability.getPlayer();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.capability.getType().toString());
        buffer.writeString(this.item.toString());
    }
}
