package transfarmer.soulboundarmory.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;

public class SoulboundDamageSource {
    public static DamageSource causeThrownDamage(Entity source, @Nullable Entity indirectEntityIn) {
        return (new SoulboundEntityDamageSourceIndirect("thrown", source, indirectEntityIn)).setProjectile();
    }

    public static DamageSource causeIndirectDamage(Entity source, EntityLivingBase indirectEntityIn)
    {
        return new SoulboundEntityDamageSourceIndirect("mob", source, indirectEntityIn);
    }
}
