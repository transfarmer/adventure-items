package user11681.soulboundarmory.entity;

import java.util.Optional;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.entity.IEntityData;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.skill.SkillContainer;
import user11681.soulboundarmory.skill.Skills;

import static net.minecraft.util.hit.HitResult.Type.ENTITY;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTACK_DAMAGE;
import static user11681.soulboundarmory.entity.EntityTypes.SOULBOUND_FIREBALL_ENTITY;

public class SoulboundFireballEntity extends SmallFireballEntity {
    protected StaffStorage storage;
    protected int hitCount;
    protected int spell;

    public SoulboundFireballEntity(final World world) {
        super(SOULBOUND_FIREBALL_ENTITY, world);
    }

    public SoulboundFireballEntity(final World world, final PlayerEntity shooter, final int spell) {
        this(world);

        this.updatePlayer();
        this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        this.setRotation(shooter.yaw, shooter.pitch);
        this.setVelocity(shooter.getRotationVector().multiply(1.5));
    }

    public SoulboundFireballEntity(final EntityType<SoulboundFireballEntity> type, final World world) {
        super(type, world);
    }

    protected void updatePlayer() {
        if (this.owner != null) {
            this.storage = Components.WEAPON_COMPONENT.get(this.owner).getStorage(StorageType.STAFF_STORAGE);
        }
    }

    @Override
    protected void onCollision(final HitResult result) {
        if (!this.world.isClient && result.getType() == ENTITY && this.owner != null) {
            final Entity entity = ((EntityHitResult) result).getEntity();
            final boolean fiery = this.isBurning();

            if (entity != null) {
                final Optional<IEntityData> data = IEntityData.maybeGet(entity);
                final SkillContainer endermanacle = storage.getSkill(Skills.ENDERMANACLE);
                final boolean invulnerable = entity.isFireImmune() || entity instanceof EndermanEntity;
                final boolean canAttack = invulnerable && this.storage.hasSkill(Skills.VULNERABILITY);
                final DamageSource source = canAttack
                        ? DamageSource.player((PlayerEntity) this.owner)
                        : fiery
                        ? DamageSource.explosiveProjectile(this, this.owner)
                        : DamageSource.explosiveProjectile(this, this.owner);

                if (endermanacle.isLearned() && data.isPresent()) {
                    data.get().blockTeleport(20 * (1 + endermanacle.getLevel()));
                }

                final boolean canBurn = (canAttack || !invulnerable) && fiery;

                if (canBurn) {
                    entity.setFireTicks(20);
                }

                if (entity.damage(source, (float) storage.getAttributeTotal(ATTACK_DAMAGE))) {
                    EnchantmentHelper.onTargetDamaged(this.owner, entity);

                    if (canBurn) {
                        entity.setFireTicks(100);
                    }

                    final SkillContainer penetration = storage.getSkill(Skills.PENETRATION);

                    if (penetration.isLearned() && this.hitCount < penetration.getLevel() + 1) {
                        this.hitCount++;
                    } else {
                        this.remove();
                    }
                }
            } else {
                this.remove();
            }
        }
    }

    @Override
    protected boolean isBurning() {
        return this.spell == 1;
    }

    public Item getTextureItem() {
        return this.isBurning() ? Items.FIRE_CHARGE : Items.ENDER_PEARL;
    }

    @Override
    public CompoundTag toTag(final CompoundTag tag) {
        super.toTag(tag);

        tag.putUuid("owner_uuid", this.owner.getUuid());
        tag.putInt("spell", this.spell);

        return tag;
    }

    @Override
    public void fromTag(final CompoundTag tag) {
        super.fromTag(tag);

        this.owner = this.world.getPlayerByUuid(tag.getUuid("shooterUUID"));
        this.spell = tag.getInt("spell");
    }
}
