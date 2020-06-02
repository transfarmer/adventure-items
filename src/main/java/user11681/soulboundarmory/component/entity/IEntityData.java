package user11681.soulboundarmory.component.entity;

import java.util.function.Consumer;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

import static user11681.soulboundarmory.component.Components.ENTITY_DATA;

public interface IEntityData extends EntitySyncedComponent {
    static IEntityData get(final Entity entity) {
        return ENTITY_DATA.get(ComponentProvider.fromEntity(entity));
    }

    static Optional<IEntityData> maybeGet(final Entity entity) {
        return ENTITY_DATA.maybeGet(ComponentProvider.fromEntity(entity));
    }

    static void ifPresent(final Entity entity, final Consumer<IEntityData> consumer) {
        maybeGet(entity).ifPresent(consumer);
    }

    void freeze(PlayerEntity freezer, int ticks, float damage);

    boolean isFrozen();

    boolean cannotTeleport();

    void blockTeleport(int ticks);

    void tick();
}
