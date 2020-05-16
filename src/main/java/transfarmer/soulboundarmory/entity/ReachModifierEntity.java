package transfarmer.soulboundarmory.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntityMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import transfarmer.soulboundarmory.component.soulbound.weapon.IWeaponComponent;

public class ReachModifierEntity extends ExtendedProjectile {
    private float reachDistance;

    public ReachModifierEntity(final World worldIn) {
        super(worldIn);
    }

    public ReachModifierEntity(final World world, final double x, final double y, final double z) {
        super(world, x, y, z);
    }

    public ReachModifierEntity(final World world, final LivingEntity shooter, final float reachDistance) {
        this(world, shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());

        this.world = world;
        this.ownerUuid = shooter.getUuid();
        this.reachDistance = reachDistance;
        this.setBoundingBox(new Box(0, 0, 0, 0, 0, 0));
    }

    @Override
    protected ItemStack asItemStack() {
        return null;
    }

    public void tick() {
        final Vec3d pos = this.getPos();

        Vec3d newPos = new Vec3d(this.getX() + this.motionX, this.getY() + this.motionY, this.getZ() + this.velocityZ());
        RayTraceContext rayTraceResult = this.world.rayTraceBlocks(pos, newPos, false, true, false);

        if (rayTraceResult != null) {
            newPos = new Vec3d(rayTraceResult.hitVec.x, rayTraceResult.hitVec.y, rayTraceResult.hitVec.z);
        }

        final Entity entity = this.findEntityOnPath(pos, newPos);

        if (entity != null) {
            rayTraceResult = new RayTraceResult(entity);
        }

        if (rayTraceResult != null && rayTraceResult.entityHit instanceof PlayerEntity) {

            if (owner instanceof PlayerEntity
                    && !((PlayerEntity) owner).canAttackPlayer((PlayerEntity) rayTraceResult.entityHit)) {
                rayTraceResult = null;
            }
        }

        if (rayTraceResult != null && !ForgeEventFactory.onProjectileImpact(this, rayTraceResult)) {
            this.onHit(rayTraceResult);
        }

        this.setPosition(this.getX(), this.getY(), this.getZ());
        this.doBlockCollisions();
    }

