package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.ISoulbound;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.skill.ISkill;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class GuiTabSkills extends GuiTabSoulbound {
    protected static final Minecraft MINECRAFT = Minecraft.getMinecraft();
    protected static final ItemModelMesher ITEM_MODEL_MESHER = MINECRAFT.getRenderItem().getItemModelMesher();
    protected static final TextureManager TEXTURE_MANAGER = MINECRAFT.getTextureManager();

    protected static final TextureAtlasSprite BACKGROUND = getSprite(Blocks.STONE, 5);
    protected static final ResourceLocation BACKGROUND_TEXTURE = getTexture(BACKGROUND);
    protected static final ResourceLocation WINDOW = new ResourceLocation("textures/gui/advancements/window.png");
    protected static final ResourceLocation SKILL_BACKGROUND = new ResourceLocation("textures/gui/advancements/widgets.png");

    public GuiTabSkills(final Capability<? extends ISoulbound> key, final List<GuiTab> tabs) {
        super(key, tabs);
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_BUTTON_SKILLS;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawWindow();

        GlStateManager.color(1, 1, 1, 1);

        final ISkill[] skills = this.capability.getSkills();

        for (final ISkill skill : skills) {
            this.drawSkill(skill);
        }
    }

    public void drawWindow() {
        final int width = 256;
        final int height = 192;
        final int alphaWidth = 252;
        final int alphaHeight = 140;
        final int half = height / 2;
        final int x = (this.width - width) / 2;
        final int y = (this.height - height) / 2;

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableBlend();

        TEXTURE_MANAGER.bindTexture(BACKGROUND_TEXTURE);
        drawModalRectWithCustomSizedTexture(x + 4, y + 4, 0, 0, width - 10, height - 10, BACKGROUND.getIconWidth(), BACKGROUND.getIconHeight());
        TEXTURE_MANAGER.deleteTexture(BACKGROUND_TEXTURE);

        TEXTURE_MANAGER.bindTexture(WINDOW);
        this.drawTexturedModalRect(x, y, 0, 0, alphaWidth, half);
        this.drawTexturedModalRect(x, y + half, 0, 40, width, alphaHeight - 40);
        TEXTURE_MANAGER.deleteTexture(WINDOW);

        this.fontRenderer.drawString(Mappings.MENU_BUTTON_SKILLS, x + 8, y + 6, 0x404040);
    }

    protected void drawSkill(final ISkill skill, int posX, int posY) {
        final ResourceLocation texture = skill.getTexture();
        final BufferedImage image = readTexture(texture);

        if (image != null) {
            final int imageWidth = image.getWidth();
            final int imageHeight = image.getHeight();
            final int width = 16;
            final int height = 16;
            posX -= width / 2;
            posY -= height / 2;

            TEXTURE_MANAGER.bindTexture(SKILL_BACKGROUND);
            this.drawTexturedModalRect(posX - 4, posY - 4, 1, 155, 24, 24);
            TEXTURE_MANAGER.deleteTexture(SKILL_BACKGROUND);

            TEXTURE_MANAGER.bindTexture(texture);
            drawScaledCustomSizeModalRect(posX, posY, 0, 0, imageWidth, imageHeight, width, height, imageWidth, imageHeight);
            TEXTURE_MANAGER.deleteTexture(texture);
        }
    }

    protected void drawSkill(final ISkill skill) {
        this.drawSkill(skill, this.width / 2 + 16 * skill.getTier(), this.height / 2 + 16 * skill.getTier());
    }

    @Override
    public void actionPerformed(@NotNull final GuiButton button) {
        super.actionPerformed(button);
    }

    protected static BufferedImage readTexture(final ResourceLocation texture) {
        try {
            return ImageIO.read(ImageIO.createImageInputStream(MINECRAFT.getResourceManager().getResource(texture).getInputStream()));
        } catch (final IOException exception) {
            Main.LOGGER.error(exception);
        }

        return null;
    }

    protected static TextureAtlasSprite getSprite(final Block block) {
        return getSprite(block.getDefaultState());
    }

    protected static TextureAtlasSprite getSprite(final Block block, final int metadata) {
        return getSprite(block.getStateFromMeta(metadata));
    }

    protected static TextureAtlasSprite getSprite(final IBlockState blockState) {
        return MINECRAFT.getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockState);
    }

    protected static TextureAtlasSprite getSprite(final Item item) {
        return ITEM_MODEL_MESHER.getItemModel(item.getDefaultInstance()).getQuads(null, null, 0).get(0).getSprite();
    }

    protected static ResourceLocation getTexture(final TextureAtlasSprite sprite) {
        final String[] location = sprite.getIconName().split(":");

        return new ResourceLocation(String.format("%s:textures/%s.png", location[0], location[1]));
    }
}
