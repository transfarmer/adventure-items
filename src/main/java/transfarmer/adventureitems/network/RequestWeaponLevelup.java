package transfarmer.adventureitems.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;
import transfarmer.adventureitems.Main;
import transfarmer.adventureitems.capability.ISoulWeapon;

import java.util.function.Supplier;

import static transfarmer.adventureitems.capability.SoulWeaponProvider.CAPABILITY;

public class RequestWeaponLevelup {
    public RequestWeaponLevelup(PacketBuffer buffer) {}

    public RequestWeaponLevelup() {}

    public void encode(PacketBuffer buffer) {}

    public void handle(Supplier<Context> contextSupplier) {
        final Context context = contextSupplier.get();
        ServerPlayerEntity sender = context.getSender();

        if (sender == null) return;

        context.enqueueWork(() -> sender.getCapability(CAPABILITY).ifPresent((ISoulWeapon capability) -> {
            if (sender.experienceLevel > capability.getLevel()) {
                capability.addLevel();
                sender.addExperienceLevel(-capability.getLevel() - 1);
                Main.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sender), new ApplyWeaponLevelup());
            }
        }));
        context.setPacketHandled(true);
    }
}
