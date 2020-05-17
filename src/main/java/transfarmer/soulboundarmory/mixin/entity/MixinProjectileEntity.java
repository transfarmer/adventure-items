package transfarmer.soulboundarmory.mixin.entity;

import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import transfarmer.soulboundarmory.duck.entity.ProjectileEntityDuck;

@Mixin(ProjectileEntity.class)
public class MixinProjectileEntity implements ProjectileEntityDuck {
    @Shadow private int life;

    @Override
    public int getLife() {
        return this.life;
    }
}
