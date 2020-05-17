package transfarmer.soulboundarmory.entity;

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
import transfarmer.soulboundarmory.component.entity.IEntityData;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;
import transfarmer.soulboundarmory.component.soulbound.item.IStaffComponent;
import transfarmer.soulboundarmory.skill.SkillContainer;

import java.util.Optional;

import static net.minecraft.util.hit.HitResult.Type.ENTITY;
import static transfarmer.soulboundarmory.Main.SOULBOUND_FIREBALL_ENTITY;
import static transfarmer.soulboundarmory.Main.SOULBOUND_STAFF_ITEM;
import static transfarmer.soulboundarmory.skill.Skills.ENDERMANACLE;
import static transfarmer.soulboundarmory.skill.Skills.PENETRATION;
import static transfarmer.soulboundarmory.skill.Skills.VULNERABILITY;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_DAMAGE;

public class SoulboundFireballEntity extends SmallFireballEntity {
    protected IStaffComponent component;
    protected int ticksSincePacket;
    protected int hitCount;
    protected int ticksInAir;
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
            this.component = (IStaffComponent) ISoulboundItemComponent.get(this.owner, SOULBOUND_STAFF_ITEM);
        }
    }

    @Override
    protected void onCollision(final HitResult result) {
        if (!this.world.isClient && result.getType() == ENTITY && this.owner != null) {
            final Entity entity = ((EntityHitResult) result).getEntity();
            final boolean fiery = this.isBurning();

            if (entity != null) {
                final Optional<IEntityData> data = IEntityData.maybeGet(entity);
                final SkillContainer endermanacle = component.getSkill(ENDERMANACLE);
                final boolean invulnerable = entity.isFireImmune() || entity instanceof EndermanEntity;
                final boolean canAttack = invulnerable && this.component.hasSkill(VULNERABILITY);
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

                if (entity.damage(source, (float) component.getAttributeTotal(ATTACK_DAMAGE))) {
                    EnchantmentHelper.onTargetDamaged(this.owner, entity);

                    if (canBurn) {
                        entity.setFireTicks(100);
                    }

                    final SkillContainer penetration = component.getSkill(PENETRATION);

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
