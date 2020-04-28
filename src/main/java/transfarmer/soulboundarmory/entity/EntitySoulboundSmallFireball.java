package transfarmer.soulboundarmory.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class EntitySoulboundSmallFireball extends EntitySmallFireball {
    public EntitySoulboundSmallFireball(final World world, final EntityPlayer shooter) {
        super(world);

        this.shootingEntity = shooter;
        this.setLocationAndAngles(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;

        final Vec3d look = shooter.getLookVec();

        this.accelerationX = 0.2 * look.x;
        this.accelerationY = 0.2 * look.y;
        this.accelerationZ = 0.2 * look.z;
    }

    @Override
    protected void onImpact(@NotNull final RayTraceResult result) {
        if (!this.world.isRemote) {
            final Entity entity = result.entityHit;

            if (entity != null) {
                if (!entity.isImmuneToFire()) {
                    entity.setFire(1);

                    if (entity.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0F)) {
                        this.applyEnchantments(this.shootingEntity, entity);
                        entity.setFire(5);
                    } else {
                        entity.extinguish();
                    }
                }
            } else {
                boolean flag1 = true;

                if (this.shootingEntity != null && this.shootingEntity instanceof EntityLiving) {
                    flag1 = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
                }

                if (flag1) {
                    BlockPos blockpos = result.getBlockPos().offset(result.sideHit);

                    if (this.world.isAirBlock(blockpos)) {
                        this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
                    }
                }
            }

            this.setDead();
        }
    }
}
