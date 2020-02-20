package transfarmer.soulboundarmory.capability.weapon;

import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.statistics.IType;

public interface ISoulWeapon extends ISoulCapability {
    void resetCooldown(IType type);

    void addCooldown(int ticks);

    int getAttackCooldown();

    int getCooldown(IType type);

    float getAttackRatio(IType type);

    void setAttackCooldown(int ticks);

    int getLightningCooldown();

    void resetLightningCooldown();

    void decrementLightningCooldown();
}
