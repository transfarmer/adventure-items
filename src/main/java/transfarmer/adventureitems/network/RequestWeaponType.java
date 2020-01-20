package transfarmer.adventureitems.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;
import transfarmer.adventureitems.Main;

import java.util.function.Supplier;

import static transfarmer.adventureitems.capability.SoulWeapon.WeaponType;
import static transfarmer.adventureitems.capability.SoulWeaponProvider.CAPABILITY;

public class RequestWeaponType {
    private final WeaponType WEAPON_TYPE;

    public RequestWeaponType(PacketBuffer buffer) {
        this.WEAPON_TYPE = buffer.readEnumValue(WeaponType.class);
    }

    public RequestWeaponType(WeaponType WEAPON_TYPE) {
        this.WEAPON_TYPE = WEAPON_TYPE;
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeEnumValue(WEAPON_TYPE);
    }

    public void handle(Supplier<Context> contextSupplier) {
        final Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity sender = context.getSender();

            if (sender == null) return;

            sender.getCapability(CAPABILITY).ifPresent((transfarmer.adventureitems.capability.ISoulWeapon capability) -> {
                capability.setCurrentTypeIndex(WEAPON_TYPE.getIndex());
                sender.inventory.setInventorySlotContents(sender.inventory.currentItem, new ItemStack(WEAPON_TYPE.getItem()));
                Main.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sender), new ApplyWeaponType(WEAPON_TYPE));
            });
        });
        context.setPacketHandled(true);
    }
}
