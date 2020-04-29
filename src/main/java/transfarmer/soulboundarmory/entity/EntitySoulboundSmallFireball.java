package transfarmer.soulboundarmory.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
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
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.capability.entity.EntityDatumProvider;
import transfarmer.soulboundarmory.capability.entity.IEntityData;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeaponCapability;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.skill.SkillLevelable;

import java.util.UUID;

import static transfarmer.soulboundarmory.skill.Skills.ENDERMANACLE;
import static transfarmer.soulboundarmory.skill.Skills.PENETRATION;
import static transfarmer.soulboundarmory.skill.Skills.VULNERABILITY;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.STAFF;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTACK_DAMAGE;

public class EntitySoulboundSmallFireball extends EntitySmallFireball {
    protected static final DataParameter<Integer> SPELL = EntityDataManager.createKey(EntitySoulboundSmallFireball.class, DataSerializers.VARINT);

    public EntityPlayer player;
    protected UUID shooterUUID;
    protected IWeaponCapability capability;
    protected int ticksSincePacket;
    protected int hitCount;
    private int ticksInAir;

    public EntitySoulboundSmallFireball(final World world) {
        super(world);

        this.dataManager.register(SPELL, 0);
    }

    public EntitySoulboundSmallFireball(final World world, final EntityPlayer shooter, final int spell) {
        this(world);

        this.updatePlayer(shooter, null);
        this.setLocationAndAngles(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.setPosition(this.posX, this.posY, this.posZ);

        final Vec3d look = shooter.getLookVec();

        this.accelerationX = 0.15 * look.x;
        this.accelerationY = 0.15 * look.y;
        this.accelerationZ = 0.15 * look.z;
        this.dataManager.set(SPELL, spell);
    }

    protected void updatePlayer(final EntityPlayer player, final UUID uuid) {
        if (player != null) {
            this.player = player;
            this.shooterUUID = player.getUniqueID();
        } else if (uuid != null) {
            this.shooterUUID = uuid;
            this.player = this.world.getPlayerEntityByUUID(uuid);
        }

        if (this.player != null) {
            this.capability = WeaponProvider.get(this.player);
            this.shootingEntity = this.player;
        }
    }

    @Override
    public void onUpdate() {
        if (this.world.isRemote || (this.shootingEntity == null || !this.shootingEntity.isDead) && this.world.isBlockLoaded(new BlockPos(this))) {
            if (!this.world.isRemote) {
                this.setFlag(6, this.isGlowing());
            }

            this.onEntityUpdate();

            if (this.isFireballFiery()) {
                this.setFire(1);
            }

            this.ticksInAir++;
            final RayTraceResult result = ProjectileHelper.forwardsRaycast(this, true, this.ticksInAir >= 25, this.shootingEntity);

            //noinspection ConstantConditions
            if (result != null && !ForgeEventFactory.onProjectileImpact(this, result)) {
                this.onImpact(result);
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            ProjectileHelper.rotateTowardsMovement(this, 0.2F);
            float f = this.getMotionFactor();

            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    final double d = 0.25;
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * d, this.posY - this.motionY * d, this.posZ - this.motionZ * d, this.motionX, this.motionY, this.motionZ);
                }

                f = 0.8F;
            }

            this.motionX += this.accelerationX;
            this.motionY += this.accelerationY;
            this.motionZ += this.accelerationZ;
            this.motionX *= f;
            this.motionY *= f;
            this.motionZ *= f;

            if (this.isFireballFiery()) {
                this.world.spawnParticle(this.getParticleType(), this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
            }

            this.setPosition(this.posX, this.posY, this.posZ);
        } else {
            this.setDead();
        }

        this.updatePlayer(this.player, this.shooterUUID);

        if (this.player != null && !this.world.isRemote && ++this.ticksSincePacket == 1) {
            ((EntityPlayerMP) this.player).connection.netManager.sendPacket(new SPacketEntityVelocity(this));
            this.ticksSincePacket = 0;
        }
    }

    @Override
    protected void onImpact(@NotNull final RayTraceResult result) {
        if (!this.world.isRemote && this.player != null) {
            final Entity entity = result.entityHit;
            final IWeaponCapability capability = WeaponProvider.get(this.player);
            final boolean fiery = this.isFireballFiery();

            if (entity != null) {
                final IEntityData data = EntityDatumProvider.get(entity);
                final SkillLevelable endermanacle = capability.getSkillLevelable(STAFF, ENDERMANACLE);
                final boolean invulnerable = entity.isImmuneToFire() || entity instanceof EntityEnderman;
                final boolean canAttack = invulnerable && this.capability.hasSkill(STAFF, VULNERABILITY);
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

                if (entity.attackEntityFrom(source, (float) capability.getAttributeTotal(STAFF, ATTACK_DAMAGE))) {
                    this.applyEnchantments(this.player, entity);

                    if (canBurn) {
                        entity.setFire(5);
                    }

                    final SkillLevelable penetration = capability.getSkillLevelable(STAFF, PENETRATION);

                    if (penetration.isLearned() && this.hitCount < penetration.getLevel() + 1) {
                        this.hitCount++;
                    } else {
                        this.setDead();
                    }
                }
            } else {
                if (fiery) {
                    if (ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity)) {
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
    public void writeEntityToNBT(@NotNull final NBTTagCompound tag) {
        super.writeEntityToNBT(tag);

        tag.setUniqueId("shooterUUID", this.shooterUUID);
        tag.setInteger("spell", this.dataManager.get(SPELL));
    }

    @Override
    public void readEntityFromNBT(@NotNull final NBTTagCompound tag) {
        super.readEntityFromNBT(tag);

        this.shooterUUID = tag.getUniqueId("shooterUUID");
        this.dataManager.set(SPELL, tag.getInteger("spell"));
    }
}
