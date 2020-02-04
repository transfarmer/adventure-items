package transfarmer.soulweapons.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.capability.ISoulWeapon;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.ATTACK_DAMAGE;
import static transfarmer.soulweapons.data.SoulWeaponEnchantment.SHARPNESS;
import static transfarmer.soulweapons.data.SoulWeaponType.DAGGER;

public class EntitySoulDagger extends EntityThrowable {
    public EntitySoulDagger(World world) {
        super(world);
    }

    public EntitySoulDagger(World world, EntityLivingBase thrower) {
        super(world, thrower);
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
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null && this.getThrower() instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) this.getThrower();
            final ISoulWeapon capability = player.getCapability(CAPABILITY, null);
            float attackDamage = capability.getAttribute(ATTACK_DAMAGE, DAGGER);

            if (capability.getEnchantments(DAGGER).containsKey(SHARPNESS)) {
                attackDamage += 1 + (capability.getEnchantment(SHARPNESS, DAGGER) - 1) / 2F;
            }

            ForgeEventFactory.onProjectileImpact(this, this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY, this.posZ),
                new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ), false, true, false));
        }

        if (!this.world.isRemote) {
            this.world.setEntityState(this, (byte) 3);
        }
    }
}
