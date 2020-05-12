package transfarmer.soulboundarmory.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.Main.MOD_ID;

@Environment(CLIENT)
public class RenderReachModifier<T extends Entity> extends Render<T> {
    public static final Identifier REACH_MODIFIER = new Identifier(MOD_ID, "textures/entity/reach_modifier.png");

    protected RenderReachModifier(final RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected Identifier getEntityTexture(@NotNull final T entity) {
        return REACH_MODIFIER;
    }

    public void doRender(@NotNull final T entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {}

    public static class RenderFactory implements IRenderFactory<EntityArrow> {
        @Override
        public Render<? super EntityArrow> createRenderFor(RenderManager manager) {
            return new RenderReachModifier<>(manager);
        }
    }
}
