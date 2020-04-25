package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.config.ColorConfig;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.ISoulboundItem;
import transfarmer.soulboundarmory.network.server.C2SBindSlot;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.util.ItemUtil;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.Main.ResourceLocations.XP_BAR;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

@SideOnly(CLIENT)
public abstract class GuiTabSoulbound extends GuiTab {
    @NotNull
    private final Capability<? extends SoulboundCapability> key;
    protected IItem item;
    protected SoulboundCapability capability;
    protected GuiSlider sliderRed;
    protected GuiSlider sliderGreen;
    protected GuiSlider sliderBlue;
    protected GuiSlider sliderAlpha;
    protected int slot;

    public GuiTabSoulbound(@NotNull final Capability<? extends SoulboundCapability> key, final List<GuiTab> tabs) {
        super(tabs);

        this.key = key;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.capability = this.mc.player.getCapability(this.key, null);
        this.capability.setCurrentTab(this.index);
        this.item = capability.getItemType();
        this.slot = this.mc.player.inventory.getSlotFor(capability.getEquippedItemStack());

        if (ColorConfig.getDisplaySliders() && this.displayXPBar()) {
            this.addButton(this.sliderRed = this.guiFactory.colorSlider(100, 0, ColorConfig.getRed(), Mappings.RED + ": "));
            this.addButton(this.sliderGreen = this.guiFactory.colorSlider(101, 1, ColorConfig.getGreen(), Mappings.GREEN + ": "));
            this.addButton(this.sliderBlue = this.guiFactory.colorSlider(102, 2, ColorConfig.getBlue(), Mappings.BLUE + ": "));
            this.addButton(this.sliderAlpha = this.guiFactory.colorSlider(103, 3, ColorConfig.getAlpha(), Mappings.ALPHA + ": "));
        }

        if (ItemUtil.getEquippedItemStack(this.mc.player, ISoulboundItem.class) != null) {
            final int width = Math.max(112, Math.round(this.width / 7.5F));

            this.addButton(new GuiButton(22, Math.min(this.getXPBarX() - width, this.width / 24), height - height / 16 - 20, width, 20, this.slot != capability.getBoundSlot()
                    ? Mappings.MENU_BUTTON_BIND
                    : Mappings.MENU_BUTTON_UNBIND)
            );
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.displayXPBar()) {
            this.drawXPBar(mouseX, mouseY);
        }
    }

    protected void drawXPBar(final int mouseX, final int mouseY) {
        if (ColorConfig.getAlpha() >= 26F / 255) {
            final int length = 182;
            final int barX = this.getXPBarX();
            final int barY = this.getXPBarY();
            final int xp = capability.getDatum(this.item, XP);

            GlStateManager.color(ColorConfig.getRed(), ColorConfig.getGreen(), ColorConfig.getBlue(), ColorConfig.getAlpha());
            TEXTURE_MANAGER.bindTexture(XP_BAR);
            this.drawTexturedModalRect(barX, barY, 0, 0, length, 5);
            this.drawTexturedModalRect(barX, barY, 0, 5, this.capability.canLevelUp(this.item)
                    ? Math.min(length, Math.round((float) xp / capability.getNextLevelXP(this.item) * length))
                    : length, 5
            );
            TEXTURE_MANAGER.deleteTexture(XP_BAR);

            final int level = this.capability.getDatum(this.item, LEVEL);
            final String levelString = String.format("%d", level);
            final int levelX = barX + (length - FONT_RENDERER.getStringWidth(levelString)) / 2;
            final int levelY = barY - 6;
            final int color = new Color(ColorConfig.getRed(), ColorConfig.getGreen(), ColorConfig.getBlue(), ColorConfig.getAlpha()).getRGB();

            FONT_RENDERER.drawString(levelString, levelX + 1, levelY, 0);
            FONT_RENDERER.drawString(levelString, levelX - 1, levelY, 0);
            FONT_RENDERER.drawString(levelString, levelX, levelY + 1, 0);
            FONT_RENDERER.drawString(levelString, levelX, levelY - 1, 0);
            FONT_RENDERER.drawString(levelString, levelX, levelY, color);

            if (this.isMouseOverLevel(mouseX, mouseY) && MainConfig.instance().getMaxLevel() >= 0) {
                this.drawHoveringText(String.format("%d/%d", level, MainConfig.instance().getMaxLevel()), mouseX, mouseY);
            } else if (this.isMouseOverXPBar(mouseX, mouseY)) {
                this.drawHoveringText(this.capability.canLevelUp(this.item)
                        ? String.format("%d/%d", xp, capability.getNextLevelXP(this.item))
                        : String.format("%d", xp), mouseX, mouseY);
            }

            GlStateManager.disableLighting();
        }
    }

    protected boolean displayXPBar() {
        return this.capability.getItemType() != null;
    }

