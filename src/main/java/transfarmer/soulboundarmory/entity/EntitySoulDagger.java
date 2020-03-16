package transfarmer.soulboundarmory.entity;

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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;

import java.util.UUID;

import static transfarmer.soulboundarmory.statistics.SoulAttribute.ATTACK_DAMAGE;
import static transfarmer.soulboundarmory.statistics.SoulAttribute.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.SoulDatum.DATA;
import static transfarmer.soulboundarmory.statistics.SoulEnchantment.SOUL_FIRE_ASPECT;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.DAGGER;

public class EntitySoulDagger extends EntityArrow {
    public ItemStack itemStack;
    private UUID shooterUUID;
    private boolean spawnClone;
    private boolean isClone;
    private float attackDamageRatio;
    private int ticksToSeek = -1;
    private int ticksSeeking;
    private int ticksInGround;
    private int xTile;
    private int yTile;
    private int zTile;
    private int inData;
    private Block inTile;

    public EntitySoulDagger(final World world) {
        super(world);
    }

    public EntitySoulDagger(final World world, final double x, final double y, final double z) {
        super(world, x, y, z);
    }

    public EntitySoulDagger(final World world, final EntityLivingBase shooter, final ItemStack itemStack, final boolean spawnClone) {
        this(world, shooter.posX, shooter.posY + shooter.getEyeHeight() - 0.1, shooter.posZ);
        this.shootingEntity = shooter;
        this.itemStack = itemStack;

        if (shooter instanceof EntityPlayer) {
            this.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
        }

        this.shooterUUID = UUID.fromString(shooter.getUniqueID().toString());
        this.spawnClone = spawnClone;
    }

    @Override
    protected ItemStack getArrowStack() {
        return this.itemStack != null
                ? this.itemStack
                : this.shootingEntity instanceof EntityPlayer
                ? SoulWeaponProvider.get(this.shootingEntity).getItemStack(DAGGER)
                : null;
    }

