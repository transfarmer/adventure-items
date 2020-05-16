package transfarmer.soulboundarmory.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntityMP;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import transfarmer.farmerlib.util.EntityUtil;
import transfarmer.soulboundarmory.component.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.component.soulbound.weapon.IWeaponComponent;
import transfarmer.soulboundarmory.entity.damage.ISoulboundDamageSource;

import javax.annotation.Nonnull;
import java.util.UUID;

import static net.minecraft.entity.projectile.ProjectileEntity.PickupPermission.ALLOWED;
import static net.minecraft.init.Enchantments.FIRE_ASPECT;
import static transfarmer.soulboundarmory.skill.Skills.RETURN;
import static transfarmer.soulboundarmory.skill.Skills.SNEAK_RETURN;
import static transfarmer.soulboundarmory.statistics.Item.DAGGER;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_DAMAGE;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_SPEED;

public class SoulboundDaggerEntity extends ExtendedProjectile {
    public ItemStack itemStack;
    protected boolean spawnClone;
    protected boolean isClone;
    protected boolean passedX;
    protected boolean passedZ;
    protected float attackDamageRatio;
    protected int ticksToSeek;
    protected int ticksSeeking;
    protected int ticksInGround;
    protected int xTile;
    protected int yTile;
    protected int zTile;
    protected int inData;
    protected Block inTile;
    protected IWeaponComponent component;

    public SoulboundDaggerEntity(final World world) {
        super(world);
    }

    public SoulboundDaggerEntity(final World world, final LivingEntity shooter, final ItemStack itemStack,
                                 final boolean spawnClone) {
        this(world, shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());

        this.ticksToSeek = -1;

        if (shooter instanceof PlayerEntity) {
            this.pickupType = ALLOWED;
        }

        this.itemStack = itemStack;
        this.spawnClone = spawnClone;

        this.attackDamageRatio = attackDamageRatio;

        this.updateShooter();
        this.setDamage();
    }

    public SoulboundDaggerEntity(final World world, final UUID id, final ItemStack itemStack,
                                 final boolean spawnClone) {
        this(world, (LivingEntity) EntityUtil.getEntity(id), itemStack, spawnClone);
    }

    public SoulboundDaggerEntity(final SoulboundDaggerEntity original, final boolean spawnClone) {
        this(original.world, original.ownerUuid, original.itemStack, spawnClone);

        this.isClone = true;
        this.setPos(original.getX(), original.getY(), original.getZ());
        this.setVelocity(original.getVelocity());
        this.pitch = original.pitch;
        this.yaw = original.yaw;
        this.prevPitch = original.prevPitch;
        this.prevYaw = original.prevYaw;
        this.attackDamageRatio = original.attackDamageRatio;
    }

    public <T extends ProjectileEntity> SoulboundDaggerEntity(final EntityType<T> entityType, final World world) {
        super(entityType, world);
    }

    public void setDamage() {
        this.setDamage(this.component.getAttribute(DAGGER, ATTACK_DAMAGE) * this.attackDamageRatio);
    }

    @Override
    @Nonnull
    protected ItemStack asItemStack() {
        return this.itemStack != null
                ? this.itemStack
                : this.getOwner() instanceof PlayerEntity
                ? this.component.getItemStack(DAGGER)
                : ItemStack.EMPTY;
    }

    protected void updateShooter() {
        if (this.component == null && this.getOwner() != null) {
            this.component = IWeaponComponent.get(this.getOwner());
        }
    }

    @Override
    public void tick() {
        if (!this.tryReturn()) {
            super.tick();
        }
    }

