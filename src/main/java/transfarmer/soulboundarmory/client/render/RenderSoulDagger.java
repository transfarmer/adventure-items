package transfarmer.soulboundarmory.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderSystem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import transfarmer.soulboundarmory.init.ModItems;

import javax.annotation.Nullable;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.Main.MOD_ID;

@Environment(CLIENT)
public class RenderSoulDagger<T extends Entity> extends Render<T> {
    public static final Identifier THROWN_SOULBOUND_DAGGER = new Identifier(MOD_ID, "textures/item/soulbound_dagger.png");

    protected RenderSoulDagger(final RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected Identifier getEntityTexture(final T entity) {
        return THROWN_SOULBOUND_DAGGER;
    }

    public void doRender(final T entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        RenderSystem.pushMatrix();
        RenderSystem.translate(x, y, z);
        RenderSystem.enableRescaleNormal();
        RenderSystem.rotate(-this.renderManager.playerViewY, 0, 1, 0);
        RenderSystem.rotate((this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1, 0, 0);
        RenderSystem.rotate(180, 0, 1, 0);
        this.bindEntityTexture(entity);

        if (this.renderOutlines) {
            RenderSystem.enableColorMaterial();
            RenderSystem.enableOutlineMode(getTeamColor(entity));
        }

        CLIENT.getRenderItem().renderItem(new ItemStack(ModItems.SOULBOUND_DAGGER), ItemCameraTransforms.TransformType.GROUND);

        if (this.renderOutlines) {
            RenderSystem.disableOutlineMode();
            RenderSystem.disableColorMaterial();
        }

        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public static class RenderFactory implements IRenderFactory<EntityArrow> {
        @Override
        public Render<? super EntityArrow> createRenderFor(RenderManager manager) {
            return new RenderSoulDagger(manager);
        }
    }
}