    protected boolean isMouseOverXPBar(final int mouseX, final int mouseY) {
        final int barX = this.getXPBarX();
        final int barY = this.getXPBarY();

        return this.displayXPBar() && mouseX >= barX && mouseX <= barX + 182 && mouseY >= barY && mouseY <= barY + 4;
    }

    protected int getXPBarY() {
        return this.height - 29;
    }

    protected int getXPBarX() {
        return (this.width - 182) / 2;
    }

    protected boolean isMouseOverLevel(final int mouseX, final int mouseY) {
        final String levelString = "" + this.capability.getDatum(this.item, LEVEL);

        final int levelLeftX = (this.width - FONT_RENDERER.getStringWidth(levelString)) / 2;
        final int levelTopY = height - 35;

        return mouseX >= levelLeftX && mouseX <= levelLeftX + FONT_RENDERER.getStringWidth(levelString)
                && mouseY >= levelTopY && mouseY <= levelTopY + FONT_RENDERER.FONT_HEIGHT;
    }

    protected int sliderMousedOver(final int mouseX, final int mouseY) {
        for (int slider = 0; slider < 4; slider++) {
            if (mouseX >= this.guiFactory.getColorSliderX() && mouseX <= this.guiFactory.getColorSliderX() + 100
                    && mouseY >= this.guiFactory.getColorSliderY(slider) && mouseY <= this.guiFactory.getColorSliderY(slider) + 20) {
                return slider;
            }
        }

        return -1;
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int button) {
        if (this.isMouseOverXPBar(mouseX, mouseY)) {
            if (!this.buttonList.contains(this.sliderAlpha)) {
                this.buttonList.add(this.sliderRed);
                this.buttonList.add(this.sliderGreen);
                this.buttonList.add(this.sliderBlue);
                this.buttonList.add(this.sliderAlpha);
                ColorConfig.instance().setDisplaySliders(true);
            } else {
                this.buttonList.remove(this.sliderRed);
                this.buttonList.remove(this.sliderGreen);
                this.buttonList.remove(this.sliderBlue);
                this.buttonList.remove(this.sliderAlpha);
                ColorConfig.instance().setDisplaySliders(false);
            }

            ColorConfig.instance().save();
            this.refresh();
        } else {
            try {
                super.mouseClicked(mouseX, mouseY, button);
            } catch (final IOException exception) {
                Main.LOGGER.error(exception);
            }
        }
    }

    @Override
    public void handleMouseInput() {
        final int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        final int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        final int dWheel = Mouse.getDWheel() / 120;
        final int row = this.sliderMousedOver(mouseX, mouseY);

        if (row >= 0 && row <= 3) {
            final GuiSlider slider;
            final float value;

            if (row == 0) {
                ColorConfig.instance().setRed(value = MathHelper.clamp(ColorConfig.getRed() + dWheel / 255F, 0, 1));
                slider = this.sliderRed;
            } else if (row == 1) {
                ColorConfig.instance().setGreen(value = MathHelper.clamp(ColorConfig.getGreen() + dWheel / 255F, 0, 1));
                slider = this.sliderGreen;
            } else if (row == 2) {
                ColorConfig.instance().setBlue(value = MathHelper.clamp(ColorConfig.getBlue() + dWheel / 255F, 0, 1));
                slider = this.sliderBlue;
            } else {
                ColorConfig.instance().setAlpha(value = MathHelper.clamp(ColorConfig.getAlpha() + dWheel / 255F, 0, 1));
                slider = this.sliderAlpha;
            }

            if (slider != null) {
                slider.setValue(value * 255);
                slider.updateSlider();
            }
        } else {
            super.handleMouseInput();
            super.handleMouseWheel(dWheel * 120);
        }
    }

    @Override
    protected void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick) {
        if (this.sliderRed != null && (this.sliderRed.dragging || this.sliderGreen.dragging || this.sliderBlue.dragging || this.sliderAlpha.dragging)) {
            this.updateSettings();
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        ColorConfig.instance().save();
    }

    @Override
    public void actionPerformed(@NotNull GuiButton button) {
        super.actionPerformed(button);

        switch (button.id) {
            case 16:
            case 17:
            case 18:
            case 19:
                this.setTab(button.id - 16);
                break;
            case 22:
                Main.CHANNEL.sendToServer(new C2SBindSlot(this.capability.getType(), this.slot));
                break;
            case 100:
            case 101:
            case 102:
            case 103:
                this.updateSettings();
        }
    }

    private void updateSettings() {
        ColorConfig.instance().setRed(this.sliderRed.getValueInt() / 255F);
        ColorConfig.instance().setGreen(this.sliderGreen.getValueInt() / 255F);
        ColorConfig.instance().setBlue(this.sliderBlue.getValueInt() / 255F);
        ColorConfig.instance().setAlpha(this.sliderAlpha.getValueInt() / 255F);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
