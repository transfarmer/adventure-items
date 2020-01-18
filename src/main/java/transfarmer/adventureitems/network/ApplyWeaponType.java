package transfarmer.adventureitems.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import transfarmer.adventureitems.SoulWeapons.WeaponType;
import transfarmer.adventureitems.capability.ISoulWeapon;
import transfarmer.adventureitems.capability.SoulWeaponProvider;

import java.util.function.Supplier;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;

public class ApplyWeaponType {
    private final WeaponType weaponType;

    public ApplyWeaponType(PacketBuffer buffer) {
        this.weaponType = buffer.readEnumValue(WeaponType.class);
    }

    public ApplyWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeEnumValue(weaponType);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.runWhenOn(CLIENT, () -> this::clientHandle));
        context.setPacketHandled(true);
    }

    @OnlyIn(CLIENT)
    private void clientHandle() {
        PlayerEntity player = Minecraft.getInstance().player;
        player.getCapability(SoulWeaponProvider.WEAPON_TYPE).ifPresent((ISoulWeapon capability) -> {
            capability.setCurrentType(weaponType);
            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(weaponType.getItem()));
        });
    }
}
