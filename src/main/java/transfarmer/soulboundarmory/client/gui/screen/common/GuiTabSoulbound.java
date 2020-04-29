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
import transfarmer.soulboundarmory.client.gui.GuiXPBar;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.config.ClientConfig;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.ItemSoulbound;
import transfarmer.soulboundarmory.network.C2S.C2SBindSlot;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.util.ItemUtil;

import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

@SideOnly(CLIENT)
public abstract class GuiTabSoulbound extends GuiTab {
    @NotNull
    protected final Capability<? extends SoulboundCapability> key;
    protected SoulboundCapability capability;
    protected IItem item;
    protected GuiXPBar xpBar;
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
        this.xpBar = new GuiXPBar(this.capability);
        this.item = capability.getItemType();
        this.slot = this.mc.player.inventory.getSlotFor(capability.getEquippedItemStack());

        if (ClientConfig.getDisplaySliders() && this.displayXPBar()) {
            this.addButton(this.sliderRed = this.guiFactory.colorSlider(100, 0, ClientConfig.getRed(), Mappings.RED + ": "));
            this.addButton(this.sliderGreen = this.guiFactory.colorSlider(101, 1, ClientConfig.getGreen(), Mappings.GREEN + ": "));
            this.addButton(this.sliderBlue = this.guiFactory.colorSlider(102, 2, ClientConfig.getBlue(), Mappings.BLUE + ": "));
            this.addButton(this.sliderAlpha = this.guiFactory.colorSlider(103, 3, ClientConfig.getAlpha(), Mappings.ALPHA + ": "));
        }

        if (ItemUtil.getEquippedItemStack(this.mc.player, ItemSoulbound.class) != null) {
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
        if (ClientConfig.getAlpha() >= 26F / 255) {
            final int xp = capability.getDatum(this.item, XP);

            this.xpBar.drawXPBar(this.getXPBarX(), this.getXPBarY(), 182);

            if (this.isMouseOverLevel(mouseX, mouseY) && MainConfig.instance().getMaxLevel() >= 0) {
                this.drawHoveringText(String.format("%d/%d", this.capability.getDatum(LEVEL), MainConfig.instance().getMaxLevel()), mouseX, mouseY);
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
        super.mouseClicked(mouseX, mouseY, button);

        if (this.isMouseOverXPBar(mouseX, mouseY)) {
            if (!this.buttonList.contains(this.sliderAlpha)) {
                this.buttonList.add(this.sliderRed);
                this.buttonList.add(this.sliderGreen);
                this.buttonList.add(this.sliderBlue);
                this.buttonList.add(this.sliderAlpha);
                ClientConfig.setDisplaySliders(true);
            } else {
                this.buttonList.remove(this.sliderRed);
                this.buttonList.remove(this.sliderGreen);
                this.buttonList.remove(this.sliderBlue);
                this.buttonList.remove(this.sliderAlpha);
                ClientConfig.setDisplaySliders(false);
            }

            ClientConfig.instance().save();
            this.refresh();
        }
    }

    @Override
    public void handleMouseInput() {
        final int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        final int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        final int dWheel = Mouse.getDWheel() / 120;
        final int row = this.sliderMousedOver(mouseX, mouseY);

        super.handleMouseInput();

        if (row >= 0 && row <= 3) {
            final GuiSlider slider;
            final float value;

            if (row == 0) {
                ClientConfig.setRed(value = MathHelper.clamp(ClientConfig.getRed() + dWheel / 255F, 0, 1));
                slider = this.sliderRed;
            } else if (row == 1) {
                ClientConfig.setGreen(value = MathHelper.clamp(ClientConfig.getGreen() + dWheel / 255F, 0, 1));
                slider = this.sliderGreen;
            } else if (row == 2) {
                ClientConfig.setBlue(value = MathHelper.clamp(ClientConfig.getBlue() + dWheel / 255F, 0, 1));
                slider = this.sliderBlue;
            } else {
                ClientConfig.setAlpha(value = MathHelper.clamp(ClientConfig.getAlpha() + dWheel / 255F, 0, 1));
                slider = this.sliderAlpha;
            }

            if (slider != null) {
                slider.setValue(value * 255);
                slider.updateSlider();
            }
        } else {
            super.handleMouseWheel(dWheel * 120);
        }
    }

    @Override
    protected void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        if (this.sliderRed != null && (this.sliderRed.dragging || this.sliderGreen.dragging || this.sliderBlue.dragging || this.sliderAlpha.dragging)) {
            this.updateSettings();
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        ClientConfig.instance().save();
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
        ClientConfig.setRed(this.sliderRed.getValueInt() / 255F);
        ClientConfig.setGreen(this.sliderGreen.getValueInt() / 255F);
        ClientConfig.setBlue(this.sliderBlue.getValueInt() / 255F);
        ClientConfig.setAlpha(this.sliderAlpha.getValueInt() / 255F);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
