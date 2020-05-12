package transfarmer.soulboundarmory.network.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.IExtendedMessage;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public abstract class S2CSoulbound implements IExtendedMessage {
    protected ISoulboundComponent capability;
    protected IItem item;
    protected PlayerEntity player;

    public S2CSoulbound() {}

    public S2CSoulbound(final ISoulboundComponent capability, final IItem item) {
        this.capability = capability;
        this.item = item;
    }

    @Override
    @Environment(CLIENT)
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.capability = CLIENT.player.getCapability(ICapabilityType.get(buffer.readString()).getCapability(), null);
        this.item = IItem.get(buffer.readString());
        this.player = this.capability.getPlayer();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.capability.getType().toString());
        buffer.writeString(this.item.toString());
    }
}
