package transfarmer.soulboundarmory.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import transfarmer.farmerlib.reflect.FieldWrapper;
import transfarmer.farmerlib.util.MathUtil;

public abstract class ExtendedProjectile extends ProjectileEntity {
    protected FieldWrapper<Vec3d, ExtendedProjectile> velocity;
    protected FieldWrapper<Integer, ExtendedProjectile> life;

    public ExtendedProjectile(final World world) {
        super(EntityType.ARROW, world);

        this.setVelocityVector();

        this.life = new FieldWrapper<>(ProjectileEntity.class, this, "life");
    }

    protected ExtendedProjectile(final EntityType<? extends ProjectileEntity> type, final double x, final double y,
                                 final double z,
                                 final World world) {
        super(type, x, y, z, world);
    }

    protected ExtendedProjectile(final EntityType<? extends ProjectileEntity> type, final LivingEntity owner,
                                 final World world) {
        super(type, owner, world);
    }

    public ExtendedProjectile(final EntityType<? extends ProjectileEntity> type, final World world) {
        super(type, world);

        this.setVelocityVector();
    }

    public ExtendedProjectile(final World world, final EntityType<? extends ProjectileEntity> entityType,
                              final double x, final double y, final double z) {
        super(entityType, x, y, z, world);

        this.setVelocityVector();
    }

    private void setVelocityVector() {
        this.velocity = new FieldWrapper<>(ProjectileEntity.class, this, "velocity");
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
        return this.velocity.get().x;
    }

    public double velocityY() {
        return this.velocity.get().y;
    }

    public double velocityZ() {
        return this.velocity.get().z;
    }

    public double getVelocityD() {
        return this.getSpeed() * MathUtil.signum(this.velocityX(), this.velocityY(), this.velocityZ());
    }
}
