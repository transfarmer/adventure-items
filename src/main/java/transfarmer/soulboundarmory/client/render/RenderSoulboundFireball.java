package transfarmer.soulboundarmory.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderSystem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderFireball;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.SpriteAtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.Identifier;
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
        RenderSystem.pushMatrix();
        this.bindEntityTexture(entity);
        RenderSystem.translate((float) x, (float) y, (float) z);
        RenderSystem.enableRescaleNormal();
        RenderSystem.scale(this.scale, this.scale, this.scale);

        final EntitySoulboundSmallFireball fireball = (EntitySoulboundSmallFireball) entity;
        final SpriteAtlasTexture sprite = CLIENT.getRenderItem().getItemModelMesher().getParticleIcon(fireball.getTextureItem());
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder builder = tessellator.getBuffer();

        final float minU = sprite.getMinU();
        final float maxU = sprite.getMaxU();
        final float minV = sprite.getMinV();
        final float maxV = sprite.getMaxV();

        RenderSystem.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        RenderSystem.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        if (this.renderOutlines) {
            RenderSystem.enableColorMaterial();
            RenderSystem.enableOutlineMode(this.getTeamColor(entity));
        }

        builder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        builder.pos(-0.5D, -0.25D, 0.0D).tex(minU, maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
        builder.pos(0.5D, -0.25D, 0.0D).tex(maxU, maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
        builder.pos(0.5D, 0.75D, 0.0D).tex(maxU, minV).normal(0.0F, 1.0F, 0.0F).endVertex();
        builder.pos(-0.5D, 0.75D, 0.0D).tex(minU, minV).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();

        if (this.renderOutlines) {
            RenderSystem.disableOutlineMode();
            RenderSystem.disableColorMaterial();
        }

        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();

        if (!this.renderOutlines) {
            this.renderName(entity, x, y, z);
        }
    }

    @Override
    protected Identifier getEntityTexture(@NotNull final EntityFireball entity) {
        return super.getEntityTexture(entity);
    }

    public static class RenderFactory implements IRenderFactory<EntitySoulboundSmallFireball> {
        @Override
        public Render<? super EntitySoulboundSmallFireball> createRenderFor(final RenderManager manager) {
            return new RenderSoulboundFireball(manager, 0.325F);
        }
    }
}
