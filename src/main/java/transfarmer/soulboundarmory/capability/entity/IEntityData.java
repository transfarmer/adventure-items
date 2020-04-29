package transfarmer.soulboundarmory.capability.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IEntityData extends INBTSerializable<NBTTagCompound> {
    Entity getEntity();

    void setEntity(Entity entity);

    void freeze(EntityPlayer freezer, int ticks, float damage);

    boolean isFrozen();

    boolean cannotTeleport();

    void blockTeleport(int ticks);

    void onUpdate();
}
