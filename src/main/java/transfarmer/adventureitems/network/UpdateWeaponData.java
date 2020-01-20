package transfarmer.adventureitems.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import transfarmer.adventureitems.capability.ISoulWeapon;

import java.util.function.Supplier;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;
import static net.minecraftforge.fml.network.NetworkEvent.Context;
import static transfarmer.adventureitems.capability.SoulWeaponProvider.CAPABILITY;

public class UpdateWeaponData {
    private int currentTypeIndex;
    private int[] bigsword, sword, dagger;

    public UpdateWeaponData(PacketBuffer buffer) {
        this.currentTypeIndex = buffer.readInt();
        this.bigsword = buffer.readVarIntArray();
        this.sword = buffer.readVarIntArray();
        this.dagger = buffer.readVarIntArray();
    }

    public UpdateWeaponData(final int currentTypeIndex, final int[] bigsword, final int[] sword, final int[] dagger) {
        this.currentTypeIndex = currentTypeIndex;
        this.bigsword = bigsword;
        this.sword = sword;
        this.dagger = dagger;
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeInt(this.currentTypeIndex);
        buffer.writeVarIntArray(this.bigsword);
        buffer.writeVarIntArray(this.sword);
        buffer.writeVarIntArray(this.dagger);
    }

    public void handle(Supplier<Context> contextSupplier) {
        Context context = contextSupplier.get();
        DistExecutor.runWhenOn(CLIENT, () -> this::clientHandle);
        context.setPacketHandled(true);
    }

    @OnlyIn(CLIENT)
    public void clientHandle() {
        Minecraft.getInstance().player.getCapability(CAPABILITY).ifPresent((ISoulWeapon capability) -> {
            capability.setCurrentTypeIndex(this.currentTypeIndex);
            capability.setAttributes(this.bigsword, this.sword, this.dagger);
        });
    }
}
