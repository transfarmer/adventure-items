package transfarmer.adventureitems.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import transfarmer.adventureitems.Main;
import transfarmer.adventureitems.capability.ISoulWeapon;

import java.util.function.Supplier;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;
import static net.minecraftforge.fml.network.NetworkEvent.Context;
import static transfarmer.adventureitems.capability.SoulWeaponProvider.CAPABILITY;

public class ClientWeaponData {
    private int currentTypeIndex;
    private int[] bigsword, sword, dagger;

    public ClientWeaponData(PacketBuffer buffer) {
        this.currentTypeIndex = buffer.readInt();
        this.bigsword = buffer.readVarIntArray(8);
        this.sword = buffer.readVarIntArray(8);
        this.dagger = buffer.readVarIntArray(8);
    }

    public ClientWeaponData(final int currentTypeIndex, final int[] bigsword, final int[] sword, final int[] dagger) {
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
        Minecraft.getInstance().player.getCapability(CAPABILITY).ifPresent((ISoulWeapon instance) -> {
            Main.LOGGER.info("capability is present");
            instance.setCurrentTypeIndex(this.currentTypeIndex);

            if (this.bigsword.length == 0 || this.sword.length == 0 || this.dagger.length == 0) {
                instance.setAttributes(new int[8], new int[8], new int[8]);
                return;
            }

            instance.setAttributes(this.bigsword, this.sword, this.dagger);
        });
        Minecraft.getInstance().player.getCapability(CAPABILITY).orElseThrow(() -> new IllegalStateException("capability does not exist"));
    }
}
