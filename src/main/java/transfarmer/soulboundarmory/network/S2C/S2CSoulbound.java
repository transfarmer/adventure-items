package transfarmer.soulboundarmory.network.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.statistics.IItem;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public abstract class S2CSoulbound implements IExtendedMessage {
    protected ISoulboundComponent component;
    protected IItem item;
    protected PlayerEntity player;

    public S2CSoulbound() {}

    public S2CSoulbound(final ISoulboundComponent component, final IItem item) {
        this.component = component;
        this.item = item;
    }

    @Override
    @Environment(CLIENT)
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.component = CLIENT.player.getComponent(IComponentType.get(buffer.readString()).getComponent(), null);
        this.item = IItem.get(buffer.readString());
        this.player = this.component.getPlayer();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.component.getType().toString());
        buffer.writeString(this.item.toString());
    }
}
