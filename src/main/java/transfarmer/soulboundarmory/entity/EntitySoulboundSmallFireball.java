package transfarmer.soulboundarmory.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.capability.entity.EntityDatumProvider;
import transfarmer.soulboundarmory.capability.entity.IEntityData;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeaponCapability;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.skill.SkillLevelable;

import java.util.UUID;

import static transfarmer.soulboundarmory.skill.Skills.ENDERMANACLE;
import static transfarmer.soulboundarmory.skill.Skills.PENETRATION;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.STAFF;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTACK_DAMAGE;

public class EntitySoulboundSmallFireball extends EntitySmallFireball {
    protected static final DataParameter<Integer> SPELL = EntityDataManager.createKey(EntitySoulboundSmallFireball.class, DataSerializers.VARINT);

    public EntityPlayer player;
    protected UUID shooterUUID;
    protected IWeaponCapability capability;
    protected int ticksSincePacket;
    protected int hitCount;

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
        super.onUpdate();

        this.updatePlayer(null, this.shooterUUID);

        if (this.player != null && !this.world.isRemote && ++this.ticksSincePacket == 2) {
            ((EntityPlayerMP) this.player).connection.netManager.sendPacket(new SPacketEntityVelocity(this));
            this.ticksSincePacket = 0;
        }
    }

    @Override
    protected void onImpact(@NotNull final RayTraceResult result) {
        if (!this.world.isRemote && this.player != null) {
            final Entity entity = result.entityHit;

            final IWeaponCapability capability = WeaponProvider.get(this.player);

            if (entity != null) {
                final IEntityData data = EntityDatumProvider.get(entity);
                final SkillLevelable endermanacle = capability.getSkillLevelable(STAFF, ENDERMANACLE);

                if (endermanacle.isLearned() && data != null) {
                    data.blockTeleport(20 * (1 + endermanacle.getLevel()));
                }

                if (!entity.isImmuneToFire()) {
                    if (entity instanceof EntityEnderman) {
                        final EntityEnderman enderman = (EntityEnderman) entity;
                        final DamageSource damageSource = DamageSource.causePlayerDamage(this.player);
                        this.hitCount++;

                        enderman.setFire(5);
                        enderman.attackEntityFrom(damageSource, 0);
                        enderman.setAttackTarget(this.player);
                        enderman.getCombatTracker().trackDamage(damageSource, 0, 0);
                    } else {
                        final boolean fiery = this.isFireballFiery();

                        if (fiery) {
                            entity.setFire(1);
                        }

                        if (entity.attackEntityFrom(DamageSource.causeFireballDamage(this, this.player), (float) capability.getAttribute(STAFF, ATTACK_DAMAGE))) {
                            this.applyEnchantments(this.player, entity);

                            if (fiery) {
                                entity.setFire(5);
                            }

                            final SkillLevelable penetration = capability.getSkillLevelable(STAFF, PENETRATION);

                            if (penetration.isLearned() && this.hitCount < penetration.getLevel() + 1) {
                                this.hitCount++;
                            } else {
                                this.setDead();
                            }
                        }
                    }
                    //            } else {
                    //                if (ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity)) {
                    //                    final BlockPos pos = result.getBlockPos().offset(result.sideHit);
                    //
                    //                    if (this.world.isAirBlock(pos)) {
                    //                        this.world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                    //                    }
                    //                }
                }
            } else {
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
        tag.setInteger("entityID", this.getEntityId());
    }

    @Override
    public void readEntityFromNBT(@NotNull final NBTTagCompound tag) {
        super.readEntityFromNBT(tag);

        this.shooterUUID = tag.getUniqueId("shooterUUID");
        this.dataManager.set(SPELL, tag.getInteger("spell"));
    }
}
