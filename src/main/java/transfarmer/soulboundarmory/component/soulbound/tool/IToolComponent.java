package transfarmer.soulboundarmory.component.soulbound.tool;

import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.minecraft.entity.Entity;

import static transfarmer.soulboundarmory.Main.TOOLS;

public interface IToolComponent extends ISoulboundItemComponent {
    static IToolComponent get(final Entity player) {
        return TOOLS.get(ComponentProvider.fromEntity(player));
    }
}
