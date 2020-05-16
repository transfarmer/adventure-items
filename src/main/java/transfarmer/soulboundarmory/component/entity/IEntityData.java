package transfarmer.soulboundarmory.component.entity;

import nerdhub.cardinal.components.api.component.ComponentProvider;
import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

import static transfarmer.soulboundarmory.Main.ENTITY_DATA;

public interface IEntityData extends EntitySyncedComponent {
    static IEntityData get(final Entity entity) {
        return ENTITY_DATA.get(ComponentProvider.fromEntity(entity));
    }

    static Optional<IEntityData> maybeGet(final Entity entity) {
        return ENTITY_DATA.maybeGet(ComponentProvider.fromEntity(entity));
    }

    void freeze(PlayerEntity freezer, int ticks, float damage);

    boolean isFrozen();

    boolean cannotTeleport();

    void blockTeleport(int ticks);

    void onUpdate();
}
