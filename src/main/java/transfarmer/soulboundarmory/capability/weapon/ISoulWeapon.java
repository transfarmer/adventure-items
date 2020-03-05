package transfarmer.soulboundarmory.capability.weapon;

import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.statistics.SoulType;

public interface ISoulWeapon extends ISoulCapability {
    void resetCooldown(SoulType type);

    void decrementCooldown();

    int getCooldown();

    int getCooldown(SoulType type);

    float getAttackRatio(SoulType type);

    void setAttackCooldown(int ticks);

    int getLightningCooldown();

    void resetLightningCooldown();

    void decrementLightningCooldown();
}
