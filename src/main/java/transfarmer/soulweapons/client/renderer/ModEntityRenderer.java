package transfarmer.soulweapons.client.renderer;

import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SideOnly(CLIENT)
public class ModEntityRenderer {
    private final Minecraft mc;

    public ModEntityRenderer(final Minecraft minecraft) {
        this.mc = minecraft;
    }

    public RayTraceResult raycast() {
        final float partialTicks = 1;
        final Entity player = this.mc.getRenderViewEntity();

        if (player != null && this.mc.world != null) {
            final double range = 256;
            final Vec3d playerPos = player.getPositionEyes(partialTicks);
            final Vec3d playerLook = player.getLook(1.0F);
            final Vec3d end = playerPos.add(playerLook.x * range, playerLook.y * range, playerLook.z * range);
            RayTraceResult rayTraceResult = null;

            final List<Entity> entities = this.mc.world.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox()
                    .expand(playerLook.x * range, playerLook.y * range, playerLook.z * range)
                    .grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, apply -> apply != null && apply.canBeCollidedWith()));

            for (final Entity entity1 : entities) {
                final AxisAlignedBB axisAlignedBB = entity1.getEntityBoundingBox().grow(entity1.getCollisionBorderSize());

                if ((rayTraceResult = axisAlignedBB.calculateIntercept(playerPos, end)) != null) {
                    break;
                }
            }

            if (rayTraceResult == null) {
                rayTraceResult = this.mc.world.rayTraceBlocks(playerPos, end);
            }

            if (rayTraceResult != null) {
                final Vec3d hitVec = rayTraceResult.hitVec;
                return new RayTraceResult(hitVec, null, new BlockPos(hitVec));
            }
        }

        return null;
    }
}
