package user11681.soulboundarmory.entity;

import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import user11681.soulboundarmory.component.soulbound.item.weapon.SwordStorage;
import user11681.usersmanual.reflect.FieldWrapper;

import java.util.UUID;

import static user11681.soulboundarmory.component.statistics.StatisticType.ATTACK_DAMAGE;

public class SoulboundLightningEntity extends LightningEntity {
    protected final FieldWrapper<Integer, SoulboundLightningEntity> ambientTick;
    protected final FieldWrapper<Integer, SoulboundLightningEntity> remainingActions;

    protected UUID casterUUID;

    public SoulboundLightningEntity(final World world) {
        super(world, 0, 0, 0, true);

        this.ambientTick = new FieldWrapper<>(LightningEntity.class, this, "ambientTick");
        this.remainingActions = new FieldWrapper<>(LightningEntity.class, this, "remainingActions");
    }

    public SoulboundLightningEntity(final World world, final double x, final double y, final double z,
                                    final UUID casterUUID) {
        super(world, x, y, z, true);

        this.casterUUID = casterUUID;
        this.ambientTick = new FieldWrapper<>(LightningEntity.class, this, "ambientTick");
        this.remainingActions = new FieldWrapper<>(LightningEntity.class, this, "remainingActions");

        ((NetherPortalBlock) Blocks.NETHER_PORTAL).createPortalAt(world, this.getBlockPos());
    }

    public SoulboundLightningEntity(final World world, final Vec3d pos, final UUID casterUUID) {
        this(world, pos.x, pos.y, pos.z, casterUUID);
    }

    @Override
    public void tick() {
        if (!this.world.isClient) {
            this.setFlag(6, this.isGlowing());
        }

        this.baseTick();

        if (this.ambientTick.get() == 2) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 15, 0.8F + this.random.nextFloat() * 0.2F);
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2, 0.5F + this.random.nextFloat() * 0.2F);
        }

        this.ambientTick.set(this.ambientTick.get() - 1);

        if (this.ambientTick.get() < 0) {
            if (this.remainingActions.get() == 0) {
                this.remove();
            } else if (this.ambientTick.get() < -this.random.nextInt(10)) {
                this.remainingActions.set(this.remainingActions.get() - 1);
                this.ambientTick.set(1);
                this.seed = this.random.nextLong();
            }
        }

        if (this.ambientTick.get() >= 0) {
            if (this.world.isClient) {
                this.world.setLightningTicksLeft(2);
            } else {
                final double radius = 3;

                for (final Entity entity : this.world.getEntities(this,
                        new Box(this.getX() - radius, this.getY() - radius, this.getZ() - radius, this.getX() + radius, this.getY() + 6 + radius, this.getZ() + radius))) {
                    final LivingEntity caster = this.getCaster();
                    final float attackDamage = caster instanceof PlayerEntity
                            ? (float) SwordStorage.get(caster).getAttributeTotal(ATTACK_DAMAGE)
                            : 5;

                    if (entity != caster && entity instanceof LivingEntity) {
                        entity.onStruckByLightning(this);

                        entity.setFireTicks(1);

                        if (!entity.isOnFire()) {
                            this.setFireTicks(160);
                        }

                        if (caster instanceof PlayerEntity) {
                            final LivingEntity target = (LivingEntity) entity;
                            final ItemStack itemStack = caster.getMainHandStack();
                            final DamageSource damageSource = DamageSource.explosion(caster);
                            final float attackDamageModifier = EnchantmentHelper.getAttackDamage(itemStack, target.getGroup());
                            int burnTime = 0;

                            if (attackDamage > 0 || attackDamageModifier > 0) {
                                final int knockbackModifier = EnchantmentHelper.getKnockback(caster);
                                final float initialHealth = target.getHealth();

                                burnTime += 4 * EnchantmentHelper.getFireAspect(caster);

                                if (!caster.isOnFire()) {
                                    burnTime += 5;
                                }

                                if (burnTime > 0 && !entity.isOnFire()) {
                                    entity.setFireTicks(20);
                                }

                                if (entity.damage(damageSource, attackDamage)) {
                                    final PlayerEntity player = (PlayerEntity) caster;

                                    if (knockbackModifier > 0) {
                                        target.takeKnockback(caster, knockbackModifier * 0.5F, MathHelper.sin(caster.yaw * 0.017453292F), -MathHelper.cos(caster.yaw * 0.017453292F));
                                    }

                                    if (attackDamageModifier > 0) {
                                        player.addCritParticles(entity);
                                    }

                                    ((LivingEntity) entity).setAttacker(caster);

                                    EnchantmentHelper.onTargetDamaged(caster, target);

                                    final float damageDealt = initialHealth - target.getHealth();

                                    if (burnTime > 0) {
                                        entity.setOnFireFor(burnTime);
                                    }

                                    if (caster.world instanceof ServerWorld && damageDealt > 2) {
                                        final int particles = (int) (damageDealt * 0.5);

                                        ((ServerWorld) caster.world).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY() + entity.getHeight() * 0.5, entity.getZ(), particles, 0.1, 0, 0.1, 0.2);
                                    }
                                }
                            }
                        } else {
                            entity.damage(DamageSource.explosion(caster), attackDamage);
                        }

                        entity.onStruckByLightning(this);
                    }
                }
            }
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag compound) {
        compound = super.toTag(compound);
        compound.putUuid("casterUUID", this.casterUUID);

        return compound;
    }

    @Override
    public void fromTag(final CompoundTag compound) {
        super.fromTag(compound);

        this.casterUUID = compound.getUuid("casterUUID");
    }

    public LivingEntity getCaster() {
        return this.world.getPlayerByUuid(this.casterUUID);
    }

    public void setCaster(final LivingEntity caster) {
        this.casterUUID = caster.getUuid();
    }
}
