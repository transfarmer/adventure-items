package transfarmer.soulboundarmory.capability.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;

public class EntityData implements IEntityData {
    private Entity entity;
    private int freezeTicks;
    private int blockTeleportTicks;

    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public void setEntity(final Entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean cannotTeleport() {
        return this.blockTeleportTicks > 0;
    }

    @Override
    public void blockTeleport(final int ticks) {
        this.blockTeleportTicks = ticks;
    }

    @Override
    public void freeze(final EntityPlayer freezer, final int ticks, final float damage) {
        if (!freezer.world.isRemote) {
            ((WorldServer) freezer.world).spawnParticle(
                    EnumParticleTypes.SNOWBALL,
                    this.entity.posX, this.entity.posY + this.entity.getEyeHeight(), this.entity.posZ, 32, 0.1, 0, 0.1, 2D);
            this.entity.playSound(SoundEvents.BLOCK_SNOW_HIT, 1, 1.2F / (this.entity.world.rand.nextFloat() * 0.2F + 0.9F));
            this.entity.extinguish();

            if (ticks > this.freezeTicks) {
                this.freezeTicks = ticks;
            }

            if (this.entity instanceof EntityLivingBase) {
                this.entity.attackEntityFrom(DamageSource.causePlayerDamage(freezer).setDamageIsAbsolute(), damage);
            }

            if (this.entity instanceof EntityCreeper) {
                ((EntityCreeper) this.entity).setCreeperState(-1);
            }

            if (this.entity instanceof IProjectile) {
                this.entity.motionX = this.entity.motionZ = 0;
            }
        }
    }

    @Override
    public boolean isFrozen() {
        return this.freezeTicks > 0 && this.entity.isEntityAlive() && !this.entity.isBurning();
    }

    @Override
    public void onUpdate() {
        if (!this.entity.world.isRemote) {
            if (this.isFrozen()) {
                if (this.entity.hurtResistantTime > 0) {
                    this.entity.hurtResistantTime--;
                }

                this.freezeTicks--;
            } else {
                this.freezeTicks = 0;
            }

            if (this.cannotTeleport()) {
                this.blockTeleportTicks--;
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound tag = new NBTTagCompound();

        tag.setInteger("freezeTicks", this.freezeTicks);
        tag.setInteger("blockTeleportTicks", this.blockTeleportTicks);

        return tag;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound tag) {
        this.freezeTicks = tag.getInteger("freezeTicks");
        this.blockTeleportTicks = tag.getInteger("blockTeleportTicks");
    }
}
