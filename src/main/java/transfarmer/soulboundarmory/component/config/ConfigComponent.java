package transfarmer.soulboundarmory.component.config;

import nerdhub.cardinal.components.api.ComponentType;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.network.Packets;

import javax.annotation.Nonnull;

public class ConfigComponent implements IConfigComponent {
    protected PlayerEntity player;
    private boolean addToOffhand;

    public ConfigComponent(final PlayerEntity player) {
        this.player = player;

        if (this.player.world.isClient) {
            ClientSidePacketRegistry.INSTANCE.sendToServer(Packets.C2S_CONFIG, null);
        }
    }

    @Override
    public boolean getAddToOffhand() {
        return this.addToOffhand;
    }

    @Override
    public void setAddToOffhand(final boolean addToOffhand) {
        this.addToOffhand = addToOffhand;
    }

    @Override
    @Nonnull
    public PlayerEntity getEntity() {
        return this.player;
    }

    @Override
    @Nonnull
    public ComponentType<?> getComponentType() {
        return Main.CONFIG;
    }

    @Override
    public void fromTag(@Nonnull final CompoundTag compoundTag) {

    }

    @Override
    @Nonnull
    public CompoundTag toTag(@Nonnull final CompoundTag compoundTag) {
        return compoundTag;
    }
}
