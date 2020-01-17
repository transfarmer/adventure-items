package transfarmer.adventureitems.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import transfarmer.adventureitems.capability.ISoulWeapon;
import transfarmer.adventureitems.SoulWeapons.WeaponType;
import transfarmer.adventureitems.capability.SoulWeaponProvider;

import java.util.function.Supplier;

import static net.minecraftforge.api.distmarker.Dist.DEDICATED_SERVER;


public class Packet {
    private final WeaponType weaponType;

    public Packet(PacketBuffer buffer) {
        this.weaponType = buffer.readEnumValue(WeaponType.class);
    }

    public Packet(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeEnumValue(weaponType);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        // Main.LOGGER.info("received");
        // Main.LOGGER.info(context.get().getPacketHandled());
        context.get().enqueueWork(() -> {
            if (FMLEnvironment.dist == DEDICATED_SERVER) {
                // Main.LOGGER.info("enqueued");
                ServerPlayerEntity player = context.get().getSender();
                player.getCapability(SoulWeaponProvider.WEAPON_TYPE).ifPresent((ISoulWeapon capability) -> {
                    // Main.LOGGER.info("set");
                    capability.setCurrentType(weaponType);
                });
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new Packet(weaponType));
            } else {
                PlayerEntity player = Minecraft.getInstance().player;
                player.getCapability(SoulWeaponProvider.WEAPON_TYPE).ifPresent((ISoulWeapon capability) -> {
                    capability.setCurrentType(weaponType);
                });
            }
        });
        context.get().setPacketHandled(true);
    }
}
