package transfarmer.adventureitems.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import transfarmer.adventureitems.SoulWeapons.WeaponType;
import transfarmer.adventureitems.capability.ISoulWeapon;
import transfarmer.adventureitems.capability.SoulWeaponProvider;

import java.util.function.Supplier;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;
import static net.minecraftforge.fml.network.NetworkEvent.*;

public class ApplyWeaponType {
    private final WeaponType weaponType;
    private boolean changeType;

    public ApplyWeaponType(PacketBuffer buffer) {
        this.weaponType = buffer.readEnumValue(WeaponType.class);
    }

    public ApplyWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    public ApplyWeaponType(WeaponType weaponType, boolean changeType) {
        this.weaponType = weaponType;
        this.changeType = changeType;
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeEnumValue(weaponType);
    }

    public void handle(Supplier<Context> contextSupplier) {
        final Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.runWhenOn(CLIENT, () -> this::clientHandle));
        context.setPacketHandled(true);
    }

    @OnlyIn(CLIENT)
    private void clientHandle() {
        PlayerEntity player = Minecraft.getInstance().player;
        player.getCapability(SoulWeaponProvider.SOUL_WEAPON).ifPresent((ISoulWeapon capability) -> {
            capability.setWeaponType(weaponType);

            if (changeType) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(weaponType.getItem()));
            }
        });
    }
}
