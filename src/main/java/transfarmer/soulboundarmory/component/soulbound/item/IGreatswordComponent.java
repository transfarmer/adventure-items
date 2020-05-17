package transfarmer.soulboundarmory.component.soulbound.item;

import net.minecraft.entity.Entity;

import static transfarmer.soulboundarmory.Main.SOULBOUND_GREATSWORD_ITEM;

public interface IGreatswordComponent extends ISoulboundItemComponent<IGreatswordComponent> {
    static IGreatswordComponent get(Entity entity) {
        return (IGreatswordComponent) ISoulboundItemComponent.get(entity, SOULBOUND_GREATSWORD_ITEM);
    }

    double getLeapForce();

    void setLeapForce(double force);

    void resetLeapForce();

    int getLeapDuration();

    void setLeapDuration(int ticks);

    void freeze(Entity entity, int ticks, double damage);
}
