package transfarmer.soulboundarmory.component.entity;

import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.minecraft.entity.player.PlayerEntity;

public interface IEntityData extends EntitySyncedComponent {
    void freeze(PlayerEntity freezer, int ticks, float damage);

    boolean isFrozen();

    boolean cannotTeleport();

    void blockTeleport(int ticks);

    void onUpdate();
}
