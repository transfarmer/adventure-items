package transfarmer.soulboundarmory.component.soulbound.item;

import net.minecraft.entity.Entity;

public interface IGreatswordComponent {
    double getLeapForce();

    void setLeapForce(double force);

    void resetLeapForce();

    int getLeapDuration();

    void setLeapDuration(int ticks);

    void freeze(Entity entity, int ticks, double damage);
}
