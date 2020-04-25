package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.ISoulbound;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.skill.ISkill;
import transfarmer.soulboundarmory.util.CollectionUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SideOnly(CLIENT)
public class GuiTabSkills extends GuiTabSoulbound {
    protected static final Minecraft MINECRAFT = Minecraft.getMinecraft();
    protected static final FontRenderer FONT_RENDERER = MINECRAFT.fontRenderer;
    protected static final ItemModelMesher ITEM_MODEL_MESHER = MINECRAFT.getRenderItem().getItemModelMesher();
    protected static final TextureManager TEXTURE_MANAGER = MINECRAFT.getTextureManager();

    protected static final TextureAtlasSprite BACKGROUND = getSprite(Blocks.STONE, 5);
    protected static final ResourceLocation BACKGROUND_TEXTURE = getTexture(BACKGROUND);
    protected static final ResourceLocation WINDOW = new ResourceLocation("textures/gui/advancements/window.png");
    protected static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/advancements/widgets.png");
    protected final Map<ISkill, List<Integer>> skills;
    protected final Map<ISkill, Entry<ResourceLocation, BufferedImage>> textures;
    protected ISkill selectedSkill;
    protected float chroma;
    protected int partialTicksSinceRelease;
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
        this.textures = new LinkedHashMap<>();
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_BUTTON_SKILLS;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.chroma = 1;
        this.windowWidth = 256;
        this.windowHeight = 192;
        this.insideWidth = this.windowWidth - 23;
        this.insideHeight = this.windowHeight - 27;
        this.centerX = Math.max(this.button.endX + this.windowWidth / 2 + 4, this.width / 2);
        this.centerY = Math.min(this.getXPBarY() - 16 - this.windowHeight / 2, this.height / 2);
        this.insideX = this.centerX - this.insideWidth / 2;
        this.insideY = this.centerY - this.insideHeight / 2;
        this.x = this.centerX - this.windowHeight / 2;
        this.y = this.centerY - this.windowWidth / 2;

        final Map<Integer, Integer> tierOrders = new HashMap<>();
        final ISkill[] skills = this.capability.getSkills();
        int currentTierSkills;

        this.skills.clear();

        for (final ISkill skill : skills) {
            final ResourceLocation texture = skill.getTexture();
            final int tier = skill.getTier();

            this.textures.put(skill, new SimpleImmutableEntry<>(texture, readTexture(texture)));

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

        this.partialTicksSinceRelease += partialTicks;

        this.drawWindow(mouseX, mouseY);
        this.drawSkills(mouseX, mouseY);
    }

    public void drawWindow(final int mouseX, final int mouseY) {
        final int x = this.centerX - this.windowWidth / 2;
        final int y = this.centerY - this.windowHeight / 2;

        this.setChroma(this.chroma);
        GlStateManager.enableBlend();

        TEXTURE_MANAGER.bindTexture(BACKGROUND_TEXTURE);
        drawModalRectWithCustomSizedTexture(x + 4, y + 4, 0, 0, this.windowWidth - 8, this.windowHeight - 8, BACKGROUND.getIconWidth(), BACKGROUND.getIconHeight());
        TEXTURE_MANAGER.deleteTexture(BACKGROUND_TEXTURE);

        this.setChroma(1F);
        TEXTURE_MANAGER.bindTexture(WINDOW);
        this.drawVerticalInterpolatedTexturedRect(x, y, 0, 0, 22, 126, 140, this.windowWidth, this.windowHeight);
        TEXTURE_MANAGER.deleteTexture(WINDOW);

        FONT_RENDERER.drawString(Mappings.MENU_BUTTON_SKILLS, x + 8, y + 6, 0x404040);

        this.chroma = (this.isSkillSelected(mouseX, mouseY)
                ? Math.max(this.chroma - 12F / 255F, 175F / 255F)
                : Math.min(this.chroma + 12F / 255F, 1F)
        );
    }

