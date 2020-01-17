package transfarmer.adventureitems.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;
import transfarmer.adventureitems.capability.ISoulWeapon;
import transfarmer.adventureitems.SoulWeapons.WeaponType;
import transfarmer.adventureitems.capability.SoulWeaponProvider;

import java.util.function.Supplier;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;
import static net.minecraftforge.api.distmarker.Dist.DEDICATED_SERVER;


public class WeaponTypePacket {
    private final WeaponType weaponType;

    public WeaponTypePacket(PacketBuffer buffer) {
        this.weaponType = buffer.readEnumValue(WeaponType.class);
    }

    public WeaponTypePacket(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeEnumValue(weaponType);
    }

    public void handle(Supplier<Context> contextSupplier) {
        Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            DistExecutor.runWhenOn(DEDICATED_SERVER, () -> () -> {
                ServerPlayerEntity sender = context.getSender();

                if (sender == null) return;

                commonHandle(sender);
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), new WeaponTypePacket(weaponType));
            });
            // DistExecutor.runWhenOn(CLIENT, () -> () -> {
            //     PlayerEntity player = Minecraft.getInstance().player;
            //     commonHandle(player);
            // });
        });
        context.setPacketHandled(true);
    }

    private <T extends PlayerEntity> void commonHandle(T player) {
        player.getCapability(SoulWeaponProvider.WEAPON_TYPE).ifPresent((ISoulWeapon capability) -> {
            capability.setCurrentType(weaponType);
            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(weaponType.getItem()));
        });
    }
}
