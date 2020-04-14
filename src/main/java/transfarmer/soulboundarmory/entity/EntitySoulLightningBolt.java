package transfarmer.soulboundarmory.entity;

import net.minecraft.block.material.*;
import net.minecraft.enchantment.*;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.stats.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.event.*;
import transfarmer.soulboundarmory.capability.soulbound.weapon.*;
import transfarmer.soulboundarmory.util.*;

import java.util.UUID;

import static transfarmer.soulboundarmory.statistics.SoulAttribute.*;
import static transfarmer.soulboundarmory.statistics.SoulEnchantment.*;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType.*;

public class EntitySoulLightningBolt extends EntityLightningBolt {
    private UUID casterUUID;
    private int lightningState;
    private int boltLivingTime;

    public EntitySoulLightningBolt(final World world, final double x, final double y, final double z, final UUID casterUUID) {
        super(world, x, y, z, true);

        this.casterUUID = casterUUID;
        this.lightningState = 2;
    }

    public EntitySoulLightningBolt(final World world, final Vec3d pos, final UUID casterUUID) {
        this(world, pos.x, pos.y, pos.z, casterUUID);
    }

    @Override
    public void onUpdate() {
        if (!this.world.isRemote) {
            this.setFlag(6, this.isGlowing());
        }

        this.onEntityUpdate();

        if (lightningState == 2) {
            this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 15, 0.8F + this.rand.nextFloat() * 0.2F);
            this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.WEATHER, 2, 0.5F + this.rand.nextFloat() * 0.2F);
        }

        if (this.lightningState-- < 0) {
            if (this.boltLivingTime == 0) {
                this.setDead();
            } else if (this.lightningState < -this.rand.nextInt(10)) {
                this.boltLivingTime--;
                this.lightningState = 1;

                if (!this.world.isRemote) {
                    this.boltVertex = this.rand.nextLong();
                }
            }
        }

        if (this.lightningState >= 0) {
            if (this.world.isRemote) {
                this.world.setLastLightningBolt(2);
            } else {
                final BlockPos[] nearby = {
                        this.getPosition(),
                        new BlockPos(this.posX, this.posY - 1, this.posZ),
                };

                boolean obsidianNearby = false;

                for (final BlockPos pos : nearby) {
                    if (this.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN) {
                        obsidianNearby = true;
                    }
                }

                if (obsidianNearby) {
                    for (final BlockPos pos : nearby) {
                        if (this.world.getBlockState(pos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(this.world, pos)) {
                            this.world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                        }
                    }
                }

                final double radius = 3;

                for (final Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this,
                        new AxisAlignedBB(this.posX - radius, this.posY - radius, this.posZ - radius, this.posX + radius, this.posY + 6 + radius, this.posZ + radius))) {
                    final EntityLivingBase caster = this.getCaster();
                    final float attackDamage = caster instanceof EntityPlayer
                            ? SoulWeaponProvider.get(caster).getAttribute(ATTACK_DAMAGE, SWORD, true, true)
                            : 5;

                    if (entity != caster && entity instanceof EntityLivingBase
                            && !ForgeEventFactory.onEntityStruckByLightning(entity, this)) {
                        entity.setFire(1);

                        if (!entity.isBurning()) {
                            this.setFire(8);
                        }

                        if (caster instanceof EntityPlayer) {
                            final EntityLivingBase target = (EntityLivingBase) entity;
                            final ItemStack itemStack = caster.getHeldItemMainhand();
                            final ISoulWeapon capability = SoulWeaponProvider.get(caster);
                            final DamageSource damageSource = SoulboundDamageSource.causeIndirectDamage(this, caster);
                            final float attackDamageModifier = EnchantmentHelper.getModifierForCreature(itemStack, target.getCreatureAttribute());
                            int burnTime = 0;

                            if (attackDamage > 0 || attackDamageModifier > 0) {
                                final int knockbackModifier = EnchantmentHelper.getKnockbackModifier(caster);
                                final float initialHealth = target.getHealth();

                                burnTime += EnchantmentHelper.getFireAspectModifier(caster);

                                if (isBurning()) {
                                    burnTime += 5;
                                }

                                if (capability.getEnchantments(DAGGER).containsKey(SOUL_FIRE_ASPECT) && !(entity instanceof EntityEnderman)) {
                                    burnTime += capability.getEnchantment(SOUL_FIRE_ASPECT, DAGGER) * 4;
                                }

                                if (burnTime > 0 && !entity.isBurning()) {
                                    entity.setFire(1);
                                }

                                if (entity.attackEntityFrom(damageSource, attackDamage)) {
                                    final EntityPlayer player = (EntityPlayer) caster;

                                    if (knockbackModifier > 0) {
                                        target.knockBack(caster, knockbackModifier * 0.5F, MathHelper.sin(caster.rotationYaw * 0.017453292F), -MathHelper.cos(caster.rotationYaw * 0.017453292F));
                                    }

                                    if (attackDamageModifier > 0) {
                                        player.onEnchantmentCritical(entity);
                                    }

                                    caster.setLastAttackedEntity(entity);

                                    EnchantmentHelper.applyThornEnchantments(target, caster);
                                    EnchantmentHelper.applyArthropodEnchantments(caster, entity);

                                    final float damageDealt = initialHealth - target.getHealth();

                                    player.addStat(StatList.DAMAGE_DEALT, Math.round(damageDealt * 10));

                                    if (burnTime > 0) {
                                        entity.setFire(burnTime);
                                    }

                                    if (caster.world instanceof WorldServer && damageDealt > 2) {
                                        final int particles = (int) (damageDealt * 0.5);

                                        ((WorldServer) caster.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, entity.posX, entity.posY + entity.height * 0.5, entity.posZ, particles, 0.1, 0, 0.1, 0.2);
                                    }
                                }
                            }
                        } else {
                            entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, caster), attackDamage);
                        }

                        entity.onStruckByLightning(this);
                    }
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setUniqueId("casterUUID", this.casterUUID);

        return compound;
    }

    @Override
    public void readFromNBT(final NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.casterUUID = compound.getUniqueId("casterUUID");
    }

    public EntityLivingBase getCaster() {
        return this.world.getPlayerEntityByUUID(this.casterUUID);
    }

    public void setCaster(final EntityLivingBase caster) {
        this.casterUUID = caster.getUniqueID();
    }
}
