package transfarmer.soulboundarmory.component.config;

import nerdhub.cardinal.components.api.component.ComponentProvider;
import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.minecraft.entity.player.PlayerEntity;

import static transfarmer.soulboundarmory.Main.CONFIG_COMPONENT;

public interface IConfigComponent extends EntitySyncedComponent {
    static IConfigComponent get(final PlayerEntity entity) {
        return CONFIG_COMPONENT.get(ComponentProvider.fromEntity(entity));
    }

    boolean getAddToOffhand();

    void setAddToOffhand(boolean addToOffhand);
}
