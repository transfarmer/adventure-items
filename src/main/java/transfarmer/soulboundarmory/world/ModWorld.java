package transfarmer.soulboundarmory.world;

import com.google.common.base.Predicates;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class ModWorld {
    public static RayTraceResult rayTraceAll(final World world, final PlayerEntity player) {
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

    @Environment(CLIENT)
    public static class ModWorldClient extends WorldClient {
        public ModWorldClient(final NetHandlerPlayClient netHandler, final WorldSettings settings, final int dimension, final EnumDifficulty difficulty, final Profiler profilerIn) {
            super(netHandler, settings, dimension, difficulty, profilerIn);
        }

        @Override
        public void sendBlockBreakProgress(final int breakerId, BlockPos pos, final int progress) {
            for (int index = 0; index < this.eventListeners.size(); ++index) {
                IWorldEventListener worldEventListener = this.eventListeners.get(index);
                worldEventListener.sendBlockBreakProgress(breakerId, pos, progress);
            }
        }
    }
}
