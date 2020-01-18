package transfarmer.adventureitems.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;
import transfarmer.adventureitems.Main;
import transfarmer.adventureitems.capability.ISoulWeapon;
import transfarmer.adventureitems.SoulWeapons.WeaponType;
import transfarmer.adventureitems.capability.SoulWeaponProvider;

import java.util.function.Supplier;

public class RequestWeaponType {
    private final WeaponType weaponType;

    public RequestWeaponType(PacketBuffer buffer) {
        this.weaponType = buffer.readEnumValue(WeaponType.class);
    }

    public RequestWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeEnumValue(weaponType);
    }

    public void handle(Supplier<Context> contextSupplier) {
        final Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity sender = context.getSender();

            if (sender == null) return;

            sender.getCapability(SoulWeaponProvider.WEAPON_TYPE).ifPresent((ISoulWeapon capability) -> {
                capability.setCurrentType(weaponType);
                sender.inventory.setInventorySlotContents(sender.inventory.currentItem, new ItemStack(weaponType.getItem()));
            });
            Main.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sender), new ApplyWeaponType(weaponType));
        });
        context.setPacketHandled(true);
    }
}
