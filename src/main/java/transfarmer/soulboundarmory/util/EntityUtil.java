package transfarmer.soulboundarmory.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class EntityUtil {
    public static double getVelocity(final Entity entity) {
        return Math.sqrt(entity.motionX * entity.motionX
                + entity.motionY * entity.motionY
                + entity.motionZ * entity.motionZ
        );
    }

    public static Entity getEntity(final UUID id) {
        final Side side = FMLCommonHandler.instance().getSide();

        if (side.isClient()) {
            return Minecraft.getMinecraft().getIntegratedServer().getEntityFromUuid(id);
        }

        return FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(id);
    }
}
