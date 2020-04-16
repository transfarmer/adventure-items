package transfarmer.soulboundarmory.capability.frozen;

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

public class Frozen implements IFrozen {
    private Entity entity;
    private int freezeTicks;

    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public void setEntity(final Entity entity) {
        this.entity = entity;
    }

    @Override
    public void freeze(final EntityPlayer freezer, final float damage, final int ticks) {
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

    @Override
    public boolean update() {
        if (this.freezeTicks > 0) {
            if (entity.isEntityAlive() && !entity.isBurning()) {
                if (entity.hurtResistantTime > 0) {
                    entity.hurtResistantTime--;
                }

                this.freezeTicks--;

                return true;
            } else {
                this.freezeTicks = 0;
            }
        }

        return false;
    }

    @Override
    public NBTTagCompound writeToNBT() {
        final NBTTagCompound tag = new NBTTagCompound();

        tag.setInteger("soulboundarmory.frozen.freezeTicks", this.freezeTicks);

        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound nbt) {
        this.freezeTicks = nbt.getInteger("soulboundarmory.frozen.freezeTicks");
    }
}
