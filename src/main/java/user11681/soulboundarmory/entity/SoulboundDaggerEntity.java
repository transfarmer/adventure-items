package user11681.soulboundarmory.entity;

import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.component.soulbound.item.weapon.DaggerStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundItemUtil;
import user11681.soulboundarmory.skill.Skills;

import static net.minecraft.entity.projectile.ProjectileEntity.PickupPermission.ALLOWED;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTACK_DAMAGE;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTACK_SPEED;
import static user11681.soulboundarmory.entity.EntityTypes.SOULBOUND_DAGGER_ENTITY;

public class SoulboundDaggerEntity extends ExtendedProjectile {
    public ItemStack itemStack;
    protected boolean spawnClone;
    protected boolean isClone;
    protected double attackDamageRatio;
    protected int ticksToSeek;
    protected int ticksSeeking;
    protected int ticksInGround;
    protected DaggerStorage storage;

    public SoulboundDaggerEntity(final World world) {
        super(world);
    }

    public SoulboundDaggerEntity(final SoulboundDaggerEntity original, final boolean spawnClone) {
        this(original.world, original.getOwner(), original.itemStack, spawnClone, original.getVelocityD(), original.getVelocityD());

        this.isClone = true;
        this.setPos(original.getX(), original.getY(), original.getZ());
        this.setVelocity(original.getVelocity());
        this.pitch = original.pitch;
        this.yaw = original.yaw;
        this.prevPitch = original.prevPitch;
        this.prevYaw = original.prevYaw;
        this.attackDamageRatio = original.attackDamageRatio;
    }

    public SoulboundDaggerEntity(final World world, final Entity shooter, final ItemStack itemStack,
                                 final boolean spawnClone, final double velocity, final double maxVelocity) {
        super(SOULBOUND_DAGGER_ENTITY, shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ(), world);

        this.ticksToSeek = -1;

        if (shooter instanceof PlayerEntity) {
            this.pickupType = ALLOWED;
        }

        this.itemStack = itemStack;
        this.spawnClone = spawnClone;

        this.attackDamageRatio = velocity / maxVelocity;

        this.updateShooter();
        this.setDamage();
    }

    public <T extends ProjectileEntity> SoulboundDaggerEntity(final EntityType<T> entityType, final World world) {
        super(entityType, world);
    }

    public void setDamage() {
        this.setDamage(this.storage.getAttribute(ATTACK_DAMAGE) * this.attackDamageRatio);
    }

    @Override
    @Nonnull
    protected ItemStack asItemStack() {
        return this.itemStack != null
                ? this.itemStack
                : this.getOwner() instanceof PlayerEntity
                ? this.storage.getItemStack()
                : ItemStack.EMPTY;
    }

    protected void updateShooter() {
        final Entity owner = this.getOwner();

        if (owner instanceof PlayerEntity) {
            this.storage = Components.WEAPON_COMPONENT.get(owner).getStorage(StorageType.DAGGER_STORAGE);
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
            final double attackSpeed = this.storage.getAttribute(ATTACK_SPEED);

            if (this.storage.hasSkill(Skills.SNEAK_RETURN) && owner.isSneaking()
                    && this.getLife() >= 60 / attackSpeed
                    || this.storage.hasSkill(Skills.RETURN)
                    && (this.getLife() >= 300
                    || this.ticksInGround > 20 / attackSpeed
                    || owner.distanceTo(this) >= 16 * (this.world.getServer().getPlayerManager().getViewDistance() - 1)
                    || this.getY() <= 0)
                    || this.ticksSeeking > 0) {
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
            this.setVelocity(this.getVelocity().multiply(-0.1));
            this.yaw += 180;
            this.prevYaw += 180;
        }

        if (owner instanceof PlayerEntity) {
            final PlayerEntity player = (PlayerEntity) owner;
            final DamageSource damageSource = DamageSource.thrownProjectile(this, owner);
            final double attackDamage = this.storage.getAttribute(ATTACK_DAMAGE);

            int burnTime = 0;

            if (attackDamage > 0) {
                final int knockbackModifier = EnchantmentHelper.getKnockback(player);
                final float initialHealth = target.getHealth();

                burnTime += EnchantmentHelper.getFireAspect(player) * 80;

                if (this.isOnFire()) {
                    burnTime += 100;
                }

                if (burnTime > 0 && !target.isOnFire()) {
                    target.setFireTicks(1);
                }

                final Vec3d velocity = target.getVelocity();
                final double velocityX = velocity.x;
                final double velocityY = velocity.y;
                final double velocityZ = velocity.z;

                if (target.damage(damageSource, (float) attackDamage)) {
                    if (knockbackModifier > 0) {
                        target.takeKnockback(player, knockbackModifier * 0.5F, MathHelper.sin(player.yaw * 0.017453292F), -MathHelper.cos(player.yaw * 0.017453292F));
                    }
                }

                if (!target.world.isClient && target.velocityModified) {
                    ((ServerPlayerEntity) target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                    target.velocityModified = false;
                    this.setVelocity(velocityX, velocityY, velocityZ);
                }

                if (attackDamage > 0) {
                    player.addCritParticles(target);
                }

                player.setAttacker(target);

                EnchantmentHelper.onUserDamaged(target, player);

                final float damageDealt = initialHealth - target.getHealth();
                player.increaseStat(Stats.DAMAGE_DEALT, Math.round(damageDealt * 10));

                if (burnTime > 0) {
                    target.setFireTicks(burnTime);
                }

                if (!player.world.isClient && damageDealt > 2) {
                    ((ServerWorld) player.world).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY() + target.getHeight() * 0.5, target.getZ(), (int) (damageDealt * 0.5), 0.1, 0, 0.1, 0.2);
                }
            }
        }
    }

    @Override
    public void onPlayerCollision(@Nonnull final PlayerEntity player) {
        if (!this.world.isClient && player.getUuid().equals(this.ownerUuid)
                && (this.isClone
                || this.storage != null
                && this.getLife() >= Math.max(1, 20 / this.storage.getAttribute(ATTACK_SPEED))
                && (player.isCreative()
                && (SoulboundItemUtil.hasSoulWeapon(player)))
                || SoulboundItemUtil.addItemStack(this.asItemStack(), player, true))) {
            player.sendPickup(this, 1);
            this.remove();
        }
    }

    @Override
    public CompoundTag toTag(@Nonnull final CompoundTag tag) {
        super.toTag(tag);

        tag.putUuid("shooterUUID", this.ownerUuid);
        tag.put("itemStack", this.itemStack.toTag(new CompoundTag()));

        this.updateShooter();
        return tag;
    }

    @Override
    public void fromTag(@Nonnull final CompoundTag tag) {
        super.fromTag(tag);

        this.ownerUuid = tag.getUuid("shooterUUID");
        this.itemStack = ItemStack.fromTag(tag.getCompound("itemStack"));

        this.updateShooter();
    }
}
