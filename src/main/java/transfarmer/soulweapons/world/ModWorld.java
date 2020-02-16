package transfarmer.soulweapons.world;

import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class ModWorld {
    public static RayTraceResult rayTraceAll(final World world, final EntityPlayer player) {
        final float partialTicks = 1;

        if (player != null && world != null) {
            final double range = 1024;
            final Vec3d playerPos = player.getPositionEyes(partialTicks);
            final Vec3d playerLook = player.getLook(partialTicks);
            final Vec3d end = playerPos.add(playerLook.x * range, playerLook.y * range, playerLook.z * range);
            RayTraceResult rayTraceResult = null;

            final List<Entity> entities = world.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox()
                    .expand(playerLook.x * range, playerLook.y * range, playerLook.z * range)
                    .grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, apply -> apply != null && apply.canBeCollidedWith()));

            for (final Entity entity : entities) {
                final AxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());

                if ((rayTraceResult = axisAlignedBB.calculateIntercept(playerPos, end)) != null) break;
            }

            if (rayTraceResult == null) {
                rayTraceResult = world.rayTraceBlocks(playerPos, end);
            }

            if (rayTraceResult != null) {
                final Vec3d hitVec = rayTraceResult.hitVec;

                return new RayTraceResult(hitVec, null, new BlockPos(hitVec));
            }
        }

        return null;
    }
}
