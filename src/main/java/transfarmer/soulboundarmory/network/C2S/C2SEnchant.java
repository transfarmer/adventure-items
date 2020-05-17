package transfarmer.soulboundarmory.network.C2S;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.ItemComponentPacket;

public class C2SEnchant extends ItemComponentPacket {
    @Override
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        final Enchantment enchantment = Registry.ENCHANTMENT.get(buffer.readIdentifier());

        component.addEnchantment(enchantment, buffer.readInt());
//        component.sync();
        component.refresh();
    }
}
