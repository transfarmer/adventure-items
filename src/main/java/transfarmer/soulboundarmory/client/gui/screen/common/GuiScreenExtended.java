package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeModContainer;

import java.util.ArrayList;
import java.util.List;

public class GuiScreenExtended extends GuiScreen {
    public static final Minecraft MINECRAFT = Minecraft.getMinecraft();
    public static final ItemModelMesher ITEM_MODEL_MESHER = MINECRAFT.getRenderItem().getItemModelMesher();
    public static final TextureManager TEXTURE_MANAGER = MINECRAFT.getTextureManager();
    public static final FontRenderer FONT_RENDERER = MINECRAFT.fontRenderer;
    public static final RenderItem RENDER_ITEM = Minecraft.getMinecraft().getRenderItem();
    public static final IResourceManager RESOURCE_MANAGER = Minecraft.getMinecraft().getResourceManager();
    public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    public void drawInterpolatedTexturedRect(int x, int y, final int startU, final int startV, final int middleU,
                                             final int middleV, final int endU, final int endV, final int finalU,
                                             final int finalV, int width, int height) {
        final int leftWidth = middleU - startU;
        final int topHeight = middleV - startV;

        this.drawHorizontalInterpolatedTexturedRect(x, y, startU, startV, middleU, endU, finalU, width, topHeight);
        this.drawHorizontalInterpolatedTexturedRect(x, y + height - topHeight, startU, endV, middleU, endU, finalU, width, topHeight);
        this.drawVerticalInterpolatedTexturedRect(x, y, startU, startV, middleV, endV, finalV, leftWidth, height);
        this.drawVerticalInterpolatedTexturedRect(x + width - leftWidth, y, endU, startV, middleV, endV, finalV, leftWidth, height);
        this.drawVerticalInterpolatedTexturedRect(x + leftWidth, y + topHeight, middleU, middleV, endV, width - 2 * leftWidth, height - 2 * topHeight);
    }

    public void drawHorizontalInterpolatedTexturedRect(int x, final int y, final int startU, final int startV,
                                                       final int middleU, final int endU, final int finalU,
                                                       int width, final int height) {
        final int startWidth = middleU - startU;
        final int finalWidth = finalU - endU;

        this.drawTexturedModalRect(x, y, startU, startV, startWidth, height);

        width -= startWidth + finalWidth;
        x += startWidth;
        x = this.drawHorizontalInterpolatedTexturedRect(x, y, startV, middleU, endU, width, height);

        this.drawTexturedModalRect(x, y, endU, startV, finalWidth, height);
    }

    public int drawHorizontalInterpolatedTexturedRect(int x, final int y, final int startV, final int middleU,
                                                      final int endU, int width, final int height) {
        while (width > 0) {
            final int middleWidth = Math.min(width, endU - middleU);

            this.drawTexturedModalRect(x, y, middleU, startV, middleWidth, height);
            x += middleWidth;
            width -= middleWidth;
        }

        return x;
    }

    public void drawVerticalInterpolatedTexturedRect(final int x, int y, final int startU, final int startV,
                                                     final int middleV, final int endV, final int finalV,
                                                     final int width, int height) {
        final int startHeight = middleV - startV;
        final int finalHeight = finalV - endV;

        this.drawTexturedModalRect(x, y, startU, startV, width, startHeight);
        height -= startHeight + finalHeight;
        y += startHeight;
        y = this.drawVerticalInterpolatedTexturedRect(x, y, startU, middleV, endV, width, height);

        this.drawTexturedModalRect(x, y, startU, endV, width, finalHeight);
    }

    public int drawVerticalInterpolatedTexturedRect(final int x, int y, final int startU, final int middleV,
                                                    final int endV, final int width, int height) {
        while (height > 0) {
            final int middleHeight = Math.min(height, endV - middleV);

            this.drawTexturedModalRect(x, y, startU, middleV, width, middleHeight);
            y += middleHeight;
            height -= middleHeight;
        }

        return y;
    }

    public void drawRectWithCustomSizedTexture(final int x, final int y, final float u, final float v,
                                                    final int width, final int height, final float textureWidth,
                                                    final float textureHeight) {
        final float f = 1F / textureWidth;
        final float f1 = 1F / textureHeight;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, this.zLevel).tex(u * f, (v + height) * f1).endVertex();
        bufferbuilder.pos(x + width, y + height, this.zLevel).tex((u + width) * f, (v + height) * f1).endVertex();
        bufferbuilder.pos(x + width, y, this.zLevel).tex((u + width) * f, v * f1).endVertex();
        bufferbuilder.pos(x, y, this.zLevel).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    public void withZ(final float zLevel, final Runnable method) {
        this.zLevel += zLevel;
        method.run();
        this.zLevel -= zLevel;
    }

