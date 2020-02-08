package transfarmer.soulweapons.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import transfarmer.soulweapons.capability.ISoulWeapon;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.ATTACK_DAMAGE;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.KNOCKBACK_ATTRIBUTE;
import static transfarmer.soulweapons.data.SoulWeaponEnchantment.FIRE_ASPECT;
import static transfarmer.soulweapons.data.SoulWeaponEnchantment.KNOCKBACK;
import static transfarmer.soulweapons.data.SoulWeaponEnchantment.SHARPNESS;
import static transfarmer.soulweapons.data.SoulWeaponType.DAGGER;

public class EntitySoulDagger extends EntityArrow {
    private int ticksInAir;
    private int ticksInGround;
    private int xTile;
    private int yTile;
    private int zTile;
    private int inData;
    private Block inTile;
    public ItemStack itemStack;
    public Entity owner;

    public EntitySoulDagger(World world) {
        super(world);
    }

    public EntitySoulDagger(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntitySoulDagger(World world, EntityLivingBase shooter, ItemStack itemStack) {
        this(world, shooter.posX, shooter.posY + shooter.getEyeHeight() - 0.1, shooter.posZ);
        this.shootingEntity = shooter;
        this.itemStack = itemStack;
        this.owner = shooter;

        if (shooter instanceof EntityPlayer) {
            this.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
        }
    }

    @Override
    protected ItemStack getArrowStack() {
        return this.itemStack;
    }

    @Override
    public void onUpdate() {
        if (!this.world.isRemote) {
            this.setFlag(6, this.isGlowing());
        }

        this.onEntityUpdate();

        if (this.prevRotationPitch == 0F && this.prevRotationYaw == 0F) {
            final float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * 180 / Math.PI);
            this.rotationPitch = (float) (MathHelper.atan2(this.motionY, f) * 180 / Math.PI);
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }

        final BlockPos blockpos = new BlockPos(this.xTile, this.yTile, this.zTile);
        final IBlockState iblockstate = this.world.getBlockState(blockpos);
        final Block block = iblockstate.getBlock();

        if (iblockstate.getMaterial() != Material.AIR) {
            AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.world, blockpos);

            if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).contains(new Vec3d(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0) {
            --this.arrowShake;
        }

        if (this.inGround) {
            int j = block.getMetaFromState(iblockstate);

            if ((block != this.inTile || j != this.inData) && !this.world.collidesWithAnyBlock(this.getEntityBoundingBox().grow(0.05D))) {
                this.inGround = false;
                this.motionX *= this.rand.nextFloat() * 0.2F;
                this.motionY *= this.rand.nextFloat() * 0.2F;
                this.motionZ *= this.rand.nextFloat() * 0.2F;
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            } else {
                ++this.ticksInGround;
            }

            ++this.timeInGround;
        } else {
            this.timeInGround = 0;
            ++this.ticksInAir;
            Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult rayTraceResult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);

            if (rayTraceResult != null) {
                vec3d = new Vec3d(rayTraceResult.hitVec.x, rayTraceResult.hitVec.y, rayTraceResult.hitVec.z);
            }

            Entity entity = this.findEntityOnPath(vec3d1, vec3d);

            if (entity != null) {
                rayTraceResult = new RayTraceResult(entity);
            }

            if (rayTraceResult != null && rayTraceResult.entityHit instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) rayTraceResult.entityHit;

                if (this.shootingEntity instanceof EntityPlayer && !((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer)) {
                    rayTraceResult = null;
                }
            }

            if (rayTraceResult != null && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, rayTraceResult)) {
                this.onHit(rayTraceResult);
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180 / Math.PI));

            while (this.rotationPitch - this.prevRotationPitch >= 180) {
                this.prevRotationPitch += 360;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180) {
                this.prevRotationYaw -= 360;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180) {
                this.prevRotationYaw += 360;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float f1 = 0.99F;

            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25, this.posY - this.motionY * 0.25, this.posZ - this.motionZ * 0.25, this.motionX, this.motionY, this.motionZ);
                }

                f1 = 0.6F;
            }

            if (this.isWet()) {
                this.extinguish();
            }

            this.motionX *= f1;
            this.motionY *= f1;
            this.motionZ *= f1;

            if (!this.hasNoGravity()) {
                this.motionY -= 0.05;
            }

            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (result.entityHit != null) {
            if (this.shootingEntity instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer) this.shootingEntity;
                final ISoulWeapon capability = player.getCapability(CAPABILITY, null);
                float attackDamage = capability.getAttribute(ATTACK_DAMAGE, DAGGER) + 1;

                if (capability.getEnchantments(DAGGER).containsKey(SHARPNESS)) {
                    attackDamage += 1 + (capability.getEnchantment(SHARPNESS, DAGGER) - 1) / 2F;
                }

                DamageSource damageSource;

                if (this.shootingEntity == null) {
                    damageSource = DamageSource.causeThrownDamage(this, this);
                } else {
                    damageSource = DamageSource.causeThrownDamage(this, this.shootingEntity);
                }

                if (result.entityHit.attackEntityFrom(damageSource, attackDamage)) {
                    if (result.entityHit instanceof EntityLivingBase) {
                        EntityLivingBase entity = (EntityLivingBase) result.entityHit;
                        float knockback = 0;
                        int burnTime = 0;

                        if (this.shootingEntity instanceof EntityLivingBase) {
                            EnchantmentHelper.applyThornEnchantments(entity, this.shootingEntity);
                            EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase) this.shootingEntity, entity);
                        }

                        if (capability.getAttribute(KNOCKBACK_ATTRIBUTE, DAGGER) > 0) {
                            knockback += 1 + capability.getAttribute(KNOCKBACK_ATTRIBUTE, DAGGER) / 8;
                        }

                        if (capability.getEnchantment(KNOCKBACK, DAGGER) > 0) {
                            knockback += capability.getEnchantment(KNOCKBACK, DAGGER);
                        }

                        if (knockback > 0) {
                            result.entityHit.addVelocity(this.motionX * knockback, this.motionY * knockback, this.motionZ * knockback);
                        }

                        if (this.isBurning()) {
                            burnTime += 5;
                        }

                        if (capability.getEnchantments(DAGGER).containsKey(FIRE_ASPECT) && !(entity instanceof EntityEnderman)) {
                            burnTime += capability.getEnchantment(FIRE_ASPECT, DAGGER) * 4;
                        }

                        if (burnTime > 0) {
                            result.entityHit.setFire(burnTime);
                        }

                        if (this.shootingEntity != null && entity != this.shootingEntity && entity instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                            ((EntityPlayerMP) this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0));
                        }
                    }

                    this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                } else {
                    this.motionX *= -0.1;
                    this.motionY *= -0.1;
                    this.motionZ *= -0.1;
                    this.rotationYaw += 180;
                    this.prevRotationYaw += 180;
                    this.ticksInAir = 0;

                    if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.001) {
                        if (this.pickupStatus == PickupStatus.ALLOWED) {
                            this.entityDropItem(this.getArrowStack(), 0.1F);
                        }

                        this.setDead();
                    }
                }
            }
        } else {
            final BlockPos blockpos = result.getBlockPos();
            final IBlockState iblockstate = this.world.getBlockState(blockpos);
            final float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);

            this.xTile = blockpos.getX();
            this.yTile = blockpos.getY();
            this.zTile = blockpos.getZ();
            this.inTile = iblockstate.getBlock();
            this.inData = this.inTile.getMetaFromState(iblockstate);
            this.motionX = result.hitVec.x - this.posX;
            this.motionY = result.hitVec.y - this.posY;
            this.motionZ = result.hitVec.z - this.posZ;
            this.posX -= this.motionX / f2 * 0.05;
            this.posY -= this.motionY / f2 * 0.05;
            this.posZ -= this.motionZ / f2 * 0.05;
            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            this.inGround = true;
            this.arrowShake = 7;

            if (iblockstate.getMaterial() != Material.AIR) {
                this.inTile.onEntityCollision(this.world, blockpos, iblockstate, this);
            }
        }
    }

    @Override
    public void onCollideWithPlayer(final EntityPlayer entity) {
        if (!this.world.isRemote && this.inGround && this.arrowShake <= 0 && entity == this.shootingEntity
            && (this.pickupStatus == EntityArrow.PickupStatus.ALLOWED || this.pickupStatus == EntityArrow.PickupStatus.CREATIVE_ONLY && entity.capabilities.isCreativeMode)
            && entity.inventory.addItemStackToInventory(this.getArrowStack())) {
            entity.onItemPickup(this, 1);
            this.setDead();
        }
    }
}
