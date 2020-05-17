package transfarmer.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.ItemComponentPacket;
import transfarmer.soulboundarmory.skill.Skills;

public class C2SSkill extends ItemComponentPacket {
    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        this.component.upgradeSkill(this.component.getSkill(Skills.get(buffer.readString())));
//        this.component.sync();
    }
}
