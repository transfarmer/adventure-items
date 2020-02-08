package transfarmer.soulweapons.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityExtendedReach extends Entity implements IProjectile {
    private float reach;
    private Vec3d initialPos;

    public EntityExtendedReach(World world) {
        super(world);
    }

    public EntityExtendedReach(World world, float reach) {
        this(world);
        this.reach = reach;
        this.initialPos = new Vec3d(this.posX, this.posY, this.posZ);
    }

    @Override
    protected void entityInit() {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {

    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        this.motionX = x * velocity;
        this.motionY = y * velocity;
        this.motionZ = y * velocity;
    }
}
