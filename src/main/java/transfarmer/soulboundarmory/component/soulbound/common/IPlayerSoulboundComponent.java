package transfarmer.soulboundarmory.component.soulbound.common;

import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.entity.Entity;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;

import java.util.List;
import java.util.Optional;

import static transfarmer.soulboundarmory.Main.COMPONENTS;

public interface IPlayerSoulboundComponent extends Component {
    static IPlayerSoulboundComponent get(final Entity entity) {
        return COMPONENTS.get(entity);
    }

    static Optional<IPlayerSoulboundComponent> maybeGet(final Entity entity) {
        return COMPONENTS.maybeGet(entity);
    }

    List<ISoulboundItemComponent<? extends Component>> getComponents();

    ISoulboundItemComponent<? extends Component> getHeldItemComponent();

    ISoulboundItemComponent<? extends Component> getAnyHeldItemComponent();

    void tick();
}
