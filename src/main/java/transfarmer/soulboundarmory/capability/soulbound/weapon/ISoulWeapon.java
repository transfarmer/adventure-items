package transfarmer.soulboundarmory.capability.soulbound.weapon;

import transfarmer.soulboundarmory.capability.soulbound.ISoulCapability;
import transfarmer.soulboundarmory.statistics.SoulType;

public interface ISoulWeapon extends ISoulCapability {
    void resetCooldown(SoulType type);

    void decrementCooldown();

    int getCooldown();

    int getCooldown(SoulType type);

    float getAttackRatio(SoulType type);

    void setAttackCooldown(int ticks);

    int getLightningCooldown();

    void setLightningCooldown(int ticks);

    void resetLightningCooldown();

    void decrementLightningCooldown();

    float getCharging();

    void setCharging(float charging);
}
