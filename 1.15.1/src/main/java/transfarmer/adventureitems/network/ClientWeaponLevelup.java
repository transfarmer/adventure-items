package transfarmer.adventureitems.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import transfarmer.adventureitems.capability.ISoulWeapon;

import java.util.function.Supplier;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;
import static transfarmer.adventureitems.capability.SoulWeaponProvider.CAPABILITY;

public class ClientWeaponLevelup {
    public ClientWeaponLevelup(PacketBuffer buffer) {}

    public ClientWeaponLevelup() {}

    public void encode(PacketBuffer buffer) {}

    public void handle(Supplier<Context> contextSupplier) {
        final Context context = contextSupplier.get();
        DistExecutor.runWhenOn(CLIENT, () -> () -> clientHandle(context));
        context.setPacketHandled(true);
    }

    @OnlyIn(CLIENT)
    private void clientHandle(Context context) {
        PlayerEntity player = Minecraft.getInstance().player;
        context.enqueueWork(() -> player.getCapability(CAPABILITY).ifPresent((ISoulWeapon capability) -> {
            capability.addLevel();
            player.addExperienceLevel(-capability.getLevel() - 1);
        }));
    }
}
