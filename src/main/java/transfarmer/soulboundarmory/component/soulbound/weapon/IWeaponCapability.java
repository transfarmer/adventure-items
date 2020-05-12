package transfarmer.soulboundarmory.component.soulbound.weapon;

import net.minecraft.entity.Entity;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

public interface IWeaponCapability extends ISoulboundComponent {
    void resetCooldown(IItem type);

    int getAttackCooldown();

    int getAttackCooldown(IItem type);

    double getAttackRatio(IItem type);

    void setAttackCooldown(int ticks);

    int getLightningCooldown();

    void setLightningCooldown(int ticks);

    void resetLightningCooldown();

    double getLeapForce();

    void setLeapForce(double force);

    void resetLeapForce();

    int getLeapDuration();

    void setLeapDuration(int ticks);

    int getFireballCooldown();

    void setFireballCooldown(int ticks);

    void resetFireballCooldown();

    int getSpell();

    void setSpell(int spell);

    void cycleSpells(int spells);

    void freeze(Entity entity, int ticks, double damage);
}
