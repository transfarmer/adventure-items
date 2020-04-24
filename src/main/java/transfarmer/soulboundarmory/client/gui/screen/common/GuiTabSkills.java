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
import transfarmer.soulboundarmory.util.CollectionUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GuiTabSkills extends GuiTabSoulbound {
    protected static final Minecraft MINECRAFT = Minecraft.getMinecraft();
    protected static final ItemModelMesher ITEM_MODEL_MESHER = MINECRAFT.getRenderItem().getItemModelMesher();
    protected static final TextureManager TEXTURE_MANAGER = MINECRAFT.getTextureManager();

    protected static final TextureAtlasSprite BACKGROUND = getSprite(Blocks.STONE, 5);
    protected static final ResourceLocation BACKGROUND_TEXTURE = getTexture(BACKGROUND);
    protected static final ResourceLocation WINDOW = new ResourceLocation("textures/gui/advancements/window.png");
    protected static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/advancements/widgets.png");
    protected final Map<ISkill, List<Integer>> skills;
    protected float fade;
    protected int windowWidth;
    protected int windowHeight;
    protected int insideWidth;
    protected int insideHeight;
    protected int centerX;
    protected int centerY;
    protected int insideX;
    protected int insideY;
    protected int x;
    protected int y;

    public GuiTabSkills(final Capability<? extends ISoulbound> key, final List<GuiTab> tabs) {
        super(key, tabs);

        this.skills = new LinkedHashMap<>();
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_BUTTON_SKILLS;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.windowWidth = 256;
        this.windowHeight = 192;
        this.insideWidth = this.windowWidth - 23;
        this.insideHeight = this.windowHeight - 27;
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
        this.insideX = this.centerX - this.insideWidth / 2;
        this.insideY = this.centerY - this.insideHeight / 2;
        this.x = this.centerX - this.windowHeight / 2;
        this.y = this.centerY - this.windowWidth / 2;

        final Map<Integer, Integer> tierOrders = new HashMap<>();
        final ISkill[] skills = this.capability.getSkills();
        int currentTierSkills;

        this.skills.clear();

        for (final ISkill skill : skills) {
            final int tier = skill.getTier();
            tierOrders.put(tier, tierOrders.getOrDefault(tier, -1) + 1);

            currentTierSkills = 0;

            for (final ISkill other : skills) {
                if (other != skill) {
                    currentTierSkills++;
                }
            }

            this.skills.put(skill, CollectionUtil.arrayList(this.centerX + Math.round(48 * (tierOrders.get(tier) - currentTierSkills / 2F)), this.insideY + 24 + 32 * tier));
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawWindow(mouseX, mouseY);
        this.drawSkills();
    }

    public void drawWindow(final int mouseX, final int mouseY) {
        final int alphaWidth = 252;
        final int alphaHeight = 140;
        final int half = this.windowHeight / 2;
        final int x = this.centerX - this.windowWidth / 2;
        final int y = this.centerY - this.windowHeight / 2;

        GlStateManager.color(1F, 1F, 1F, 1F - this.fade / 255F);
        GlStateManager.enableBlend();

        TEXTURE_MANAGER.bindTexture(BACKGROUND_TEXTURE);
        drawModalRectWithCustomSizedTexture(x + 4, y + 4, 0, 0, this.windowWidth - 8, this.windowHeight - 8, BACKGROUND.getIconWidth(), BACKGROUND.getIconHeight());
        TEXTURE_MANAGER.deleteTexture(BACKGROUND_TEXTURE);

        GlStateManager.color(1, 1, 1, 1);
        TEXTURE_MANAGER.bindTexture(WINDOW);
        this.drawTexturedModalRect(x, y, 0, 0, alphaWidth, half);
        this.drawTexturedModalRect(x, y + half, 0, 40, this.windowWidth, alphaHeight - 40);
        TEXTURE_MANAGER.deleteTexture(WINDOW);

        this.fontRenderer.drawString(Mappings.MENU_BUTTON_SKILLS, x + 8, y + 6, 0x404040);

        if (this.isSkillSelected(mouseX, mouseY)) {
            this.fade = Math.min(this.fade + 12F, 80F);
        } else {
            this.fade = Math.max(this.fade - 12F, 0F);
        }
    }

    protected void drawSkills() {
        GlStateManager.color(1, 1, 1, 1);

        for (final ISkill skill : this.skills.keySet()) {
            final List<Integer> positions = this.skills.get(skill);

            this.drawSkill(skill, positions.get(0), positions.get(1));
        }
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

            TEXTURE_MANAGER.bindTexture(WIDGETS);
            this.drawTexturedModalRect(posX - 4, posY - 4, 1, 155, 24, 24);
            TEXTURE_MANAGER.deleteTexture(WIDGETS);

            TEXTURE_MANAGER.bindTexture(texture);
            drawScaledCustomSizeModalRect(posX, posY, 0, 0, imageWidth, imageHeight, width, height, imageWidth, imageHeight);
            TEXTURE_MANAGER.deleteTexture(texture);
        }
    }

    protected boolean isSkillSelected(final int mouseX, final int mouseY) {
        return this.getSelectedSkill(mouseX, mouseY) != null;
    }

    protected ISkill getSelectedSkill(final int mouseX, final int mouseY) {
        for (final ISkill skill : this.skills.keySet()) {
            if (this.isSkillSelected(skill, mouseX, mouseY)) {
                return skill;
            }
        }

        return null;
    }

    protected boolean isSkillSelected(final ISkill skill, final int mouseX, final int mouseY) {
        final List<Integer> positions = this.skills.get(skill);

        return Math.abs(positions.get(0) - mouseX) <= 12 && Math.abs(positions.get(1) - mouseY) <= 12;
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
