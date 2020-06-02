package user11681.soulboundarmory.component.config;

import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.minecraft.entity.Entity;

import static user11681.soulboundarmory.component.Components.CONFIG_COMPONENT;

public interface IConfigComponent extends EntitySyncedComponent {
    static IConfigComponent get(final Entity entity) {
        return CONFIG_COMPONENT.get(entity);
    }

    boolean getAddToOffhand();

    void setAddToOffhand(boolean addToOffhand);

    boolean getLevelupNotifications();

    void setLevelupNotifications(boolean levelupNotifications);
}
