package transfarmer.soulboundarmory.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderFireball;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.entity.EntitySoulboundSmallFireball;

public class RenderSoulboundFireball extends RenderFireball {
    protected final float scale;

    public RenderSoulboundFireball(final RenderManager renderManager, final float scale) {
        super(renderManager, scale);

        this.scale = scale;
    }

    @Override
    public void doRender(@NotNull final EntityFireball entity, final double x, final double y, final double z,
                         final float entityYaw, final float partialTicks) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(this.scale, this.scale, this.scale);

        final EntitySoulboundSmallFireball fireball = (EntitySoulboundSmallFireball) entity;
        final TextureAtlasSprite sprite = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(fireball.getTextureItem());
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder builder = tessellator.getBuffer();

        final float minU = sprite.getMinU();
        final float maxU = sprite.getMaxU();
        final float minV = sprite.getMinV();
        final float maxV = sprite.getMaxV();

        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        builder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        builder.pos(-0.5D, -0.25D, 0.0D).tex(minU, maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
        builder.pos(0.5D, -0.25D, 0.0D).tex(maxU, maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
        builder.pos(0.5D, 0.75D, 0.0D).tex(maxU, minV).normal(0.0F, 1.0F, 0.0F).endVertex();
        builder.pos(-0.5D, 0.75D, 0.0D).tex(minU, minV).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        if (!this.renderOutlines) {
            this.renderName(entity, x, y, z);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(@NotNull final EntityFireball entity) {
        return super.getEntityTexture(entity);
    }

    public static class RenderFactory implements IRenderFactory<EntitySoulboundSmallFireball> {
        @Override
        public Render<? super EntitySoulboundSmallFireball> createRenderFor(final RenderManager manager) {
            return new RenderSoulboundFireball(manager, 0.325F);
        }
    }
}
