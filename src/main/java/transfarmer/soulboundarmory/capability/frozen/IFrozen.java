package transfarmer.soulboundarmory.capability.frozen;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public interface IFrozen {
    Entity getEntity();

    void setEntity(Entity entity);

    void freeze(EntityPlayer freezer, float damage, int ticks);

    boolean update();
}
