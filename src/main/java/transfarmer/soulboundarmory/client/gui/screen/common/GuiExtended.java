package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeModContainer;
import transfarmer.soulboundarmory.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface GuiExtended {
    Minecraft MINECRAFT = Minecraft.getMinecraft();
    ItemModelMesher ITEM_MODEL_MESHER = MINECRAFT.getRenderItem().getItemModelMesher();
    TextureManager TEXTURE_MANAGER = MINECRAFT.getTextureManager();
    FontRenderer FONT_RENDERER = MINECRAFT.fontRenderer;
    RenderItem RENDER_ITEM = Minecraft.getMinecraft().getRenderItem();
    ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    static void drawInterpolatedTexturedRect(int x, int y, final int startU, final int startV, final int middleU,
                                             final int middleV, final int endU, final int endV, final int finalU,
                                             final int finalV, int width, int height) {
        drawInterpolatedTexturedRect(x, y, startU, startV, middleU, middleV, endU, endV, finalU, finalV, width, height, 0);
    }

    static void drawInterpolatedTexturedRect(int x, int y, final int startU, final int startV, final int middleU,
                                             final int middleV, final int endU, final int endV, final int finalU,
                                             final int finalV, int width, int height, final float zLevel) {
        final int leftWidth = middleU - startU;
        final int topHeight = middleV - startV;

        drawHorizontalInterpolatedTexturedRect(x, y, startU, startV, middleU, endU, finalU, width, topHeight, zLevel);
        drawHorizontalInterpolatedTexturedRect(x, y + height - topHeight, startU, endV, middleU, endU, finalU, width, topHeight, zLevel);
        drawVerticalInterpolatedTexturedRect(x, y, startU, startV, middleV, endV, finalV, leftWidth, height, zLevel);
        drawVerticalInterpolatedTexturedRect(x + width - leftWidth, y, endU, startV, middleV, endV, finalV, leftWidth, height, zLevel);
        drawVerticalInterpolatedTexturedRect(x + leftWidth, y + topHeight, middleU, middleV, endV, width - 2 * leftWidth, height - 2 * topHeight, zLevel);
    }

    static void drawHorizontalInterpolatedTexturedRect(int x, int y, final int startU, final int startV,
                                                       final int middleU, final int endU, final int finalU, int width,
                                                       final int height) {
        drawHorizontalInterpolatedTexturedRect(x, y, startU, startV, middleU, endU, finalU, width, height, 0);
    }

    static void drawHorizontalInterpolatedTexturedRect(int x, final int y, final int startU, final int startV,
                                                       final int middleU, final int endU, final int finalU,
                                                       int width, final int height, final float zLevel) {
        final int startWidth = middleU - startU;
        final int finalWidth = finalU - endU;

        drawTexturedModalRect(x, y, startU, startV, startWidth, height, zLevel);

        width -= startWidth + finalWidth;
        x += startWidth;
        x = drawHorizontalInterpolatedTexturedRect(x, y, startV, middleU, endU, width, height, zLevel);

        drawTexturedModalRect(x, y, endU, startV, finalWidth, height, zLevel);
    }

    static int drawHorizontalInterpolatedTexturedRect(int x, int y, int startV, int middleU, int endU, int width,
                                                      int height) {
        return drawHorizontalInterpolatedTexturedRect(x, y, startV, middleU, endU, width, height, 0);
    }

    static int drawHorizontalInterpolatedTexturedRect(int x, final int y, final int startV, final int middleU,
                                                      final int endU, int width, final int height, final float zLevel) {
        while (width > 0) {
            final int middleWidth = Math.min(width, endU - middleU);

            drawTexturedModalRect(x, y, middleU, startV, middleWidth, height, zLevel);
            x += middleWidth;
            width -= middleWidth;
        }

        return x;
    }

    static void drawVerticalInterpolatedTexturedRect(final int x, int y, final int startU, final int startV,
                                                     final int middleV, final int endV, final int finalV,
                                                     final int width, int height) {
        drawVerticalInterpolatedTexturedRect(x, y, startU, startV, middleV, endV, finalV, width, height, 0);
    }

    static void drawVerticalInterpolatedTexturedRect(final int x, int y, final int startU, final int startV,
                                                     final int middleV, final int endV, final int finalV,
                                                     final int width, int height, final float zLevel) {
        final int startHeight = middleV - startV;
        final int finalHeight = finalV - endV;

        drawTexturedModalRect(x, y, startU, startV, width, startHeight, zLevel);
        height -= startHeight + finalHeight;
        y += startHeight;
        y = drawVerticalInterpolatedTexturedRect(x, y, startU, middleV, endV, width, height, zLevel);

        drawTexturedModalRect(x, y, startU, endV, width, finalHeight, zLevel);
    }

    static int drawVerticalInterpolatedTexturedRect(final int x, int y, final int startU, final int middleV,
                                                    final int endV, final int width, int height) {
        return drawVerticalInterpolatedTexturedRect(x, y, startU, middleV, endV, width, height, 0);
    }

    static int drawVerticalInterpolatedTexturedRect(final int x, int y, final int startU, final int middleV,
                                                    final int endV, final int width, int height, final float zLevel) {
        while (height > 0) {
            final int middleHeight = Math.min(height, endV - middleV);

            drawTexturedModalRect(x, y, startU, middleV, width, middleHeight, zLevel);
            y += middleHeight;
            height -= middleHeight;
        }
        return y;
    }

    static void drawTexturedModalRect(final int x, final int y, final int v, final int u, final int width,
                                      final int height) {
        drawTexturedModalRect(x, y, v, u, width, height, 0);
    }

    static void drawTexturedModalRect(final int x, final int y, final int v, final int u, final int width,
                                      final int height, final float zLevel) {
        float f = 1F / 256F;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder builder = tessellator.getBuffer();

        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        builder.pos(x, y + height, zLevel).tex((float) (v) * f, (float) (u + height) * f).endVertex();
        builder.pos(x + width, y + height, zLevel).tex((float) (v + width) * f, (float) (u + height) * f).endVertex();
        builder.pos(x + width, y, zLevel).tex((v + width) * f, (float) (u) * f).endVertex();
        builder.pos(x, y, zLevel).tex((float) (v) * f, (float) (u) * f).endVertex();
        tessellator.draw();
    }

    static void drawModalRectWithCustomSizedTexture(final int x, final int y, final float u, final float v,
                                                    final int width, final int height, final float textureWidth,
                                                    final float textureHeight) {
        drawModalRectWithCustomSizedTexture(x, y, u, v, width, height, textureWidth, textureHeight, 0);
    }

    static void drawModalRectWithCustomSizedTexture(final int x, final int y, final float u, final float v,
                                                    final int width, final int height, final float textureWidth,
                                                    final float textureHeight, final float zLevel) {
        final float f = 1F / textureWidth;
        final float f1 = 1F / textureHeight;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, zLevel).tex(u * f, (v + (float) height) * f1).endVertex();
        bufferbuilder.pos(x + width, y + height, zLevel).tex((u + (float) width) * f, (v + (float) height) * f1).endVertex();
        bufferbuilder.pos(x + width, y, zLevel).tex((u + (float) width) * f, v * f1).endVertex();
        bufferbuilder.pos(x, y, zLevel).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    static void renderItemModelIntoGUI(final ItemStack stack, final int x, final int y, final IBakedModel model,
                                       final int color, final float zLevel) {
        GlStateManager.pushMatrix();

        TEXTURE_MANAGER.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        TEXTURE_MANAGER.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        setupGuiTransform(x, y, model.isGui3d(), zLevel);
        renderItem(stack, color, ForgeHooksClient.handleCameraTransforms(model, TransformType.GUI, false));

        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();

        TEXTURE_MANAGER.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        TEXTURE_MANAGER.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }

    static void renderItem(final ItemStack stack, final int color, final IBakedModel model) {
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);

            if (model.isBuiltInRenderer()) {
                GlStateManager.enableRescaleNormal();
                stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
            } else {
                renderModel(model, color, stack);

                if (stack.hasEffect()) {
                    renderEffect(model);
                }
            }

            GlStateManager.popMatrix();
        }
    }

    static void renderModel(final IBakedModel model, final int color) {
        renderModel(model, color, ItemStack.EMPTY);
    }

    static void renderModel(final IBakedModel model, final int color, final ItemStack stack) {
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

    static void renderEffect(final IBakedModel model) {
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
        renderModel(model, -8372020);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);

        final float f1 = (float) (Minecraft.getSystemTime() % 4873) / 4873F / 8F;

        GlStateManager.translate(-f1, 0F, 0F);
        GlStateManager.rotate(10F, 0F, 0F, 1F);
        renderModel(model, -8372020);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);

        TEXTURE_MANAGER.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

    static void setupGuiTransform(final int x, final int y, final boolean isGUI3D, final float zLevel) {
        GlStateManager.translate((float) x, (float) y, zLevel);
        GlStateManager.translate(8F, 8F, 0F);
        GlStateManager.scale(1F, -1F, 1F);
        GlStateManager.scale(16F, 16F, 16F);

        if (isGUI3D) {
            GlStateManager.enableLighting();
        } else {
            GlStateManager.disableLighting();
        }
    }

    static BufferedImage readTexture(final ResourceLocation texture) {
        try {
            return ImageIO.read(ImageIO.createImageInputStream(MINECRAFT.getResourceManager().getResource(texture).getInputStream()));
        } catch (final IOException exception) {
            Main.LOGGER.error(exception);
        }

        return null;
    }

    static TextureAtlasSprite getSprite(final Block block) {
        return getSprite(block.getDefaultState());
    }

    static TextureAtlasSprite getSprite(final Block block, final int metadata) {
        return getSprite(block.getStateFromMeta(metadata));
    }

    static TextureAtlasSprite getSprite(final IBlockState blockState) {
        return MINECRAFT.getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockState);
    }

    static TextureAtlasSprite getSprite(final Item item) {
        return ITEM_MODEL_MESHER.getItemModel(item.getDefaultInstance()).getQuads(null, null, 0).get(0).getSprite();
    }

    static ResourceLocation getTexture(final TextureAtlasSprite sprite) {
        final String[] location = sprite.getIconName().split(":");

        return new ResourceLocation(String.format("%s:textures/%s.png", location[0], location[1]));
    }

    static List<String> wrap(final int width, final String... strings) {
        final List<String> lines = new ArrayList<>();

        for (final String string : strings) {
            lines.addAll(wrap(width, string));
        }

        return lines;
    }

    static List<String> wrap(final int width, final String string) {
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
