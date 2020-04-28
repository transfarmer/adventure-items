package transfarmer.soulboundarmory.capability.soulbound.weapon;

import net.minecraft.entity.Entity;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

public interface WeaponCapability extends SoulboundCapability {
    void resetCooldown(IItem type);

    void decrementCooldown();

    int getCooldown();

    int getCooldown(IItem type);

    double getAttackRatio(IItem type);

    void setAttackCooldown(int ticks);

    int getLightningCooldown();

    void setLightningCooldown(int ticks);

    void resetLightningCooldown();

    void decrementLightningCooldown();

    double getLeapForce();

    void setLeapForce(double force);

    void resetLeapForce();

    int getLeapDuration();

    void setLeapDuration(int ticks);

    void freeze(Entity entity, int ticks, double damage);
}
