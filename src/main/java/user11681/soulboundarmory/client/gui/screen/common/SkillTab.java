package user11681.soulboundarmory.client.gui.screen.common;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.client.gui.RGBASlider;
import user11681.soulboundarmory.client.i18n.Mappings;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.skill.Skill;
import user11681.soulboundarmory.skill.SkillContainer;

import static user11681.soulboundarmory.component.statistics.StatisticType.SKILL_POINTS;

@Environment(EnvType.CLIENT)
public class SkillTab extends SoulboundTab {
    protected static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/block/andesite.png");
    protected static final Identifier WINDOW = new Identifier("textures/gui/advancements/window.png");
    protected static final Identifier WIDGETS = new Identifier("textures/gui/advancements/widgets.png");

    protected final Map<SkillContainer, List<Integer>> skills;
    protected SkillContainer selectedSkill;
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

    public SkillTab(final SoulboundComponent component, final List<ScreenTab> tabs) {
        super(Mappings.MENU_SKILLS, component, tabs);

        this.skills = new HashMap<>();
    }

    @Override
    protected Text getLabel() {
        return Mappings.MENU_SKILLS;
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

        final RGBASlider slider = this.rgbaSliders.get(0);

        if (slider != null && slider.x < this.x + this.windowWidth) {
            this.buttons.removeAll(this.options);
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
        this.renderBackground(BACKGROUND_TEXTURE, this.x, this.y, this.insideWidth, this.insideHeight, 0);

        this.setChroma(1F);
        TEXTURE_MANAGER.bindTexture(WINDOW);
        this.withZ(-200, () -> this.drawVerticalInterpolatedTexturedRect(x, y, 0, 0, 22, 126, 140, this.windowWidth, this.windowHeight));

        final int color = 0x404040;
        final int points = this.storage.getDatum(SKILL_POINTS);

        TEXT_RENDERER.draw(Mappings.MENU_SKILLS.toString(), x + 8, y + 6, color);

        if (points > 0) {
            final String text = String.format("%s: %d", Mappings.MENU_UNSPENT_POINTS, points);

            TEXT_RENDERER.draw(text, rightX - TEXT_RENDERER.getStringWidth(text), y + 6, color);
        }

        final float delta = 20F * partialTicks / 255F;

        this.chroma = (this.isSkillSelected(mouseX, mouseY)
                ? Math.max(this.chroma - delta, 175F / 255F)
                : Math.min(this.chroma + delta, 1F)
        );
    }

    protected void drawSkills(final int mouseX, final int mouseY) {
        final List<SkillContainer> skills = new ArrayList<>(this.skills.keySet());

        for (int i = 0, skillsLength = skills.size(); i < skillsLength; i++) {
            final SkillContainer skill = skills.get(i);

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

    protected void drawSkill(final SkillContainer skill, final int mouseX, final int mouseY) {
        final List<Integer> positions = this.skills.get(skill);

        if (positions != null) {
            this.drawSkill(skill, mouseX, mouseY, positions.get(0), positions.get(1));
        }
    }

    protected void drawSkill(final SkillContainer skill, final int mouseX, final int mouseY, final int X, final int Y) {
        final int width = 16;
        final int height = 16;
        final int offsetV = skill.isLearned() ? 26 : 0;
        final float[] color = new float[3];
        final int x = X - width / 2;
        final int y = Y - height / 2;

        if (skill == this.selectedSkill) {
            this.setChroma(1);

            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                this.drawTooltip(skill, x, y, offsetV);
            }

            color[0] = color[1] = color[2] = 1;
        } else {
            this.setChroma(this.chroma);
            this.addBlitOffset(-50);

            color[0] = color[1] = color[2] = this.chroma;
        }

        TEXTURE_MANAGER.bindTexture(WIDGETS);
        this.blit(x - 4, y - 4, 1, 155 - offsetV, 24, 24);

        RenderSystem.color3f(color[0], color[1], color[2]);
        skill.render(this, x, y, this.getBlitOffset());

        this.setBlitOffset(0);
    }

    protected void drawTooltip(final SkillContainer skill, final int centerX, final int centerY, final int offsetV) {
        final String name = skill.getName();
        List<String> tooltip = skill.getTooltip();
        int barWidth = 36 + TEXT_RENDERER.getStringWidth(name);

        if (tooltip != null) {
            int size = tooltip.size();

            if (size > 0) {
                final boolean learned = skill.isLearned();
                final boolean aboveCenter = centerY > this.centerY;
                final int direction = aboveCenter ? -1 : 1;
                final int y = centerY + (aboveCenter ? -16 : 14);
                final int textY = y + (aboveCenter ? -5 : 7);
                String string = "";

                if (!learned) {
                    final int cost = skill.getCost();
                    final Text plural = cost > 1 ? Mappings.MENU_POINTS : Mappings.MENU_POINT;
                    string = String.format(Mappings.MENU_SKILL_LEARN_COST.asFormattedString(), cost, plural);
                } else if (skill.canBeUpgraded()) {
                    string = String.format("%s %d", Mappings.MENU_LEVEL, skill.getLevel());
                }

                barWidth = 12 + Math.max(barWidth, 8 + TEXT_RENDERER.getStringWidth(string));
                tooltip = this.wrap(barWidth, tooltip.toArray(new String[0]));
                size = tooltip.size();
                barWidth = Math.max(barWidth, 8 + TEXT_RENDERER.getStringWidth(tooltip.stream().max(Comparator.comparingInt(String::length)).get()));
                final int tooltipHeight = 1 + (1 + size) * TEXT_RENDERER.fontHeight;

                if (!learned || skill.canBeUpgraded()) {
                    this.setChroma(1);

                    TEXTURE_MANAGER.bindTexture(WIDGETS);
                    this.drawHorizontalInterpolatedTexturedRect(centerX - 8, y + tooltipHeight, 0, 55, 2, 198, 200, barWidth, 20);

                    TEXT_RENDERER.draw(string, centerX - 3, textY + direction * (size + 1) * TEXT_RENDERER.fontHeight, 0x999999);
                }

                this.setChroma(1);

                TEXTURE_MANAGER.bindTexture(WIDGETS);
                this.drawInterpolatedTexturedRect(centerX - 8, y, 0, 55, 2, 57, 198, 73, 200, 75, barWidth, tooltipHeight);

                for (int i = 0; i < size; i++) {
                    TEXT_RENDERER.draw(tooltip.get(i), centerX - 3, textY + direction * i * TEXT_RENDERER.fontHeight, 0x999999);
                }
            }

            this.setChroma(1);

            TEXTURE_MANAGER.bindTexture(WIDGETS);
            this.drawHorizontalInterpolatedTexturedRect(x - 8, y - 2, 0, 29 - offsetV, 2, 198, 200, barWidth, 20);

            TEXT_RENDERER.draw(name, x + 24, y + 4, 0xFFFFFF);
        }
    }

    protected boolean isSkillSelected(final int mouseX, final int mouseY) {
        return this.getSelectedSkill(mouseX, mouseY) != null;
    }

    protected SkillContainer getSelectedSkill(final double mouseX, final double mouseY) {
        for (final SkillContainer skill : this.skills.keySet()) {
            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                return skill;
            }
        }

        return null;
    }

    protected boolean isMouseOverSkill(final SkillContainer skill, final double mouseX, final double mouseY) {
        final List<Integer> positions = this.skills.get(skill);

        return Math.abs(positions.get(0) - mouseX) <= 12 && Math.abs(positions.get(1) - mouseY) <= 12;
    }

    protected void setChroma(final float chroma) {
        RenderSystem.color3f(chroma, chroma, chroma);
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        final SkillContainer skill = this.getSelectedSkill(mouseX, mouseY);

        if (skill != null) {
            this.storage.upgradeSkill(skill);
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
        final List<SkillContainer> skills = this.storage.getSkills();

        for (final SkillContainer skill : skills) {
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

        for (final SkillContainer skill : skills) {
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
                    total += this.skills.get(this.storage.getSkill(other)).get(0);
                }

                x += total / dependencies.size();
            } else {
                x += this.centerX;
            }

            this.skills.put(skill, Arrays.asList(x, this.insideY + 24 + 32 * tier));
        }
    }
}
