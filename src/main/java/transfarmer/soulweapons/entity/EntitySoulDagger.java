package transfarmer.soulweapons.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.init.ModItems;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.ATTACK_DAMAGE;
import static transfarmer.soulweapons.data.SoulWeaponEnchantment.FIRE_ASPECT;
import static transfarmer.soulweapons.data.SoulWeaponEnchantment.SHARPNESS;
import static transfarmer.soulweapons.data.SoulWeaponType.DAGGER;

public class EntitySoulDagger extends EntityArrow {
    private int xTile;
    private int yTile;
    private int zTile;
    private int inData;
    private Block inTile;

    public EntitySoulDagger(World world) {
        super(world);
    }

    public EntitySoulDagger(World world, EntityLivingBase thrower) {
        super(world, thrower);
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(ModItems.SOUL_DAGGER);
    }

    public EntitySoulDagger(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                this.world.spawnParticle(EnumParticleTypes.CRIT, this.posX, this.posY, this.posZ, 0, 0, 0);
            }
        }
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (result.entityHit != null) {
            if (this.shootingEntity instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer) this.shootingEntity;
                final ISoulWeapon capability = player.getCapability(CAPABILITY, null);
                float attackDamage = capability.getAttribute(ATTACK_DAMAGE, DAGGER) + 1;

                if (capability.getEnchantments(DAGGER).containsKey(SHARPNESS)) {
                    attackDamage += 1 + (capability.getEnchantment(SHARPNESS, DAGGER) - 1) / 2F;
                }

                result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(player, this), attackDamage);

                if (result.entityHit instanceof EntityLivingBase) {
                    final EntityLivingBase entity = (EntityLivingBase) result.entityHit;

                    if (capability.getEnchantments(DAGGER).containsKey(FIRE_ASPECT) && !(entity instanceof EntityEnderman)) {
                        result.entityHit.setFire(capability.getEnchantment(FIRE_ASPECT, DAGGER) * 4);
                    }
                }
            }
        } else {
            BlockPos blockpos = result.getBlockPos();
            this.xTile = blockpos.getX();
            this.yTile = blockpos.getY();
            this.zTile = blockpos.getZ();
            IBlockState iblockstate = this.world.getBlockState(blockpos);
            this.inTile = iblockstate.getBlock();
            this.inData = this.inTile.getMetaFromState(iblockstate);
            this.motionX = result.hitVec.x - this.posX;
            this.motionY = result.hitVec.y - this.posY;
            this.motionZ = result.hitVec.z - this.posZ;
            float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            this.posX -= this.motionX / f2 * 0.05000000074505806D;
            this.posY -= this.motionY / f2 * 0.05000000074505806D;
            this.posZ -= this.motionZ / f2 * 0.05000000074505806D;
            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            this.inGround = true;
            this.arrowShake = 7;

            if (iblockstate.getMaterial() != Material.AIR)
            {
                this.inTile.onEntityCollision(this.world, blockpos, iblockstate, this);
            }
        }
    }

    public void onCollideWithPlayer(EntityPlayer entity) {
        if (!this.world.isRemote && this.inGround && this.arrowShake <= 0 && entity == this.shootingEntity) {
            boolean flag = this.pickupStatus == EntityArrow.PickupStatus.ALLOWED || this.pickupStatus == EntityArrow.PickupStatus.CREATIVE_ONLY && entity.capabilities.isCreativeMode;

            if (this.pickupStatus == EntityArrow.PickupStatus.ALLOWED && !entity.inventory.addItemStackToInventory(this.getArrowStack())) {
                flag = false;
            }

            if (flag) {
                entity.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }
}
