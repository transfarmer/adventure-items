package transfarmer.adventureitems.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import transfarmer.adventureitems.Main;
import transfarmer.adventureitems.capability.ISoulWeapon;
import transfarmer.adventureitems.capability.SoulWeapon.WeaponType;
import transfarmer.adventureitems.capability.SoulWeaponProvider;

import java.util.function.Supplier;


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
        Main.LOGGER.info("received");
        Main.LOGGER.info(context.get().getPacketHandled());
        context.get().enqueueWork(() -> {
            Main.LOGGER.info("enqueued");
            ServerPlayerEntity sender = context.get().getSender();
            sender.getCapability(SoulWeaponProvider.WEAPON_TYPE).ifPresent((ISoulWeapon capability) -> {
                Main.LOGGER.info("set");
                capability.setCurrentType(weaponType);
            });
        });
        context.get().setPacketHandled(true);
    }
}