    public void renderItemModelIntoGUI(final ItemStack stack, final int x, final int y, final IBakedModel model,
                                       final int color) {
        GlStateManager.pushMatrix();

        TEXTURE_MANAGER.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        TEXTURE_MANAGER.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        this.setupGuiTransform(x, y, model.isGui3d());
        this.renderItem(stack, color, ForgeHooksClient.handleCameraTransforms(model, TransformType.GUI, false));

        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();

        TEXTURE_MANAGER.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        TEXTURE_MANAGER.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }

    public void renderItem(final ItemStack stack, final int color, final IBakedModel model) {
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);

            if (model.isBuiltInRenderer()) {
                GlStateManager.enableRescaleNormal();
                stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
            } else {
                this.renderModel(model, color, stack);

                if (stack.hasEffect()) {
                    this.renderEffect(model);
                }
            }

            GlStateManager.popMatrix();
        }
    }

    public void renderModel(final IBakedModel model, final int color) {
        this.renderModel(model, color, ItemStack.EMPTY);
    }

    public void renderModel(final IBakedModel model, final int color, final ItemStack stack) {
        if (ForgeModContainer.allowEmissiveItems) {
            ForgeHooksClient.renderLitItem(RENDER_ITEM, model, color, stack);

            return;
        }

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);

        for (EnumFacing enumfacing : EnumFacing.values()) {
            RENDER_ITEM.renderQuads(bufferbuilder, model.getQuads(null, enumfacing, 0), color, stack);
        }

        RENDER_ITEM.renderQuads(bufferbuilder, model.getQuads(null, null, 0), color, stack);
        tessellator.draw();
    }

    public void renderEffect(final IBakedModel model) {
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(SourceFactor.SRC_COLOR, DestFactor.ONE);

        TEXTURE_MANAGER.bindTexture(RES_ITEM_GLINT);

        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8F, 8F, 8F);

        final float f = (float) (Minecraft.getSystemTime() % 3000) / 3000F / 8F;

        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50F, 0F, 0F, 1F);
        this.renderModel(model, 0x8040CB);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);

        final float f1 = (float) (Minecraft.getSystemTime() % 4873) / 4873F / 8F;

        GlStateManager.translate(-f1, 0F, 0F);
        GlStateManager.rotate(10F, 0F, 0F, 1F);
        this.renderModel(model, 0x8040CB);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);

        TEXTURE_MANAGER.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

    public void setupGuiTransform(final int x, final int y, final boolean isGUI3D) {
        GlStateManager.translate((float) x, (float) y, this.zLevel);
        GlStateManager.translate(8F, 8F, 0F);
        GlStateManager.scale(1F, -1F, 1F);
        GlStateManager.scale(16F, 16F, 16F);

        if (isGUI3D) {
            GlStateManager.enableLighting();
        } else {
            GlStateManager.disableLighting();
        }
    }

    public static TextureAtlasSprite getSprite(final Block block) {
        return getSprite(block.getDefaultState());
    }

    public static TextureAtlasSprite getSprite(final Block block, final int metadata) {
        return getSprite(block.getStateFromMeta(metadata));
    }

    public static TextureAtlasSprite getSprite(final IBlockState blockState) {
        return MINECRAFT.getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockState);
    }

    public static TextureAtlasSprite getSprite(final Item item) {
        return ITEM_MODEL_MESHER.getItemModel(item.getDefaultInstance()).getQuads(null, null, 0).get(0).getSprite();
    }

    public static ResourceLocation getTexture(final TextureAtlasSprite sprite) {
        final String[] location = sprite.getIconName().split(":");

        return new ResourceLocation(String.format("%s:textures/%s.png", location[0], location[1]));
    }

    public List<String> wrap(final int width, final String... strings) {
        final List<String> lines = new ArrayList<>();

        for (final String string : strings) {
            lines.addAll(wrap(width, string));
        }

        return lines;
    }

    public List<String> wrap(final int width, final String string) {
        final List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (final String word : string.split(" ")) {
            final int wordWidth = FONT_RENDERER.getStringWidth(word);
            final int lineWidth = FONT_RENDERER.getStringWidth(currentLine.toString());

            final boolean wrap = lineWidth + wordWidth > width;

            if (wrap && currentLine.length() == 0) {
                lines.add(word);
            } else {
                if (wrap) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }

                currentLine.append(word).append(" ");
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }
}
