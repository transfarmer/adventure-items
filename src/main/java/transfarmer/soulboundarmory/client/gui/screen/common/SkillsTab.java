package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.client.renderer.RenderSystem;
import net.minecraft.client.renderer.texture.SpriteAtlasTexture;
import net.minecraft.init.Blocks;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.capabilities.Component;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.client.i18n.Mappings;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.statistics.StatisticType.SKILL_POINTS;

@Environment(CLIENT)
public class SkillsTab extends SoulboundTab {
    protected static final SpriteAtlasTexture BACKGROUND = ExtendedScreen.getSprite(Blocks.STONE, 5);
    protected static final Identifier BACKGROUND_TEXTURE = ExtendedScreen.getTexture(BACKGROUND);
    protected static final Identifier WINDOW = new Identifier("textures/gui/advancements/window.png");
    protected static final Identifier WIDGETS = new Identifier("textures/gui/advancements/widgets.png");

    protected final Map<Skill, List<Integer>> skills;
    protected Skill selectedSkill;
    protected float chroma;
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

    public SkillsTab(final Component<? extends ISoulboundComponent> key, final List<ScreenTab> tabs) {
        super(key, tabs);

        this.skills = new LinkedHashMap<>();
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_BUTTON_SKILLS;
    }

    @Override
    public void init() {
        super.init();

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

        this.updateIcons();
    }

    @Override
    protected void initOptions() {
        super.initOptions();

        if (this.alphaSlider != null && this.alphaSlider.x < this.x + this.windowWidth) {
            this.buttonList.removeAll(this.options);
        }
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        this.drawWindow(mouseX, mouseY, partialTicks);
        this.drawSkills(mouseX, mouseY);
    }

    public void drawWindow(final int mouseX, final int mouseY, final float partialTicks) {
        final int x = this.centerX - this.windowWidth / 2;
        final int y = this.centerY - this.windowHeight / 2;
        final int rightX = this.centerX + this.insideWidth / 2;

        this.setChroma(this.chroma);
        RenderSystem.enableBlend();

        TEXTURE_MANAGER.bindTexture(BACKGROUND_TEXTURE);
        this.withZ(-250, () -> this.drawRectWithCustomSizedTexture(x + 4, y + 4, 0, 0, this.windowWidth - 8, this.windowHeight - 8, BACKGROUND.getIconWidth(), BACKGROUND.getIconHeight()));

        this.setChroma(1F);
        TEXTURE_MANAGER.bindTexture(WINDOW);
        this.withZ(-200, () -> this.drawVerticalInterpolatedTexturedRect(x, y, 0, 0, 22, 126, 140, this.windowWidth, this.windowHeight));

        final int color = 0x404040;
        final int points = this.component.getDatum(this.item, SKILL_POINTS);

        TEXT_RENDERER.drawString(Mappings.MENU_BUTTON_SKILLS, x + 8, y + 6, color);

        if (points > 0) {
            final String text = String.format("%s: %d", Mappings.MENU_UNSPENT_POINTS, points);

            TEXT_RENDERER.drawString(text, rightX - TEXT_RENDERER.getStringWidth(text), y + 6, color);
        }

        final float delta = 20F * partialTicks / 255F;

        this.chroma = (this.isSkillSelected(mouseX, mouseY)
                ? Math.max(this.chroma - delta, 175F / 255F)
                : Math.min(this.chroma + delta, 1F)
        );
    }

