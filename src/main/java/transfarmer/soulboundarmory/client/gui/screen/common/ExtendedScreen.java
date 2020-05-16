package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static transfarmer.soulboundarmory.MainClient.CLIENT;

public abstract class ExtendedScreen extends Screen {
    public static final ItemRenderer ITEM_RENDERER = CLIENT.getItemRenderer();
    public static final ItemModels ITEM_MODELS = ITEM_RENDERER.getModels();
    public static final TextureManager TEXTURE_MANAGER = CLIENT.getTextureManager();
    public static final TextRenderer TEXT_RENDERER = CLIENT.textRenderer;
    public static final ResourceManager RESOURCE_MANAGER = CLIENT.getResourceManager();
    public static final Identifier GLINT = new Identifier("textures/misc/enchanted_item_glint.png");

    protected ExtendedScreen(final Text title) {
        super(title);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (scanCode == CLIENT.options.keyInventory.getDefaultKeyCode().getKeyCode()) {
            this.onClose();

            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

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

    public void drawHorizontalInterpolatedTexturedRect(int x, final int y, int startU, final int startV,
                                                       final int middleU, int endU, final int finalU, int width,
                                                       final int height) {
        if (middleU < startU) {
            startU = middleU;
        }

        final int startWidth = middleU - startU;
        final int finalWidth = finalU - endU;

        this.blit(x, y, startU, startV, startWidth, height);

        width -= startWidth + finalWidth;
        x += startWidth;
        x = this.drawHorizontalInterpolatedTexturedRect(x, y, startV, middleU, endU, width, height);

        this.blit(x, y, endU, startV, finalWidth, height);
    }

    public int drawHorizontalInterpolatedTexturedRect(int x, final int y, final int startV, final int middleU,
                                                      final int endU, int width, final int height) {
        while (width > 0) {
            final int middleWidth = Math.min(width, endU - middleU);

            this.blit(x, y, middleU, startV, middleWidth, height);

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

        this.blit(x, y, startU, startV, width, startHeight);
        height -= startHeight + finalHeight;
        y += startHeight;
        y = this.drawVerticalInterpolatedTexturedRect(x, y, startU, middleV, endV, width, height);

        this.blit(x, y, startU, endV, width, finalHeight);
    }

    public int drawVerticalInterpolatedTexturedRect(final int x, int y, final int startU, final int middleV,
                                                    final int endV, final int width, int height) {
        while (height > 0) {
            final int middleHeight = Math.min(height, endV - middleV);

            this.blit(x, y, startU, middleV, width, middleHeight);
            y += middleHeight;
            height -= middleHeight;
        }

        return y;
    }

    public void withZ(final int blitOffset, final Runnable method) {
        this.addBlitOffset(blitOffset);
        method.run();
        this.addBlitOffset(-blitOffset);
    }

    public void addBlitOffset(final int blitOffset) {
        this.setBlitOffset(this.getBlitOffset() + blitOffset);
    }

    public static Sprite getSprite(final Block block) {
        return getSprite(block.getDefaultState());
    }

    public static Sprite getSprite(final BlockState blockState) {
        return CLIENT.getBlockRenderManager().getModels().getSprite(blockState);
    }

    public static SpriteAtlasTexture getSprite(final Item item) {
        return null;//ITEM_MODELS.getModel(new ItemStack(item)).getQuads(null, null, null).get(0);
    }

    public static Identifier getTexture(final SpriteAtlasTexture sprite) {
        final String[] location = sprite.getId().toString().split(":");

        return new Identifier(String.format("%s:textures/%s.png", location[0], location[1]));
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
            final int wordWidth = CLIENT.textRenderer.getStringWidth(word);
            final int lineWidth = TEXT_RENDERER.getStringWidth(currentLine.toString());

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
