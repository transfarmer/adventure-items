package transfarmer.soulboundarmory.component.soulbound.item;

public interface IStaffComponent extends ISoulboundItemComponent<IStaffComponent> {
    int getFireballCooldown();

    void setFireballCooldown(int ticks);

    void resetFireballCooldown();

    int getSpell();

    void setSpell(int spell);

    void cycleSpells(int spells);
}
