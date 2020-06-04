package user11681.soulboundarmory.component.soulbound.player;

import javax.annotation.Nonnull;
import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.item.tool.PickStorage;
import user11681.soulboundarmory.registry.ModItems;

public class ToolSoulboundComponent extends SoulboundComponentBase {
    public ToolSoulboundComponent(final PlayerEntity player) {
        super(player);

        this.store(new PickStorage(this, ModItems.SOULBOUND_PICK));
    }

    @Nonnull
    @Override
    public ComponentType<SoulboundComponentBase> getComponentType() {
        return Components.WEAPON_COMPONENT;
    }
}
