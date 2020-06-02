package user11681.soulboundarmory.component.config;

import nerdhub.cardinal.components.api.ComponentType;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.network.Packets;

import javax.annotation.Nonnull;

public class ConfigComponent implements IConfigComponent {
    protected PlayerEntity player;
    protected boolean addToOffhand;
    protected boolean levelupNotifications;

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
    public boolean getLevelupNotifications() {
        return this.levelupNotifications;
    }

    @Override
    public void setLevelupNotifications(final boolean levelupNotifications) {
        this.levelupNotifications = levelupNotifications;
    }

    @Override
    @Nonnull
    public PlayerEntity getEntity() {
        return this.player;
    }

    @Override
    @Nonnull
    public ComponentType<IConfigComponent> getComponentType() {
        return Components.CONFIG_COMPONENT;
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