    @Override
    public void onUpdate() {
        if (!this.world.isRemote) {
            this.setFlag(6, this.isGlowing());
        }

        this.onEntityUpdate();

        if (this.shootingEntity == null && this.shooterUUID != null) {
            this.shootingEntity = this.world.getPlayerEntityByUUID(this.shooterUUID);
        }

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            final float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * 180 / Math.PI);
            this.rotationPitch = (float) (MathHelper.atan2(this.motionY, f) * 180 / Math.PI);
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }

        final BlockPos blockPos = new BlockPos(this.xTile, this.yTile, this.zTile);
        final IBlockState blockState = this.world.getBlockState(blockPos);
        final Block block = blockState.getBlock();

        if (this.arrowShake > 0) {
            --this.arrowShake;
        }

        if (this.shootingEntity instanceof EntityPlayer) {
            final ISoulWeapon capability = SoulWeaponProvider.get(this.shootingEntity);
            final float attackSpeed = capability.getAttribute(ATTACK_SPEED, DAGGER, true, true);

            if (capability.getDatum(DATA.skills, DAGGER) >= 4 && this.shootingEntity.isSneaking() && this.ticksExisted >= 60 / attackSpeed
                    || capability.getDatum(DATA.skills, DAGGER) >= 3
                    && (this.ticksExisted >= 300 || this.ticksInGround > 20 / attackSpeed
                    || this.shootingEntity.getDistance(this) >= 256)) {
                final AxisAlignedBB boundingBox = this.shootingEntity.getEntityBoundingBox();
                double multiplier = 1.8 / capability.getAttribute(ATTACK_SPEED, DAGGER, true, true);
                final double dX = boundingBox.minX + (boundingBox.maxX - boundingBox.minX) / 2 - this.posX;
                final double dY = boundingBox.minY + (boundingBox.maxY - boundingBox.minY) / 2 - this.posY;
                final double dZ = boundingBox.minZ + (boundingBox.maxZ - boundingBox.minZ) / 2 - this.posZ;

                if (this.ticksToSeek == -1 && !this.inGround) {
                    this.ticksToSeek = (int) Math.round(Math.log(0.05) / Math.log(multiplier));
                }

                if (this.ticksToSeek > 0 && !this.inGround) {
                    this.motionX *= multiplier;
                    this.motionY *= multiplier;
                    this.motionZ *= multiplier;

                    this.ticksToSeek--;
                } else {
                    final Vec3d normalized = new Vec3d(dX, dY, dZ).normalize();

                    if (this.ticksToSeek != 0) {
                        this.ticksToSeek = 0;
                    }

                    if (this.inGround) {
                        this.motionX = 0;
                        this.motionY = 0;
                        this.motionZ = 0;
                        this.inGround = false;
                    }

                    if (this.ticksSeeking > 5) {
                        multiplier = Math.min(0.08 * this.ticksSeeking, 5);

                        if (Math.sqrt(dX * dX + dY * dY + dZ * dZ) <= 5 && this.ticksSeeking > 100) {
                            this.motionX = dX;
                            this.motionY = dY;
                            this.motionZ = dZ;
                        } else {
                            this.motionX = normalized.x * multiplier;
                            this.motionY = normalized.y * multiplier;
                            this.motionZ = normalized.z * multiplier;
                        }
                    } else {
                        multiplier = 0.08;
                        this.motionX += normalized.x * multiplier;
                        this.motionY += normalized.y * multiplier;
                        this.motionZ += normalized.z * multiplier;
                    }

                    this.ticksSeeking++;
                }
            } else {
                if (this.ticksSeeking > 0) {
                    this.ticksSeeking = 0;
                }
            }
        } else if (blockState.getMaterial() != Material.AIR) {
            final AxisAlignedBB axisAlignedBB = blockState.getCollisionBoundingBox(this.world, blockPos);

            if (axisAlignedBB != Block.NULL_AABB && axisAlignedBB.offset(blockPos).contains(new Vec3d(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
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
            final Vec3d pos = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d hitPos = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult rayTraceResult = this.world.rayTraceBlocks(pos, hitPos, false, true, false);

            if (rayTraceResult != null) {
                hitPos = new Vec3d(rayTraceResult.hitVec.x, rayTraceResult.hitVec.y, rayTraceResult.hitVec.z);
            }

            final Entity entity = this.findEntityOnPath(pos, hitPos);

            if (entity != null) {
                rayTraceResult = new RayTraceResult(entity);
            }

            if (rayTraceResult != null && rayTraceResult.entityHit instanceof EntityPlayer
                    && this.shootingEntity instanceof EntityPlayer
                    && !((EntityPlayer) this.shootingEntity).canAttackPlayer((EntityPlayer) rayTraceResult.entityHit)) {
                rayTraceResult = null;
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

            if (this.ticksToSeek == -1) {
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
    protected void onHit(final RayTraceResult result) {
        final Entity entity = result.entityHit;

        if (entity != null) {
            if (this.spawnClone) {
                final EntitySoulDagger dagger = (EntitySoulDagger) this.clone();

                this.world.spawnEntity(dagger);
                this.spawnClone = false;
            }

            if (!this.isClone && this.ticksSeeking == 0) {
                this.motionX *= -0.1;
                this.motionY *= -0.1;
                this.motionZ *= -0.1;
                this.rotationYaw += 180;
                this.prevRotationYaw += 180;
            }

            if (this.shootingEntity instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer) this.shootingEntity;
                final ISoulWeapon capability = SoulWeaponProvider.get(player);
                final DamageSource damageSource = this.shootingEntity == null
                        ? DamageSource.causeThrownDamage(this, this)
                        : DamageSource.causeThrownDamage(this, this.shootingEntity);
                final float attackDamageModifier = entity instanceof EntityLivingBase
                        ? EnchantmentHelper.getModifierForCreature(this.itemStack, ((EntityLivingBase) entity).getCreatureAttribute())
                        : EnchantmentHelper.getModifierForCreature(this.itemStack, EnumCreatureAttribute.UNDEFINED);
                final float attackDamage = (capability.getAttribute(ATTACK_DAMAGE, DAGGER, true, true) + attackDamageModifier) * this.attackDamageRatio;
                int burnTime = 0;

                if (attackDamage > 0) {
                    final int knockbackModifier = EnchantmentHelper.getKnockbackModifier(player);
                    float initialHealth = 0;

                    burnTime += EnchantmentHelper.getFireAspectModifier(player);

                    if (entity instanceof EntityLivingBase) {
                        initialHealth = ((EntityLivingBase) entity).getHealth();

                        if (this.isBurning()) {
                            burnTime += 5;
                        }

                        if (capability.getEnchantments(DAGGER).containsKey(SOUL_FIRE_ASPECT) && !(entity instanceof EntityEnderman)) {
                            burnTime += capability.getEnchantment(SOUL_FIRE_ASPECT, DAGGER) * 4;
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
                                entity.addVelocity(-MathHelper.sin(player.rotationYaw * 0.017453292F) * knockbackModifier * 0.5, 0.1, MathHelper.cos(player.rotationYaw * 0.017453292F) * knockbackModifier * 0.5);
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
                            player.addStat(StatList.DAMAGE_DEALT, Math.round(damageDealt * 10));

                            if (burnTime > 0) {
                                entity.setFire(burnTime);
                            }

                            if (player.world instanceof WorldServer && damageDealt > 2) {
                                ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, entity.posX, entity.posY + entity.height * 0.5, entity.posZ, (int) (damageDealt * 0.5), 0.1, 0, 0.1, 0.2);
                            }
                        }
                    } else {
                        if (burnTime > 0) {
                            entity.extinguish();
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
            this.inGround = true;
            this.arrowShake = 7;
            this.playSound(blockState.getBlock().getSoundType(blockState, this.world, blockPos, this).getBreakSound(), 1, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

            if (blockState.getMaterial() != Material.AIR) {
                this.inTile.onEntityCollision(this.world, blockPos, blockState, this);
            }
        }
    }

    public void shoot(final Entity shooter, final float pitch, final float yaw, final float velocity, final float attackDamageRatio, final float inaccuracy) {
        super.shoot(shooter, pitch, yaw, 0, velocity, inaccuracy);
        this.attackDamageRatio = attackDamageRatio;
    }

    @Override
    public void onCollideWithPlayer(final EntityPlayer player) {
        if (!this.world.isRemote && this.ticksExisted > 20 / (SoulWeaponProvider.get(player).getAttribute(ATTACK_SPEED, DAGGER, true, true))
                && this.arrowShake <= 0 && player.getUniqueID().equals(this.shooterUUID) && (this.pickupStatus == PickupStatus.ALLOWED
                || this.pickupStatus == PickupStatus.CREATIVE_ONLY && player.isCreative())) {
            if (this.isClone) {
                player.onItemPickup(this, 1);
                this.setDead();
            } else if (player.isCreative() && SoulItemHelper.hasSoulWeapon(player)) {
                player.onItemPickup(this, 1);
                this.setDead();
            } else if (SoulItemHelper.addItemStack(this.getArrowStack(), player, true)) {
                player.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }

    @Override
    public void writeEntityToNBT(final NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setString("UUID", this.shooterUUID.toString());
        compound.setInteger("dimensionID", this.world.provider.getDimension());
    }

    @Override
    public void readEntityFromNBT(final NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.shooterUUID = UUID.fromString(compound.getString("UUID"));
        this.world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(compound.getInteger("dimensionID"));
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Object clone() {
        final EntitySoulDagger copy = new EntitySoulDagger(this.world, (EntityLivingBase) this.shootingEntity, this.itemStack, false);

        copy.isClone = true;
        copy.posX = this.posX;
        copy.posY = this.posY;
        copy.posZ = this.posZ;
        copy.motionX = this.motionX;
        copy.motionY = this.motionY;
        copy.motionZ = this.motionZ;
        copy.rotationPitch = this.rotationPitch;
        copy.rotationYaw = this.rotationYaw;
        copy.prevRotationPitch = this.prevRotationPitch;
        copy.prevRotationYaw = this.prevRotationYaw;
        copy.attackDamageRatio = this.attackDamageRatio;

        return copy;
    }
}
