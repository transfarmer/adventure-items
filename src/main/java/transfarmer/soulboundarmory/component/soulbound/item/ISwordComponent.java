package transfarmer.soulboundarmory.component.soulbound.item;

import net.minecraft.entity.Entity;

import static transfarmer.soulboundarmory.Main.SOULBOUND_SWORD_ITEM;

public interface ISwordComponent extends ISoulboundItemComponent<ISwordComponent> {
    static ISwordComponent get(Entity entity) {
        return (ISwordComponent) ISoulboundItemComponent.get(entity, SOULBOUND_SWORD_ITEM);
    }

    int getLightningCooldown();

    void setLightningCooldown(int ticks);

    void resetLightningCooldown();
}
