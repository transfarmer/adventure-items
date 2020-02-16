package transfarmer.soulweapons.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import transfarmer.soulweapons.capability.SoulWeaponHelper;
import transfarmer.soulweapons.capability.SoulWeaponProvider;

import java.util.List;

import static transfarmer.soulweapons.data.SoulWeaponType.SWORD;

public class EntitySoulLightningBolt extends EntityLightningBolt {
    private EntityLivingBase caster;
    private int lightningState;
    private int boltLivingTime;

    public EntitySoulLightningBolt(final World world, final double x, final double y, final double z, final EntityLivingBase caster) {
        super(world, x, y, z, false);

        this.caster = caster;
        this.lightningState = 2;
    }

    public EntitySoulLightningBolt(final World world, final Vec3d pos, final EntityLivingBase caster) {
        this(world, pos.x, pos.y, pos.z, caster);
    }

    @Override
    public void onUpdate() {
        if (!this.world.isRemote) {
            this.setFlag(6, this.isGlowing());
        }

        this.onEntityUpdate();

        if (lightningState == 2) {
            this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 10000, 0.8F + this.rand.nextFloat() * 0.2F);
            this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.WEATHER, 2, 0.5F + this.rand.nextFloat() * 0.2F);
        }

        if (this.lightningState-- < 0) {
            if (this.boltLivingTime == 0) {
                this.setDead();
            } else if (this.lightningState < -this.rand.nextInt(10)) {
                this.boltLivingTime--;
                this.lightningState = 1;

                if (!this.world.isRemote) {
                    this.boltVertex = this.rand.nextLong();
                }
            }
        }

        if (this.lightningState >= 0) {
            if (this.world.isRemote) {
                this.world.setLastLightningBolt(2);
            } else {
                final double radius = 3.0D;
                final List<Entity> nearbyEntities = this.world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(this.posX - radius, this.posY - radius, this.posZ - radius, this.posX + radius, this.posY + 6.0D + radius, this.posZ + radius));

                for (final Entity entity : nearbyEntities) {
                    if (entity instanceof EntityArmorStand || entity instanceof EntityCreeper
                            || entity instanceof EntityHanging || entity instanceof EntityPig
                            || entity instanceof EntityVillager) {
                        entity.onStruckByLightning(this);
                    } else if (entity != this.caster && entity instanceof EntityLivingBase
                            && !ForgeEventFactory.onEntityStruckByLightning(entity, this)) {
                        final float attackDamage = this.caster instanceof EntityPlayer
                                ? 1 + SoulWeaponProvider.get(this.caster).getEffectiveAttackDamage(SWORD) : 5;

                        entity.setFire(1);

                        if (!entity.isBurning()) {
                            this.setFire(8);
                        }

                        if (this.caster instanceof EntityPlayer) {
                            SoulWeaponHelper.delegateAttack(entity, this, (EntityPlayer) this.caster,
                                    this.caster.getHeldItemMainhand(), attackDamage);
                        } else {
                            entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.caster), attackDamage);
                        }
                    }
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setUniqueId("casterUUID", this.caster.getUniqueID());

        return compound;
    }

    @Override
    public void readFromNBT(final NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.caster = this.world.getPlayerEntityByUUID(compound.getUniqueId("casterUUID"));
    }

    public EntityLivingBase getCaster() {
        return this.caster;
    }

    public void setCaster(final EntityLivingBase caster) {
        this.caster = caster;
    }
}
