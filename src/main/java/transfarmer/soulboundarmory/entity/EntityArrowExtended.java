package transfarmer.soulboundarmory.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;
import transfarmer.soulboundarmory.util.MathUtil;

public abstract class EntityArrowExtended extends EntityArrow {
    public EntityArrowExtended(final World worldIn) {
        super(worldIn);
    }

    public EntityArrowExtended(final World world, final double x, final double y, final double z) {
        super(world, x, y, z);
    }

    public double getDisplacement(final Entity entity) {
        return this.getDisplacement(entity.posX, entity.posY, entity.posZ);
    }

    public double getDisplacement(final double x, final double y, final double z) {
        return this.getDistance(x, y, z) * this.getDirection(this.posX - x, this.posY - y, this.posZ - z);
    }

    public double getSpeed() {
        return Math.sqrt(this.motionX * this.motionX
                + this.motionY * this.motionY
                + this.motionZ * this.motionZ
        );
    }

    public double getVelocity() {
        return this.getSpeed() * MathUtil.signum(this.motionX, this.motionY, this.motionZ);
    }

    public double getDirection(final double x, final double y, final double z) {
        return MathUtil.signum(x, y, z);
    }
}
