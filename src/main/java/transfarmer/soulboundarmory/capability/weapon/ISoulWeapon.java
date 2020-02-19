package transfarmer.soulboundarmory.capability.weapon;

import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.data.IType;

public interface ISoulWeapon extends ISoulCapability {
    float getAttackSpeed(IType type);

    float getEffectiveAttackSpeed(IType type);

    float getAttackDamage(IType type);

    float getEffectiveAttackDamage(IType type);

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
