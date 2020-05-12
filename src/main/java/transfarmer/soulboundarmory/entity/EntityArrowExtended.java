package transfarmer.soulboundarmory.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import transfarmer.farmerlib.reflect.FieldWrapper;
import transfarmer.farmerlib.util.MathUtil;

public abstract class EntityArrowExtended extends ProjectileEntity {
    protected final FieldWrapper<Vec3d, EntityArrowExtended> velocityVector;

    public EntityArrowExtended(final World world) {
        super(EntityType.ARROW, world);

        this.velocityVector = new FieldWrapper<>(this, "velocity");
    }

    public double displacementTo(final Entity entity) {
        return this.displacementTo(entity.getX(), entity.getY(), entity.getZ());
    }

    public double displacementTo(final double x, final double y, final double z) {
        return this.distanceTo(x, y, z) * MathUtil.signum(x - this.getX(), y - this.getY(), z - this.getZ());
    }

    public double distanceTo(final double x, final double y, final double z) {
        return Math.sqrt(this.squaredDistanceTo(x, y, z));
    }

    public double getSpeed() {
        return Math.sqrt(this.velocityX() * this.velocityX()
                + this.velocityY() * this.velocityY()
                + this.velocityZ() * this.velocityZ()
        );
    }

    public double velocityX() {
        return this.velocityVector.get().x;
    }

    public double velocityY() {
        return this.velocityVector.get().y;
    }

    public double velocityZ() {
        return this.velocityVector.get().z;
    }

    public double getVelocityD() {
        return this.getSpeed() * MathUtil.signum(this.velocityX(), this.velocityY(), this.velocityZ());
    }
}
