package transfarmer.soulboundarmory.component.soulbound.item;

import net.minecraft.entity.Entity;

import static transfarmer.soulboundarmory.Main.SOULBOUND_STAFF_ITEM;

public interface IStaffComponent extends ISoulboundItemComponent<IStaffComponent> {
    static IStaffComponent get(Entity entity) {
        return (IStaffComponent) ISoulboundItemComponent.get(entity, SOULBOUND_STAFF_ITEM);
    }

    int getFireballCooldown();

    void setFireballCooldown(int ticks);

    void resetFireballCooldown();

    int getSpell();

    void setSpell(int spell);

    void cycleSpells(int spells);
}
