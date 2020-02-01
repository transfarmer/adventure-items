package transfarmer.soulweapons.entity;

import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class SoulDagger extends EntityThrowable {
    public SoulDagger(World world) {
        super(world);
    }

    @Override
    protected void onImpact(RayTraceResult result) {

    }
}
