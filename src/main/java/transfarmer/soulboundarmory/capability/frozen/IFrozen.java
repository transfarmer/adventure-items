package transfarmer.soulboundarmory.capability.frozen;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import transfarmer.soulboundarmory.capability.ICapabilitySerializable;

public interface IFrozen extends ICapabilitySerializable {
    Entity getEntity();

    void setEntity(Entity entity);

    void freeze(EntityPlayer freezer, float damage, int ticks);

    boolean update();
}
