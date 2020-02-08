package transfarmer.soulweapons.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import transfarmer.soulweapons.Main;

import javax.annotation.Nullable;

public class RenderReachModifier<T extends Entity> extends Render<T> {
    protected RenderReachModifier(final RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(final T entity) {
        return new ResourceLocation(Main.MODID, "textures/entity/reach_modifier.png");
    }

    public void doRender(final T entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {}

    public static class RenderFactory implements IRenderFactory<EntityArrow> {
        @Override
        public Render<? super EntityArrow> createRenderFor(RenderManager manager) {
            return new RenderReachModifier(manager);
        }
    }
}
