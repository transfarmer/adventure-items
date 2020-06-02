package user11681.soulboundarmory.component.soulbound.player;

import java.util.Map;
import javax.annotation.Nonnull;
import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.item.tool.PickStorage;
import user11681.soulboundarmory.item.ModItems;

public class ToolSoulboundComponent extends SoulboundComponentBase {
    public ToolSoulboundComponent(final PlayerEntity player) {
        super(player);

        this.store(new PickStorage(this, ModItems.SOULBOUND_PICK));
    }

    @Override
    public Map<StorageType<?>, ItemStorage<?>> getStorages() {
        return null;
    }

    @Override
    public PickStorage getHeldItemStorage() {
        return null;
    }

    @Override
    public PickStorage getAnyHeldItemStorage() {
        return null;
    }

    @Nonnull
    @Override
    public ComponentType<SoulboundComponent> getComponentType() {
        return Components.WEAPON_COMPONENT;
    }
}
