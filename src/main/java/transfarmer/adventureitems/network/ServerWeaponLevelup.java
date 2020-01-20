package transfarmer.adventureitems.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;
import transfarmer.adventureitems.Main;
import transfarmer.adventureitems.capability.ISoulWeapon;

import java.util.function.Supplier;

import static transfarmer.adventureitems.capability.SoulWeaponProvider.CAPABILITY;

public class ServerWeaponLevelup {
    public ServerWeaponLevelup(PacketBuffer buffer) {}

    public ServerWeaponLevelup() {}

    public void encode(PacketBuffer buffer) {}

    public void handle(Supplier<Context> contextSupplier) {
        final Context context = contextSupplier.get();
        ServerPlayerEntity sender = context.getSender();

        if (sender == null) return;

        context.enqueueWork(() -> sender.getCapability(CAPABILITY).ifPresent((ISoulWeapon capability) -> {
            if (sender.experienceLevel > capability.getLevel()) {
                capability.addLevel();
                sender.addExperienceLevel(-capability.getLevel());
                Main.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sender), new ClientWeaponLevelup());
            }
        }));
        context.setPacketHandled(true);
    }
}
