package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
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

    static void drawTexturedModalRect(final int x, final int y, final int v, final int u, final int width, final int height) {
         drawTexturedModalRect(x, y, v, u, width, height, 0);
    }

    static void drawTexturedModalRect(final int x, final int y, final int v, final int u, final int width,
                                      final int height, final int zLevel) {
        float f = 1F / 256F;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder builder = tessellator.getBuffer();

        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        builder.pos(x, y + height, zLevel).tex((float)(v) * f, (float)(u + height) * f).endVertex();
        builder.pos(x + width, y + height, zLevel).tex((float)(v + width) * f, (float)(u + height) * f).endVertex();
        builder.pos(x + width, y, zLevel).tex((v + width) * f, (float)(u) * f).endVertex();
        builder.pos(x, y, zLevel).tex((float)(v) * f, (float)(u) * f).endVertex();
        tessellator.draw();
    }

    static void drawInterpolatedTexturedRect(int x, int y, final int startU, final int startV, final int middleU,
                                                final int middleV, final int endU, final int endV, final int finalU,
                                                final int finalV, int width, int height) {
        final int leftWidth = middleU - startU;
        final int topHeight = middleV - startV;

        drawHorizontalInterpolatedTexturedRect(x, y, startU, startV, middleU, endU, finalU, width, topHeight);
        drawHorizontalInterpolatedTexturedRect(x, y + height - topHeight, startU, endV, middleU, endU, finalU, width, topHeight);
        drawVerticalInterpolatedTexturedRect(x, y, startU, startV, middleV, endV, finalV, leftWidth, height);
        drawVerticalInterpolatedTexturedRect(x + width - leftWidth, y, endU, startV, middleV, endV, finalV, leftWidth, height);
        drawVerticalInterpolatedTexturedRect(x + leftWidth, y + topHeight, middleU, middleV, endV, width - 2 * leftWidth, height - 2 * topHeight);
    }

    static void drawHorizontalInterpolatedTexturedRect(int x, final int y, final int startU, final int startV,
                                                       final int middleU, final int endU, final int finalU,
                                                       int width, final int height) {
        final int startWidth = middleU - startU;
        final int finalWidth = finalU - endU;

        drawTexturedModalRect(x, y, startU, startV, startWidth, height);
        width -= startWidth + finalWidth;
        x += startWidth;
        x = drawHorizontalInterpolatedTexturedRect(x, y, startV, middleU, endU, width, height);

        drawTexturedModalRect(x, y, endU, startV, finalWidth, height);
    }

    static int drawHorizontalInterpolatedTexturedRect(int x, final int y, final int startV, final int middleU,
                                                         final int endU, int width, final int height) {
        while (width > 0) {
            final int middleWidth = Math.min(width, endU - middleU);

            drawTexturedModalRect(x, y, middleU, startV, middleWidth, height);
            x += middleWidth;
            width -= middleWidth;
        }

        return x;
    }

    static void drawVerticalInterpolatedTexturedRect(final int x, int y, final int startU, final int startV,
                                                        final int middleV, final int endV, final int finalV,
                                                        final int width, int height) {
        final int startHeight = middleV - startV;
        final int finalHeight = finalV - endV;

        drawTexturedModalRect(x, y, startU, startV, width, startHeight);
        height -= startHeight + finalHeight;
        y += startHeight;
        y = drawVerticalInterpolatedTexturedRect(x, y, startU, middleV, endV, width, height);

        drawTexturedModalRect(x, y, startU, endV, width, finalHeight);
    }

    static int drawVerticalInterpolatedTexturedRect(final int x, int y, final int startU, final int middleV,
                                                       final int endV, final int width, int height) {
        while (height > 0) {
            final int middleHeight = Math.min(height, endV - middleV);

            drawTexturedModalRect(x, y, startU, middleV, width, middleHeight);
            y += middleHeight;
            height -= middleHeight;
        }
        return y;
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