    protected void drawSkills(final int mouseX, final int mouseY) {
        final Skill[] skills = this.skills.keySet().toArray(new Skill[0]);

        for (int i = 0, skillsLength = skills.length; i < skillsLength; i++) {
            final Skill skill = skills[i];

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

    protected void drawSkill(final Skill skill, final int mouseX, final int mouseY) {
        final List<Integer> positions = this.skills.get(skill);

        if (positions != null) {
            this.drawSkill(skill, mouseX, mouseY, positions.get(0), positions.get(1));
        }
    }

    protected void drawSkill(final Skill skill, final int mouseX, final int mouseY, final int X, final int Y) {
        final int width = 16;
        final int height = 16;
        final int offsetV = skill.isLearned() ? 26 : 0;
        final int color;
        final int x = X - width / 2;
        final int y = Y - height / 2;

        if (skill == this.selectedSkill) {
            this.setChroma(1);

            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                this.drawTooltip(skill, x, y, offsetV);
            }

            color = 0xFFFFFFFF;
        } else {
            this.setChroma(this.chroma);
            this.blitOffset -= 50;

            color = new Color(this.chroma, this.chroma, this.chroma).getRGB();
        }

        TEXTURE_MANAGER.bindTexture(WIDGETS);
        this.drawTexturedModalRect(x - 4, y - 4, 1, 155 - offsetV, 24, 24);

        skill.getGUI().render(x, y, color, this.blitOffset);

        this.blitOffset = 0;
    }

    protected void drawTooltip(final Skill skill, final int getX(), final int getY(), final int offsetV) {
        final String name = skill.getName();
        List<String> tooltip = skill.getTooltip();
        int barWidth = 36 + TEXT_RENDERER.getStringWidth(name);

        if (tooltip != null) {
            int size = tooltip.size();

            if (size > 0) {
                final boolean learned = skill.isLearned();
                final boolean aboveCenter = getY() > this.centerY;
                final int direction = aboveCenter ? -1 : 1;
                final int y = getY() + (aboveCenter ? -16 : 14);
                final int textY = y + (aboveCenter ? -5 : 7);
                String string = "";

                if (!learned) {
                    final int cost = skill.getCost();
                    final String plural = cost > 1 ? Mappings.MENU_POINTS : Mappings.MENU_POINT;
                    string = String.format(Mappings.MENU_SKILL_LEARN_COST, cost, plural);
                } else if (skill instanceof Skill) {
                    string = String.format("%s %d", Mappings.MENU_LEVEL, ((Skill) skill).getLevel());
                }

                barWidth = 12 + Math.max(barWidth, 8 + TEXT_RENDERER.getStringWidth(string));
                tooltip = this.wrap(barWidth, tooltip.toArray(new String[0]));
                size = tooltip.size();
                barWidth = Math.max(barWidth, 8 + TEXT_RENDERER.getStringWidth(tooltip.stream().max(Comparator.comparingInt(String::length)).get()));
                final int tooltipHeight = 1 + (1 + size) * TEXT_RENDERER.FONT_HEIGHT;

                if (!learned || skill instanceof Skill) {
                    this.setChroma(1);

                    TEXTURE_MANAGER.bindTexture(WIDGETS);
                    this.drawHorizontalInterpolatedTexturedRect(getX() - 8, y + tooltipHeight, 0, 55, 2, 198, 200, barWidth, 20);

                    TEXT_RENDERER.drawString(string, getX() - 3, textY + direction * (size + 1) * TEXT_RENDERER.FONT_HEIGHT, 0x999999);
                }

                this.setChroma(1);

                TEXTURE_MANAGER.bindTexture(WIDGETS);
                this.drawInterpolatedTexturedRect(getX() - 8, y, 0, 55, 2, 57, 198, 73, 200, 75, barWidth, tooltipHeight);

                for (int i = 0; i < size; i++) {
                    TEXT_RENDERER.drawString(tooltip.get(i), getX() - 3, textY + direction * i * TEXT_RENDERER.FONT_HEIGHT, 0x999999);
                }
            }

            this.setChroma(1);

            TEXTURE_MANAGER.bindTexture(WIDGETS);
            this.drawHorizontalInterpolatedTexturedRect(getX() - 8, getY() - 2, 0, 29 - offsetV, 2, 198, 200, barWidth, 20);

            TEXT_RENDERER.drawString(name, getX() + 24, getY() + 4, 0xFFFFFF);
        }
    }

    protected boolean isSkillSelected(final int mouseX, final int mouseY) {
        return this.getSelectedSkill(mouseX, mouseY) != null;
    }

    protected Skill getSelectedSkill(final int mouseX, final int mouseY) {
        for (final Skill skill : this.skills.keySet()) {
            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                return skill;
            }
        }

        return null;
    }

    protected boolean isMouseOverSkill(final Skill skill, final int mouseX, final int mouseY) {
        final List<Integer> positions = this.skills.get(skill);

        return Math.abs(positions.get(0) - mouseX) <= 12 && Math.abs(positions.get(1) - mouseY) <= 12;
    }

    protected void setChroma(final float chroma) {
        RenderSystem.color(chroma, chroma, chroma);
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        final Skill skill = this.getSelectedSkill(mouseX, mouseY);

        if (skill != null) {
            this.component.upgradeSkill(this.item, skill);
        }
        return false;
    }

    @Override
    public void refresh() {
        final float chroma = this.chroma;

        super.refresh();

        this.chroma = chroma;
    }

    protected void updateIcons() {
        this.skills.clear();

        final Map<Integer, List<Integer>> tierOrders = new HashMap<>();
        final List<Skill> skills = this.component.getSkills(this.item);

        for (final Skill skill : skills) {
            final int tier = skill.getTier();
            final List<Integer> data = tierOrders.getOrDefault(tier, new ArrayList<>(Collections.nCopies(3, 0)));

            if (!tierOrders.containsValue(data)) {
                tierOrders.put(tier, data);
            }

            data.set(1, data.get(1) + 1);
        }

        int width = 2;

        for (final int tier : tierOrders.keySet()) {
            final int tiers = tierOrders.get(tier).get(1);

            if (tier != 0 && tiers != 0) {
                width += tiers - 1;
            }
        }

        for (final Skill skill : skills) {
            final int tier = skill.getTier();
            final List<Integer> data = tierOrders.getOrDefault(tier, new ArrayList<>(Collections.nCopies(3, 0)));
            data.set(0, data.get(0) + 1);

            if (!tierOrders.containsValue(data)) {
                tierOrders.put(tier, data);
            }

            final int spacing = !skill.hasDependencies() ? width * 24 : 48;
            final int offset = (1 - data.get(1)) * spacing / 2;
            int x = offset + (data.get(0) - 1) * spacing;

            final List<Skill> dependencies = skill.getDependencies();

            if (skill.hasDependencies()) {
                int total = 0;

                for (final Skill other : dependencies) {
                    total += this.skills.get(other).get(0);
                }

                x += total / dependencies.size();
            } else {
                x += this.centerX;
            }

            this.skills.put(skill, Arrays.asList(x, this.insideY + 24 + 32 * tier));
        }
    }
}
