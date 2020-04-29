package transfarmer.soulboundarmory.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;

public class SoulboundDamageSource {
    public static DamageSource causeThrownDamage(final Entity source, @Nullable final Entity indirectEntity) {
        return (new SoulboundEntityDamageSourceIndirect("thrown", source, indirectEntity)).setProjectile();
    }

    public static DamageSource causeIndirectDamage(final Entity source, final EntityLivingBase indirectEntity) {
        return new SoulboundEntityDamageSourceIndirect("mob", source, indirectEntity);
    }
}
