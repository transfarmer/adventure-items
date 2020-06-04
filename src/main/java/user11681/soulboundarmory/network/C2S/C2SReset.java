package user11681.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.common.ItemComponentPacket;
import user11681.soulboundarmory.component.statistics.Category;
import user11681.soulboundarmory.registry.Registries;

public class C2SReset extends ItemComponentPacket {
    public C2SReset() {
        super(new Identifier(Main.MOD_ID, "server_reset"));
    }

    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        final Identifier identifier = buffer.readIdentifier();

        if (identifier != null) {
            final Category category = Registries.CATEGORY.get(identifier);

            storage.reset(category);
        } else {
            storage.reset();
        }

//        component.sync();
        storage.refresh();
    }
}
