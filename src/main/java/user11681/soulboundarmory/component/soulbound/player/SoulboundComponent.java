package user11681.soulboundarmory.component.soulbound.player;

import java.util.Map;
import java.util.Optional;
import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;

import static user11681.soulboundarmory.component.Components.WEAPON_COMPONENT;

public interface SoulboundComponent extends EntitySyncedComponent {
    static SoulboundComponent get(final Entity entity) {
        return WEAPON_COMPONENT.get(entity);
    }

    static Optional<SoulboundComponent> maybeGet(final Entity entity) {
        return WEAPON_COMPONENT.maybeGet(entity);
    }

    PlayerEntity getEntity();

    ItemStorage<?> getStorage();

    <T extends ItemStorage<T>> T getStorage(final StorageType<T> type);

    Map<StorageType<?>, ItemStorage<?>> getStorages();

    ItemStorage<?> getHeldItemStorage();

    ItemStorage<?> getAnyHeldItemStorage();

    void tick();
}
