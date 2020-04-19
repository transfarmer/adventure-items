package transfarmer.soulboundarmory.capability.frozen;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IFrozen extends INBTSerializable<NBTTagCompound> {
    Entity getEntity();

    void setEntity(Entity entity);

    void freeze(EntityPlayer freezer, int ticks, float damage);

    boolean update();
}