    protected boolean tryReturn() {
        final Entity owner = this.getOwner();

        if (owner instanceof PlayerEntity) {
            final double attackSpeed = this.component.getAttribute(DAGGER, ATTACK_SPEED);

            if (this.component.hasSkill(DAGGER, SNEAK_RETURN) && owner.isSneaking()
                    && this.life.get() >= 60 / attackSpeed
                    || this.component.hasSkill(DAGGER, RETURN)
                    && (this.life.get() >= 300 || this.ticksInGround > 20 / attackSpeed
                    || owner.distanceTo(this)
                    >= 16 * (this.world.getServer().getPlayerManager().getViewDistance() - 1)
                    || this.getY() <= 0) || this.ticksSeeking > 0) {
                final Box box = owner.getBoundingBox();
                final double dX = (box.getMax(Axis.X) + box.getMin(Axis.X)) / 2 - this.getZ();
                final double dY = (box.getMax(Axis.Y) + box.getMin(Axis.Y)) / 2 - this.getY();
                final double dZ = (box.getMax(Axis.Z) + box.getMin(Axis.Z)) / 2 - this.getZ();
                double multiplier = 1.8 / attackSpeed;

                if (this.ticksToSeek == -1 && !this.inGround) {
                    this.ticksToSeek = (int) Math.round(Math.log(0.05) / Math.log(multiplier));
                }

                if (this.ticksToSeek > 0 && !this.inGround) {
                    this.setVelocity(this.velocityX() * multiplier, this.velocityY() * multiplier, this.velocityZ() * multiplier);

                    this.ticksToSeek--;
                } else {
                    final Vec3d normalized = new Vec3d(dX, dY, dZ).normalize();

                    if (this.ticksToSeek != 0) {
                        this.ticksToSeek = 0;
                    }

                    if (this.inGround) {
                        this.setVelocity(0, 0, 0);
                        this.inGround = false;
                    }

                    if (this.ticksSeeking > 5) {
                        multiplier = Math.min(0.08 * this.ticksSeeking, 5);

                        if (Math.sqrt(dX * dX + dY * dY + dZ * dZ) <= 5 && this.ticksSeeking > 100) {
                            this.setVelocity(dX, dY, dZ);
                        } else {
                            this.setVelocity(normalized.x * multiplier, normalized.y * multiplier, normalized.z * multiplier);
                        }
                    } else {
                        multiplier = 0.08;
                        this.addVelocity(normalized.x * multiplier, normalized.y * multiplier, normalized.z * multiplier);
                    }

                    this.ticksSeeking++;

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void onHit(final HitResult result) {
        final Vec3d pos = result.getPos();
        final BlockPos blockPos = new BlockPos(pos);
        final BlockState blockState = this.world.getBlockState(blockPos);

        if (this.ticksSeeking == 0) {
            super.onHit(result);
        }

        if (this.world.isClient) {
            //noinspection MethodCallSideOnly
            this.playSound(blockState.getBlock().getSoundGroup(blockState).getHitSound(), 1, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        }
    }

    @Override
    protected void onHit(final LivingEntity target) {
        final Entity owner = this.getOwner();

        if (this.spawnClone) {
            this.world.spawnEntity(new SoulboundDaggerEntity(this, false));
        }

        if (!this.isClone && this.ticksSeeking == 0) {
            this.motionX *= -0.1;
            this.motionY *= -0.1;
            this.velocityZ() *= -0.1;
            this.yaw += 180;
            this.prevYaw += 180;
        }

        if (owner instanceof PlayerEntity) {
            final PlayerEntity player = (PlayerEntity) owner;
            final DamageSource damageSource = DamageSource.thrownProjectile(this, owner);

            ((ISoulboundDamageSource) damageSource).setItemStack(this.asItemStack());

            int burnTime = 0;

            if (attackDamage > 0) {
                final int knockbackModifier = EnchantmentHelper.getKnockbackModifier(player);
                float initialHealth = 0;

                burnTime += EnchantmentHelper.getFireAspectModifier(player);

                if (target instanceof LivingEntity) {
                    initialHealth = ((LivingEntity) target).getHealth();

                    if (this.isBurning()) {
                        burnTime += 5;
                    }

                    if (this.component.getEnchantments(DAGGER).containsKey(FIRE_ASPECT) && !(target instanceof EntityEnderman)) {
                        burnTime += this.component.getEnchantment(DAGGER, FIRE_ASPECT) * 4;
                    }

                    if (burnTime > 0 && !target.isBurning()) {
                        target.setFire(1);
                    }
                }

                final double motionX = target.motionX;
                final double motionY = target.motionY;
                final double velocityZ () = target.velocityZ();

                if (target.attackEntityFrom(damageSource, (float) attackDamage)) {
                    if (knockbackModifier > 0) {
                        if (target instanceof LivingEntity) {
                            ((LivingEntity) target).knockBack(player, knockbackModifier * 0.5F, MathHelper.sin(player.yaw * 0.017453292F), -MathHelper.cos(player.yaw * 0.017453292F));
                        } else {
                            target.addVelocity(-MathHelper.sin(player.yaw * 0.017453292F) * knockbackModifier * 0.5, 0.1, MathHelper.cos(player.yaw * 0.017453292F) * knockbackModifier * 0.5);
                        }
                    }

                    if (target instanceof PlayerEntityMP && target.velocityChanged) {
                        ((PlayerEntityMP) target).connection.sendPacket(new SPacketEntityVelocity(target));
                        target.velocityChanged = false;
                        target.motionX = motionX;
                        target.motionY = motionY;
                        target.velocityZ() = velocityZ();
                    }

                    if (attackDamageModifier > 0) {
                        player.onEnchantmentCritical(target);
                    }

                    player.setLastAttackedEntity(target);

                    this.applyEnchantments(player, target);

                    if (target instanceof LivingEntity) {
                        final float damageDealt = initialHealth - ((LivingEntity) target).getHealth();
                        player.addStat(StatList.DAMAGE_DEALT, Math.round(damageDealt * 10));

                        if (burnTime > 0) {
                            target.setFire(burnTime);
                        }

                        if (player.world instanceof WorldServer && damageDealt > 2) {
                            ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY() + target.height * 0.5, target.getZ(), (int) (damageDealt * 0.5), 0.1, 0, 0.1, 0.2);
                        }
                    }
                } else {
                    if (burnTime > 0) {
                        target.extinguish();
                    }
                }
            }
        }
    }

    public void shoot(final @Nonnull Entity shooter, final float pitch, final float yaw, final float velocity,
                      final float attackDamageRatio, final float inaccuracy) {
        super.shoot(shooter, pitch, yaw, 0, velocity, inaccuracy);
    }

    @Override
    public void onCollideWithPlayer(@Nonnull final PlayerEntity player) {
        if (!this.world.isClient && player.getUniqueID().equals(this.shooterUUID)
                && this.component != null
                && this.ticksExisted >= Math.max(1, 20 / this.component.getAttribute(DAGGER, ATTACK_SPEED))
                && this.arrowShake <= 0 && (this.pickupStatus == PickupStatus.ALLOWED
                || this.pickupStatus == PickupStatus.CREATIVE_ONLY && player.isCreative())) {
            if (this.isClone) {
                player.onItemPickup(this, 1);
                this.setDead();
            } else if (player.isCreative() && SoulboundItemUtil.hasSoulWeapon(player)) {
                player.onItemPickup(this, 1);
                this.setDead();
            } else if (SoulboundItemUtil.addItemStack(this.getArrowStack(), player, true)) {
                player.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }

    @Override
    public void writeEntityToNBT(@Nonnull final CompoundTag compound) {
        super.writeEntityToNBT(compound);

        compound.setUniqueId("shooterUUID", this.shooterUUID);
        compound.putInt("dimensionID", this.world.provider.getDimension());
        compound.put("itemStack", this.itemStack.toTag());

        this.updateShooter();
    }

    @Override
    public void readEntityFromNBT(@Nonnull final CompoundTag compound) {
        super.readEntityFromNBT(compound);

        this.shooterUUID = compound.getUniqueId("shooterUUID");
        this.world = this.getServer().getWorld(compound.getInt("dimensionID"));
        this.itemStack = new ItemStack(compound.getCompound("itemStack"));

        this.updateShooter();
    }
}