    @Override
    protected void onHit(final RayTraceResult result) {
        if (!this.world.isClient && result.entityHit != owner && owner instanceof PlayerEntity) {
            final Entity target = result.entityHit;
            final PlayerEntity player = (PlayerEntity) owner;
            final IWeaponComponent component = WeaponProvider.get(player);

            if (target != null) {
                if (this.distanceToHit(result) <= this.reachDistance * this.reachDistance
                    && ForgeHooks.onPlayerAttackTarget(player, target) && target.canBeAttackedWithItem() && !target.hitByEntity(player)) {

                    float attackDamageModifier = (float) player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                    float attackDamageRatio = target instanceof LivingEntity
                        ? EnchantmentHelper.getModifierForCreature(player.getMainHandStack(), ((LivingEntity) target).getCreatureAttribute())
                        : EnchantmentHelper.getModifierForCreature(player.getMainHandStack(), EnumCreatureAttribute.UNDEFINED);

                    final double cooldownRatio = component.getAttackRatio(component.getItemType());
                    attackDamageModifier *= 0.2 + cooldownRatio * cooldownRatio * 0.8;
                    attackDamageRatio *= cooldownRatio;

                    if (attackDamageModifier > 0 || attackDamageRatio > 0) {
                        final boolean strong = cooldownRatio > 0.9F;
                        final boolean knockback = player.isSprinting() && strong;
                        int knockbackModifier = EnchantmentHelper.getKnockbackModifier(player);

                        if (knockback) {
                            player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1, 1);
                            knockbackModifier++;
                        }

                        boolean critical = strong && player.fallDistance > 0 && !player.onGround && !player.isOnLadder()
                            && !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding()
                            && target instanceof LivingEntity && !player.isSprinting();

                        final CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(player, target, critical, critical ? 1.5F : 1);
                        critical = hitResult != null;

                        if (critical) {
                            attackDamageModifier *= hitResult.getDamageModifier();
                        }

                        attackDamageModifier += attackDamageRatio;
                        final double speed = player.distanceWalkedModified - player.prevDistanceWalkedModified;
                        final boolean sweep = strong && !critical && !knockback && player.onGround && speed < player.getAIMoveSpeed() && player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ItemSword;
                        final int fireAspectModifier = EnchantmentHelper.getFireAspectModifier(player);
                        float initialHealth = 0;
                        boolean burn = false;

                        if (target instanceof LivingEntity) {
                            initialHealth = ((LivingEntity) target).getHealth();

                            if (fireAspectModifier > 0 && !target.isBurning()) {
                                burn = true;
                                target.setFire(1);
                            }
                        }

                        final double motionX = target.motionX;
                        final double motionY = target.motionY;
                        final double velocityZ() = target.velocityZ();

                        if (target.attackEntityFrom(DamageSource.causePlayerDamage(player), attackDamageModifier)) {
                            if (knockbackModifier > 0) {
                                if (target instanceof LivingEntity) {
                                    ((LivingEntity) target).knockBack(player, knockbackModifier * 0.5F, MathHelper.sin(player.yaw * 0.017453292F), -MathHelper.cos(player.yaw * 0.017453292F));
                                } else {
                                    target.addVelocity(-MathHelper.sin(player.yaw * 0.017453292F) * knockbackModifier * 0.5F, 0.1D, MathHelper.cos(player.yaw * 0.017453292F) * knockbackModifier * 0.5F);
                                }

                                player.motionX *= 0.6D;
                                player.velocityZ() *= 0.6D;
                                player.setSprinting(false);
                            }

                            if (sweep) {
                                float attackDamage = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * attackDamageModifier;

                                for (final LivingEntity entity : player.world.getEntitiesWithinAABB(LivingEntity.class, target.getEntityBoundingBox())) {
                                    if (entity != player && entity != target && !player.isOnSameTeam(entity) && player.getDistanceSq(entity) < this.reachDistance * this.reachDistance) {
                                        entity.knockBack(player, 0.4F, MathHelper.sin(player.yaw * 0.017453292F), -MathHelper.cos(player.yaw * 0.017453292F));
                                        entity.attackEntityFrom(DamageSource.causePlayerDamage(player), attackDamage);
                                    }
                                }

                                player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1, 1);
                                player.spawnSweepParticles();
                            }

                            if (target instanceof PlayerEntityMP && target.velocityChanged) {
                                ((PlayerEntityMP) target).connection.sendPacket(new SPacketEntityVelocity(target));
                                target.velocityChanged = false;
                                target.motionX = motionX;
                                target.motionY = motionY;
                                target.velocityZ() = velocityZ();
                            }

                            if (critical) {
                                player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1, 1);
                                player.onCriticalHit(target);
                            }

                            if (!critical && !sweep) {
                                if (strong) {
                                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1, 1);
                                } else {
                                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1, 1);
                                }
                            }

                            if (attackDamageRatio > 0) {
                                player.onEnchantmentCritical(target);
                            }

                            player.setLastAttackedEntity(target);

                            if (target instanceof LivingEntity) {
                                EnchantmentHelper.applyThornEnchantments((LivingEntity) target, player);
                            }

                            EnchantmentHelper.applyArthropodEnchantments(player, target);

                            if (target instanceof LivingEntity) {
                                float damageDealt = initialHealth - ((LivingEntity) target).getHealth();
                                player.addStat(StatList.DAMAGE_DEALT, Math.round(damageDealt * 10.0F));

                                if (fireAspectModifier > 0) {
                                    target.setFire(fireAspectModifier * 4);
                                }

                                if (player.world instanceof WorldServer && damageDealt > 2.0F) {
                                    int k = (int) ((double) damageDealt * 0.5D);
                                    ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY() + (double) (target.height * 0.5F), target.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                                }
                            }

                            player.addExhaustion(0.1F);
                        } else {
                            player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 1.0F);

                            if (burn) {
                                target.extinguish();
                            }
                        }
                    }
                }
            }

            component.resetCooldown(component.getItemType());
        }

        this.setDead();
    }

    public void shoot(final double x, final double y, final double z) {
        this.motionX = x * 255;
        this.motionY = y * 255;
        this.velocityZ() = z * 255;
    }

    private double distanceToHit(final RayTraceResult rayTraceResult) {
        final Vec3d pos = rayTraceResult.hitVec;

        return Math.pow(pos.x - this.getX(), 2) + Math.pow(pos.y - this.getY(), 2) + Math.pow(pos.z - this.getZ(), 2);
    }
}
