package transfarmer.adventureitems.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import transfarmer.adventureitems.capability.SoulWeapon.WeaponType;

import java.util.function.Supplier;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;
import static net.minecraftforge.fml.network.NetworkEvent.Context;
import static transfarmer.adventureitems.capability.SoulWeaponProvider.CAPABILITY;

public class ClientWeaponType {
    private final WeaponType weaponType;

    public ClientWeaponType(PacketBuffer buffer) {
        this.weaponType = buffer.readEnumValue(WeaponType.class);
    }

    public ClientWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeEnumValue(this.weaponType);
    }

    public void handle(Supplier<Context> contextSupplier) {
        final Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.runWhenOn(CLIENT, () -> this::clientHandle));
        context.setPacketHandled(true);
    }

    @OnlyIn(CLIENT)
    private void clientHandle() {
        PlayerEntity player = Minecraft.getInstance().player;
        player.getCapability(CAPABILITY).ifPresent((transfarmer.adventureitems.capability.ISoulWeapon capability) -> {
            capability.setCurrentTypeIndex(weaponType.getIndex());
            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(weaponType.getItem()));
        });
    }
}