    protected void drawSkills(final int mouseX, final int mouseY) {
        final ISkill[] skills = this.skills.keySet().toArray(new ISkill[0]);

        for (int i = 0, skillsLength = skills.length; i < skillsLength; i++) {
            final ISkill skill = skills[i];

            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                this.selectedSkill = skill;
            } else {
                this.drawSkill(skill, mouseX, mouseY);
            }

            if (i == skillsLength - 1 && this.selectedSkill != null) {
                this.drawSkill(this.selectedSkill, mouseX, mouseY);
            }
        }
    }

    protected void drawSkill(final ISkill skill, final int mouseX, final int mouseY) {
        final List<Integer> positions = this.skills.get(skill);

        if (positions != null) {
            this.drawSkill(skill, mouseX, mouseY, positions.get(0), positions.get(1));
        }
    }

    protected void drawSkill(final ISkill skill, final int mouseX, final int mouseY, int posX, int posY) {
        final Entry<ResourceLocation, BufferedImage> textures = this.textures.get(skill);
        final ResourceLocation texture = textures.getKey();
        final BufferedImage image = textures.getValue();

        if (image != null) {
            final int imageWidth = image.getWidth();
            final int imageHeight = image.getHeight();
            final int width = 16;
            final int height = 16;
            final int learned = skill.isLearned() ? 26 : 0;
            posX -= width / 2;
            posY -= height / 2;

            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                final String name = skill.getName();
                List<String> tooltip = skill.getTooltip();
                int barWidth = 36 + FONT_RENDERER.getStringWidth(name);

                this.setChroma(1);
                TEXTURE_MANAGER.bindTexture(WIDGETS);

                if (tooltip != null) {
                    int size = tooltip.size();

                    if (size > 0) {
                        tooltip = this.wrap(12 + barWidth, tooltip.toArray(new String[0]));
                        size = tooltip.size();

                        final int y = posY + (posY > this.centerY ? -16 : 14);

                        barWidth = 8 + FONT_RENDERER.getStringWidth(tooltip.stream().max(Comparator.comparingInt(String::length)).get());

                        this.drawInterpolatedTexturedRect(posX - 8, y, 0, 55, 2, 57, 198, 73, 200, 75, barWidth, 1 + (1 + size) * FONT_RENDERER.FONT_HEIGHT);

                        for (int i = 0; i < size; i++) {
                            this.drawString(FONT_RENDERER, tooltip.get(i), posX - 3, posY + 21 + i * FONT_RENDERER.FONT_HEIGHT, 0xFFFFFF);
                        }
                    }
                }

                TEXTURE_MANAGER.bindTexture(WIDGETS);
                this.drawHorizontalInterpolatedTexturedRect(posX - 8, posY - 2, 0, 29 - learned, 2, 198, 200, barWidth, 20);
                TEXTURE_MANAGER.deleteTexture(WIDGETS);

                this.drawString(FONT_RENDERER, name, posX + 24, posY + 4, 0xFFFFFF);
            }

            if (skill == this.selectedSkill) {
                this.setChroma(1);
            } else {
                this.setChroma(this.chroma);
            }

            TEXTURE_MANAGER.bindTexture(WIDGETS);
            this.drawTexturedModalRect(posX - 4, posY - 4, 1, 155 - learned, 24, 24);
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
            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                return skill;
            }
        }

        return null;
    }

    protected boolean isMouseOverSkill(final ISkill skill, final int mouseX, final int mouseY) {
        final List<Integer> positions = this.skills.get(skill);

        return Math.abs(positions.get(0) - mouseX) <= 12 && Math.abs(positions.get(1) - mouseY) <= 12;
    }

    protected void setChroma(final float chroma) {
        GlStateManager.color(chroma, chroma, chroma);
    }

    protected List<String> wrap(final int width, final String... strings) {
        final List<String> lines = new ArrayList<>();

        for (final String string : strings) {
            lines.addAll(this.wrap(width, string));
        }

        return lines;
    }

    protected List<String> wrap(final int width, final String string) {
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

    @Override
    public void actionPerformed(@NotNull final GuiButton button) {
        super.actionPerformed(button);
    }

    protected void drawInterpolatedTexturedRect(int x, int y, final int startU, final int startV, final int middleU,
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

    protected void drawHorizontalInterpolatedTexturedRect(int x, final int y, final int startU, final int startV,
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

    protected int drawHorizontalInterpolatedTexturedRect(int x, final int y, final int startV, final int middleU,
                                                         final int endU, int width, final int height) {
        while (width > 0) {
            final int middleWidth = Math.min(width, endU - middleU);

            this.drawTexturedModalRect(x, y, middleU, startV, middleWidth, height);
            x += middleWidth;
            width -= middleWidth;
        }

        return x;
    }

    protected void drawVerticalInterpolatedTexturedRect(final int x, int y, final int startU, final int startV,
                                                        final int middleV, final int endV, final int finalV,
                                                        final int width, int height) {
        final int startHeight = middleV - startV;
        final int finalHeight = finalV - endV;

        this.drawTexturedModalRect(x, y, startU, startV, width, startHeight);
        height -= startHeight + finalHeight;
        y += startHeight;
        y = drawVerticalInterpolatedTexturedRect(x, y, startU, middleV, endV, width, height);

        this.drawTexturedModalRect(x, y, startU, endV, width, finalHeight);
    }

    protected int drawVerticalInterpolatedTexturedRect(final int x, int y, final int startU, final int middleV,
                                                       final int endV, final int width, int height) {
        while (height > 0) {
            final int middleHeight = Math.min(height, endV - middleV);

            this.drawTexturedModalRect(x, y, startU, middleV, width, middleHeight);
            y += middleHeight;
            height -= middleHeight;
        }
        return y;
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
