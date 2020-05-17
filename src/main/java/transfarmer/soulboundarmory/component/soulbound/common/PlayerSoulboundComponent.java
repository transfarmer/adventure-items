package transfarmer.soulboundarmory.component.soulbound.common;

import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PlayerSoulboundComponent implements IPlayerSoulboundComponent {
    protected final List<ISoulboundItemComponent<? extends Component>> components;

    public PlayerSoulboundComponent(final PlayerEntity player) {
        this.components = new ArrayList<>();
    }

    public List<ISoulboundItemComponent<? extends Component>> getComponents() {
        return this.components;
    }

    @Override
    public void fromTag(@Nonnull final CompoundTag tag) {}

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull final CompoundTag tag) {
        return tag;
    }
}
