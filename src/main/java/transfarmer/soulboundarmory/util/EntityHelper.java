package transfarmer.soulboundarmory.util;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

public class EntityHelper {
    public static double getVelocity(final Entity entity) {
        return Math.sqrt(entity.motionX * entity.motionX
                + entity.motionY * entity.motionY
                + entity.motionZ * entity.motionZ
        );
    }

    public static Entity getEntity(final UUID id) {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(id);
    }
}
