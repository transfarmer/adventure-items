package transfarmer.soulboundarmory.network.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.ItemComponentPacket;

import static transfarmer.soulboundarmory.MainClient.CLIENT;

public class S2CEnchant extends ItemComponentPacket {
    @Override
    @Environment(EnvType.CLIENT)
    protected void accept(final PacketContext context, final ExtendedPacketBuffer buffer) {
        final Enchantment enchantment = Registry.ENCHANTMENT.get(buffer.readIdentifier());

        CLIENT.execute(() -> {
            component.addEnchantment(enchantment, buffer.readInt());
            component.refresh();
        });
    }
}
