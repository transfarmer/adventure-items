package transfarmer.soulboundarmory.util;

import net.minecraft.entity.Entity;

public class EntityHelper {
    public static double getVelocity(final Entity entity) {
        return Math.sqrt(entity.motionX * entity.motionX
                + entity.motionY * entity.motionY
                + entity.motionZ * entity.motionZ
        );
    }
}
