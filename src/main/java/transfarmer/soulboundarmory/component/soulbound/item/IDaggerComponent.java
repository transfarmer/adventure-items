package transfarmer.soulboundarmory.component.soulbound.item;

import net.minecraft.entity.player.PlayerEntity;

import static transfarmer.soulboundarmory.Main.SOULBOUND_DAGGER_ITEM;

public interface IDaggerComponent extends ISoulboundItemComponent<IDaggerComponent> {
    static IDaggerComponent get(PlayerEntity player) {
        return (IDaggerComponent) ISoulboundItemComponent.get(player, SOULBOUND_DAGGER_ITEM);
    }
}
