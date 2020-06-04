package user11681.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.common.ItemComponentPacket;

public class C2SEnchant extends ItemComponentPacket {
    public C2SEnchant() {
        super(new Identifier(Main.MOD_ID, "server_enchant"));
    }

    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        final Enchantment enchantment = Registry.ENCHANTMENT.get(buffer.readIdentifier());

        storage.addEnchantment(enchantment, buffer.readInt());
//        component.sync();
        storage.refresh();
    }
}
