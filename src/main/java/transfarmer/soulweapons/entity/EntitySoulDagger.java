package transfarmer.soulweapons.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.capability.SoulWeaponHelper;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SKILLS;
import static transfarmer.soulweapons.data.SoulWeaponEnchantment.FIRE_ASPECT;
import static transfarmer.soulweapons.data.SoulWeaponType.DAGGER;

public class EntitySoulDagger extends EntityArrow {
    public ItemStack itemStack;
    private float attackDamageRatio;
    private int ticksSeeking;
    private int ticksInGround;
    private int xTile;
    private int yTile;
    private int zTile;
    private int inData;
    private Block inTile;

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

        final BlockPos blockPos = new BlockPos(this.xTile, this.yTile, this.zTile);
        final IBlockState blockState = this.world.getBlockState(blockPos);
        final Block block = blockState.getBlock();

        if (blockState.getMaterial() != Material.AIR) {
            AxisAlignedBB axisalignedbb = blockState.getCollisionBoundingBox(this.world, blockPos);

            if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockPos).contains(new Vec3d(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0) {
            --this.arrowShake;
        }

        if (this.shootingEntity instanceof EntityPlayer) {
            final ISoulWeapon capability = this.shootingEntity.getCapability(CAPABILITY, null);
            final float attackSpeed = 4 + capability.getAttackSpeed(DAGGER);

            if (capability.getDatum(SKILLS, DAGGER) >= 4 && this.shootingEntity.isSneaking()
                || capability.getDatum(SKILLS, DAGGER) >= 3
                && (this.ticksExisted > 60 / attackSpeed || this.ticksInGround > 20 / attackSpeed)) {
                final AxisAlignedBB boundingBox = this.shootingEntity.getEntityBoundingBox();
                this.ticksSeeking++;

                if (this.inGround) {
                    this.motionX = 0;
                    this.motionY = 0;
                    this.motionZ = 0;
                    this.inGround = false;
                }

                if (Math.hypot(Math.hypot(this.motionX, this.motionY), this.motionZ) > 0.3) {
                    final double multiplier = 0.85;
                    this.motionX *= multiplier;
                    this.motionY *= multiplier;
                    this.motionZ *= multiplier;
                } else {
                    final double multiplier = 0.8;
                    final double displacementX = boundingBox.minX + (boundingBox.maxX - boundingBox.minX) / 2 - this.posX;
                    final double displacementY = boundingBox.minY + (boundingBox.maxY - boundingBox.minY) / 2 - this.posY;
                    final double displacementZ = boundingBox.minZ + (boundingBox.maxZ - boundingBox.minZ) / 2 - this.posZ;
                    final Vec3d normalized = new Vec3d(displacementX, displacementY, displacementZ).normalize();

                    this.motionX += normalized.x * multiplier;
                    this.motionY += normalized.y * multiplier;
                    this.motionZ += normalized.z * multiplier;
                }
            } else {
                if (this.ticksSeeking > 0) {
                    this.ticksSeeking = 0;
                }
            }
        }

        if (this.inGround) {
            final int metadata = block.getMetaFromState(blockState);

            if ((block != this.inTile || metadata != this.inData) && !this.world.collidesWithAnyBlock(this.getEntityBoundingBox().grow(0.05D))) {
                this.inGround = false;
                this.motionX *= this.rand.nextFloat() * 0.2F;
                this.motionY *= this.rand.nextFloat() * 0.2F;
                this.motionZ *= this.rand.nextFloat() * 0.2F;
                this.ticksInGround = 0;
            } else {
                this.ticksInGround++;
            }
        } else {
            Vec3d pos = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d hitPos = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult rayTraceResult = this.world.rayTraceBlocks(pos, hitPos, false, true, false);

            if (rayTraceResult != null) {
                hitPos = new Vec3d(rayTraceResult.hitVec.x, rayTraceResult.hitVec.y, rayTraceResult.hitVec.z);
            }

            final Entity entity = this.findEntityOnPath(pos, hitPos);

            if (entity != null) {
                rayTraceResult = new RayTraceResult(entity);
            }

            if (rayTraceResult != null && rayTraceResult.entityHit instanceof EntityPlayer) {
                EntityPlayer playerHit = (EntityPlayer) rayTraceResult.entityHit;

                if (this.shootingEntity instanceof EntityPlayer && !((EntityPlayer) this.shootingEntity).canAttackPlayer(playerHit)) {
                    rayTraceResult = null;
                }
            }

            if (rayTraceResult != null && !ForgeEventFactory.onProjectileImpact(this, rayTraceResult)) {
                this.onHit(rayTraceResult);
            }

            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25, this.posY - this.motionY * 0.25, this.posZ - this.motionZ * 0.25, this.motionX, this.motionY, this.motionZ);
                }
            }

            if (this.isWet()) {
                this.extinguish();
            }

            if (this.ticksSeeking == 0) {
                final float acceleration = this.isInWater() ? 0.6F : 0.99F;

                this.motionX *= acceleration;
                this.motionY *= acceleration;
                this.motionZ *= acceleration;

                if (!this.hasNoGravity()) {
                    this.motionY -= 0.05;
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * 180 / Math.PI);

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

            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }

        if (this.shootingEntity != null && !this.shootingEntity.isEntityAlive()) {
            this.setDead();
        }
    }

    @Override
    protected void onHit(RayTraceResult result) {
        final Entity entity = result.entityHit;
        if (entity != null) {
            if (this.shootingEntity instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer) this.shootingEntity;
                final ISoulWeapon capability = player.getCapability(CAPABILITY, null);

                final DamageSource damageSource = this.shootingEntity == null
                    ? DamageSource.causeThrownDamage(this, this)
                    : DamageSource.causeThrownDamage(this, this.shootingEntity);

                int burnTime = 0;

                float attackDamage = 1 + capability.getAttackDamage(DAGGER);
                final float attackDamageModifier = entity instanceof EntityLivingBase
                    ? EnchantmentHelper.getModifierForCreature(this.itemStack, ((EntityLivingBase) entity).getCreatureAttribute())
                    : EnchantmentHelper.getModifierForCreature(this.itemStack, EnumCreatureAttribute.UNDEFINED);

                if (attackDamage > 0 || attackDamageModifier > 0) {
                    final int knockbackModifier = EnchantmentHelper.getKnockbackModifier(player);
                    float initialHealth = 0;

                    attackDamage = (attackDamage + attackDamageModifier) * this.attackDamageRatio;
                    burnTime += EnchantmentHelper.getFireAspectModifier(player);

                    if (entity instanceof EntityLivingBase) {
                        initialHealth = ((EntityLivingBase) entity).getHealth();

                        if (this.isBurning()) {
                            burnTime += 5;
                        }

                        if (capability.getEnchantments(DAGGER).containsKey(FIRE_ASPECT) && !(entity instanceof EntityEnderman)) {
                            burnTime += capability.getEnchantment(FIRE_ASPECT, DAGGER) * 4;
                        }

                        if (burnTime > 0 && !entity.isBurning()) {
                            entity.setFire(1);
                        }
                    }

                    final double motionX = entity.motionX;
                    final double motionY = entity.motionY;
                    final double motionZ = entity.motionZ;

                    if (entity.attackEntityFrom(damageSource, attackDamage)) {
                        if (knockbackModifier > 0) {
                            if (entity instanceof EntityLivingBase) {
                                ((EntityLivingBase) entity).knockBack(player, knockbackModifier * 0.5F, MathHelper.sin(player.rotationYaw * 0.017453292F), -MathHelper.cos(player.rotationYaw * 0.017453292F));
                            } else {
                                entity.addVelocity(-MathHelper.sin(player.rotationYaw * 0.017453292F) * knockbackModifier * 0.5F, 0.1D, MathHelper.cos(player.rotationYaw * 0.017453292F) * knockbackModifier * 0.5F);
                            }
                        }

                        if (entity instanceof EntityPlayerMP && entity.velocityChanged) {
                            ((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(entity));
                            entity.velocityChanged = false;
                            entity.motionX = motionX;
                            entity.motionY = motionY;
                            entity.motionZ = motionZ;
                        }

                        if (attackDamageModifier > 0) {
                            player.onEnchantmentCritical(entity);
                        }

                        player.setLastAttackedEntity(entity);

                        if (entity instanceof EntityLivingBase) {
                            EnchantmentHelper.applyThornEnchantments((EntityLivingBase) entity, player);
                        }

                        EnchantmentHelper.applyArthropodEnchantments(player, entity);

                        if (entity instanceof EntityLivingBase) {
                            final float damageDealt = initialHealth - ((EntityLivingBase) entity).getHealth();
                            player.addStat(StatList.DAMAGE_DEALT, Math.round(damageDealt * 10.0F));

                            if (burnTime > 0) {
                                entity.setFire(burnTime);
                            }

                            if (player.world instanceof WorldServer && damageDealt > 2.0F) {
                                int k = (int) ((double) damageDealt * 0.5D);
                                ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, entity.posX, entity.posY + (double) (entity.height * 0.5F), entity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }
                    }
                } else {
                    if (burnTime > 0) {
                        entity.extinguish();
                    }
                    if (capability.getDatum(SKILLS, DAGGER) < 2) {
                        this.motionX *= -0.1;
                        this.motionY *= -0.1;
                        this.motionZ *= -0.1;
                        this.rotationYaw += 180;
                        this.prevRotationYaw += 180;

                        if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.001) {
                            if (this.pickupStatus == PickupStatus.ALLOWED) {
                                this.entityDropItem(this.getArrowStack(), 0.1F);
                            }

                            this.setDead();
                        }
                    }
                }
            }
        } else if (this.ticksSeeking == 0) {
            final BlockPos blockPos = result.getBlockPos();
            final IBlockState blockState = this.world.getBlockState(blockPos);
            final float speed = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);

            this.xTile = blockPos.getX();
            this.yTile = blockPos.getY();
            this.zTile = blockPos.getZ();
            this.inTile = blockState.getBlock();
            this.inData = this.inTile.getMetaFromState(blockState);
            this.motionX = result.hitVec.x - this.posX;
            this.motionY = result.hitVec.y - this.posY;
            this.motionZ = result.hitVec.z - this.posZ;
            this.posX -= this.motionX / speed * 0.02;
            this.posY -= this.motionY / speed * 0.02;
            this.posZ -= this.motionZ / speed * 0.02;
            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            this.inGround = true;
            this.arrowShake = 7;

            if (blockState.getMaterial() != Material.AIR) {
                this.inTile.onEntityCollision(this.world, blockPos, blockState, this);
            }
        }
    }

    public void shoot(final Entity shooter, final float pitch, final float yaw, final float velocity, final float inaccuracy) {
        this.shoot(shooter, pitch, yaw, 0, velocity, inaccuracy);
        this.attackDamageRatio = velocity / 2.5F;
    }

    @Override
    public void onCollideWithPlayer(final EntityPlayer player) {
        if (!this.world.isRemote && this.ticksExisted > 20 / (4 + player.getCapability(CAPABILITY, null).getAttackSpeed(DAGGER))
            && this.arrowShake <= 0 && player.equals(this.shootingEntity) && (this.pickupStatus == PickupStatus.ALLOWED
            || this.pickupStatus == PickupStatus.CREATIVE_ONLY && player.isCreative())) {
            if (player.isCreative() && SoulWeaponHelper.hasSoulWeapon(player)) {
                player.onItemPickup(this, 1);
                this.setDead();
            } else if (player.inventory.addItemStackToInventory(this.getArrowStack())) {
                player.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }
}
