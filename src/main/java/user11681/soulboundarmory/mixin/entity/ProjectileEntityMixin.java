package user11681.soulboundarmory.mixin.entity;

import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import user11681.soulboundarmory.duck.entity.ProjectileEntityDuck;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin implements ProjectileEntityDuck {
    @Accessor
    @Override
    public abstract int getLife();
}
