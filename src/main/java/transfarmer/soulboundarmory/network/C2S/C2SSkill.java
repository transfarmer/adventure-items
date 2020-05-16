package transfarmer.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import transfarmer.soulboundarmory.network.common.ComponentPacket;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.statistics.ISkillType;

public class C2SSkill extends ComponentPacket {
    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        super.accept(context, buffer);

        this.component.upgradeSkill(this.item, this.component.getSkill(item, ISkillType.get(buffer.readString()).toString()));
        this.component.sync();
    }
}
