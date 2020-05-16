package transfarmer.soulboundarmory.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntityMP;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nonnull;
import transfarmer.soulboundarmory.component.entity.IEntityData;
import transfarmer.soulboundarmory.component.soulbound.weapon.IWeaponComponent;

import java.util.UUID;

import static transfarmer.soulboundarmory.skill.Skills.ENDERMANACLE;
import static transfarmer.soulboundarmory.skill.Skills.PENETRATION;
import static transfarmer.soulboundarmory.skill.Skills.VULNERABILITY;
import static transfarmer.soulboundarmory.statistics.Item.STAFF;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_DAMAGE;

public class SoulboundFireballEntity extends SmallFireballEntity {
    protected static final DataParameter<Integer> SPELL = EntityDataManager.createKey(SoulboundFireballEntity.class, DataSerializers.VARINT);

    public PlayerEntity player;
    protected UUID shooterUUID;
    protected IWeaponComponent component;
    protected int ticksSincePacket;
    protected int hitCount;
    private int ticksInAir;

    public SoulboundFireballEntity(final World world) {
        super(world);

        this.dataManager.register(SPELL, 0);
    }

    public SoulboundFireballEntity(final World world, final PlayerEntity shooter, final int spell) {
        this(world);

        this.updatePlayer(shooter, null);
        this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        this.setRotation(shooter.yaw, shooter.pitch);

        final Vec3d look = shooter.getLookVec();

        this.accelerationX = 0.3 * look.x;
        this.accelerationY = 0.3 * look.y;
        this.accelerationZ = 0.3 * look.z;
        this.dataManager.set(SPELL, spell);
    }

    public SoulboundFireballEntity(final EntityType<SoulboundFireballEntity> type, final World world) {
        super(type, world);
    }

    protected void updatePlayer(final PlayerEntity player, final UUID uuid) {
        if (player != null) {
            this.player = player;
            this.shooterUUID = player.getUniqueID();
        } else if (uuid != null) {
            this.shooterUUID = uuid;
            this.player = this.world.getPlayerEntityByUUID(uuid);
        }

        if (this.player != null) {
            this.component = WeaponProvider.get(this.player);
            owner = this.player;
        }
    }

    @Override
    public void onUpdate() {
        if (this.world.isClient || (owner == null || !owner.isDead) && this.world.isBlockLoaded(new BlockPos(this))) {
            if (!this.world.isClient) {
                this.setFlag(6, this.isGlowing());
            }

            this.baseTick();

            if (this.isFireballFiery()) {
                this.setFire(1);
            }

            this.ticksInAir++;
            final RayTraceResult result = ProjectileHelper.forwardsRaycast(this, true, this.ticksInAir >= 25, owner);

            //noinspection ConstantConditions
            if (result != null && !ForgeEventFactory.onProjectileImpact(this, result)) {
                this.onImpact(result);
            }

            this.getX() += this.motionX;
            this.getY() += this.motionY;
            this.getZ() += this.velocityZ();
            ProjectileHelper.rotateTowardsMovement(this, 0.2F);
            float f = this.getMotionFactor();

            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    final double d = 0.25;
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.getX() - this.motionX * d, this.getY() - this.motionY * d, this.getZ() - this.velocityZ() * d, this.motionX, this.motionY, this.velocityZ());
                }

                f = 0.8F;
            }

            this.motionX += this.accelerationX;
            this.motionY += this.accelerationY;
            this.velocityZ() += this.accelerationZ;
            this.motionX *= f;
            this.motionY *= f;
            this.velocityZ() *= f;

            if (this.isFireballFiery()) {
                this.world.spawnParticle(this.getParticleType(), this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
            }

            this.setPosition(this.getX(), this.getY(), this.getZ());
        } else {
            this.setDead();
        }

        this.updatePlayer(this.player, this.shooterUUID);

        if (this.player != null && !this.world.isClient && ++this.ticksSincePacket == 1) {
            ((PlayerEntityMP) this.player).connection.netManager.sendPacket(new SPacketEntityVelocity(this));
            this.ticksSincePacket = 0;
        }
    }

    @Override
    protected void onImpact(@Nonnull final RayTraceResult result) {
        if (!this.world.isClient && this.player != null) {
            final Entity entity = result.entityHit;
            final IWeaponComponent component = WeaponProvider.get(this.player);
            final boolean fiery = this.isFireballFiery();

            if (entity != null) {
                final IEntityData data = EntityDatumProvider.get(entity);
                final Skill endermanacle = component.getSkill(STAFF, ENDERMANACLE);
                final boolean invulnerable = entity.isImmuneToFire() || entity instanceof EntityEnderman;
                final boolean canAttack = invulnerable && this.component.hasSkill(STAFF, VULNERABILITY);
                final DamageSource source = canAttack
                        ? DamageSource.causePlayerDamage(this.player)
                        : fiery
                        ? DamageSource.causeFireballDamage(this, this.player)
                        : DamageSource.causeIndirectMagicDamage(this, this.player);

                if (endermanacle.isLearned() && data != null) {
                    data.blockTeleport(20 * (1 + endermanacle.getLevel()));
                }

                final boolean canBurn = (canAttack || !invulnerable) && fiery;

                if (canBurn) {
                    entity.setFire(1);
                }

                if (entity.attackEntityFrom(source, (float) component.getAttributeTotal(STAFF, ATTACK_DAMAGE))) {
                    this.applyEnchantments(this.player, entity);

                    if (canBurn) {
                        entity.setFire(5);
                    }

                    final Skill penetration = component.getSkill(STAFF, PENETRATION);

                    if (penetration.isLearned() && this.hitCount < penetration.getLevel() + 1) {
                        this.hitCount++;
                    } else {
                        this.setDead();
                    }
                }
            } else {
                if (fiery) {
                    if (ForgeEventFactory.getMobGriefingEvent(this.world, owner)) {
                        final BlockPos pos = result.getBlockPos().offset(result.sideHit);

                        if (this.world.isAirBlock(pos)) {
                            this.world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                        }
                    }
                }

                this.setDead();
            }
        }
    }

    @Override
    protected boolean isFireballFiery() {
        return this.dataManager.get(SPELL) == 1;
    }

    public Item getTextureItem() {
        return this.isFireballFiery() ? Items.FIRE_CHARGE : Items.ENDER_PEARL;
    }

    @Override
    public void writeEntityToNBT(@Nonnull final CompoundTag tag) {
        super.writeEntityToNBT(tag);

        tag.setUniqueId("shooterUUID", this.shooterUUID);
        tag.putInt("spell", this.dataManager.get(SPELL));
    }

    @Override
    public void readEntityFromNBT(@Nonnull final CompoundTag tag) {
        super.readEntityFromNBT(tag);

        this.shooterUUID = tag.getUniqueId("shooterUUID");
        this.dataManager.set(SPELL, tag.getInt("spell"));
    }
}
