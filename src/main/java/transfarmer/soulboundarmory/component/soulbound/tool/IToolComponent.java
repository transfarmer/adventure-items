package transfarmer.soulboundarmory.component.soulbound.tool;

import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.minecraft.entity.Entity;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;

import static transfarmer.soulboundarmory.Main.TOOLS;

public interface IToolComponent extends ISoulboundComponent {
    static IToolComponent get(final Entity player) {
        return TOOLS.get(ComponentProvider.fromEntity(player));
    }
}
