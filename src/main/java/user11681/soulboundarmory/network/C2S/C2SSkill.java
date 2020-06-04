package user11681.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.common.ItemComponentPacket;
import user11681.soulboundarmory.registry.Registries;

public class C2SSkill extends ItemComponentPacket {
    public C2SSkill() {
        super(new Identifier(Main.MOD_ID, "server_skill"));
    }

    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        this.storage.upgradeSkill(this.storage.getSkill(Registries.SKILL.get(buffer.readString())));
//        this.component.sync();
    }
}
