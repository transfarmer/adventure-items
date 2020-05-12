package transfarmer.soulboundarmory.component.entity;

import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import transfarmer.soulboundarmory.entity.damage.ExposedDamageSource;

import javax.annotation.Nonnull;

import static transfarmer.soulboundarmory.Main.ENTITY_DATA;

public class EntityData implements IEntityData {
    private final Entity entity;
    private int freezeTicks;
    private int blockTeleportTicks;

    public EntityData(final Entity entity) {
        this.entity = entity;
    }

    @Nonnull
    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public boolean cannotTeleport() {
        return this.blockTeleportTicks > 0;
    }

    @Override
    public void blockTeleport(final int ticks) {
        this.blockTeleportTicks = ticks;
    }

    @Override
    public void freeze(final PlayerEntity freezer, final int ticks, final float damage) {
        if (!freezer.world.isClient) {
            ((ServerWorld) freezer.world).spawnParticles(
                    ParticleTypes.ITEM_SNOWBALL,
                    this.entity.getX(), this.entity.getEyeY(), this.entity.getZ(), 32, 0.1, 0, 0.1, 2D);
            this.entity.playSound(SoundEvents.BLOCK_SNOW_HIT, 1, 1.2F / (this.entity.world.random.nextFloat() * 0.2F + 0.9F));
            this.entity.extinguish();

            if (ticks > this.freezeTicks) {
                this.freezeTicks = ticks;
            }

            if (this.entity instanceof LivingEntity) {
                this.entity.damage(ExposedDamageSource.player(freezer).unblockable.set(true), damage);
            }

            if (this.entity instanceof CreeperEntity) {
                ((CreeperEntity) this.entity).setFuseSpeed(-1);
            }

            if (this.entity instanceof Projectile) {
                this.entity.setVelocity(0, this.entity.getVelocity().y, 0);
            }
        }
    }

    @Override
    public boolean isFrozen() {
        return this.freezeTicks > 0 && this.entity.isAlive() && !this.entity.isOnFire();
    }

    @Override
    public void onUpdate() {
        if (!this.entity.world.isClient) {
            if (this.isFrozen() && this.entity instanceof LivingEntity) {
                final LivingEntity entity = (LivingEntity) this.entity;

                if (entity.hurtTime > 0) {
                    entity.hurtTime--;
                }

                this.freezeTicks--;
            } else {
                this.freezeTicks = 0;
            }

            if (this.cannotTeleport()) {
                this.blockTeleportTicks--;
            }
        }
    }

    @Override
    public ComponentType<?> getComponentType() {
        return ENTITY_DATA;
    }

    public void fromTag(final CompoundTag tag) {
        this.freezeTicks = tag.getInt("freezeTicks");
        this.blockTeleportTicks = tag.getInt("blockTeleportTicks");
    }

    @Nonnull
    @Override
    public CompoundTag toTag(final CompoundTag tag) {
        tag.putInt("freezeTicks", this.freezeTicks);
        tag.putInt("blockTeleportTicks", this.blockTeleportTicks);

        return tag;
    }
}
