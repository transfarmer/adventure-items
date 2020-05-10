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
import transfarmer.soulboundarmory.client.gui.GuiButtonExtended;
import transfarmer.soulboundarmory.client.gui.GuiXPBar;
import transfarmer.soulboundarmory.client.gui.GuiXPBar.Style;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.config.ClientConfig;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.ItemSoulbound;
import transfarmer.soulboundarmory.network.C2S.C2SBindSlot;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.util.ItemUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

@SideOnly(CLIENT)
public abstract class GuiTabSoulbound extends GuiTab {
    protected static final NumberFormat FORMAT = DecimalFormat.getInstance();
    @NotNull
    protected final Capability<? extends SoulboundCapability> key;
    protected SoulboundCapability capability;
    protected IItem item;
    protected GuiXPBar xpBar;
    protected List<GuiButton> options;
    protected GuiSlider sliderRed;
    protected GuiSlider sliderGreen;
    protected GuiSlider sliderBlue;
    protected GuiSlider sliderAlpha;
    protected GuiButtonExtended styleButton;
    protected int slot;

    public GuiTabSoulbound(@NotNull final Capability<? extends SoulboundCapability> key, final List<GuiTab> tabs) {
        super(tabs);

        this.key = key;
        this.options = new ArrayList<>(4);
    }

    @Override
    public void initGui() {
        this.displayTabs = ItemUtil.getEquippedItemStack(this.mc.player.inventory, ItemSoulbound.class) != null;

        super.initGui();

        this.capability = this.mc.player.getCapability(this.key, null);
        this.capability.setCurrentTab(this.index);
        this.xpBar = new GuiXPBar(this.capability);
        this.item = capability.getItemType();
        this.slot = this.mc.player.inventory.getSlotFor(capability.getEquippedItemStack());

        this.initOptions();

        if (this.displayTabs) {
            final String text = this.slot != capability.getBoundSlot()
                    ? Mappings.MENU_BUTTON_BIND
                    : Mappings.MENU_BUTTON_UNBIND;
            final int width = Math.max(this.button.width, FONT_RENDERER.getStringWidth(text) + 8);
            final int x = this.button.endX - width;

            this.addButton(new GuiButton(22, x, this.height - this.height / 16 - 20, width, 20, text)
            );
        }
    }

    protected void initOptions() {
        if (ClientConfig.getDisplayOptions() && this.displayXPBar()) {
            this.options.add(this.addButton(this.sliderRed = this.colorSlider(1000, 0, ClientConfig.getRed(), Mappings.RED + ": ")));
            this.options.add(this.addButton(this.sliderGreen = this.colorSlider(1001, 1, ClientConfig.getGreen(), Mappings.GREEN + ": ")));
            this.options.add(this.addButton(this.sliderBlue = this.colorSlider(1002, 2, ClientConfig.getBlue(), Mappings.BLUE + ": ")));
            this.options.add(this.addButton(this.sliderAlpha = this.colorSlider(1003, 3, ClientConfig.getAlpha(), Mappings.ALPHA + ": ")));
            this.options.add(this.addButton(this.styleButton = this.optionButton(1004, 4, String.format("%s: %s", Mappings.XP_BAR_STYLE, ClientConfig.getStyle().toString()))));
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.zLevel -= 500;
        this.drawDefaultBackground();
        this.zLevel += 500;

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
        return this.item != null;
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
            if (mouseX >= this.getOptionX() && mouseX <= this.getOptionX() + 100
                    && mouseY >= this.getOptionY(slider) && mouseY <= this.getOptionY(slider) + 20) {
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
                this.buttonList.addAll(this.options);
                ClientConfig.setDisplayOptions(true);
            } else {
                this.buttonList.removeAll(this.options);
                ClientConfig.setDisplayOptions(false);
            }

            ClientConfig.instance().save();
            this.refresh();
        } else if (this.styleButton != null && this.styleButton.isMouseHoveringOver()) {
            this.cycleStyle(-1);
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
            case 1000:
            case 1001:
            case 1002:
            case 1003:
                this.updateSettings();
                break;
            case 1004:
                this.cycleStyle(1);
        }
    }

    protected void cycleStyle(final int change) {
        int index = (Style.STYLES.indexOf(ClientConfig.getStyle()) + change) % Style.AMOUNT;

        if (index < 0) {
            this.cycleStyle(Style.AMOUNT + index);
        } else {
            ClientConfig.setStyle(Style.STYLES.get(index));
            this.refresh();
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

    public GuiButton centeredButton(final int id, final int y, final int buttonWidth, final String text) {
        return new GuiButton(id, (this.width - buttonWidth) / 2, y, buttonWidth, 20, text);
    }

    public GuiButton squareButton(final int id, final int x, final int y, final String text) {
        return new GuiButton(id, x - 10, y - 10, 20, 20, text);
    }

    public GuiButton resetButton(final int id) {
        return new GuiButton(id, this.width - this.width / 24 - 112, this.height - this.height / 16 - 20, 112, 20, Mappings.MENU_BUTTON_RESET);
    }

    public GuiButtonExtended optionButton(final int id, final int row, final String text) {
        return new GuiButtonExtended(id, this.getOptionX(), this.getOptionY(row), 100, 20, text);
    }

    public GuiSlider colorSlider(final int id, final int row, final double currentValue, final String text) {
        return new GuiSlider(id, this.getOptionX(), this.getOptionY(row), 100, 20, text, "", 0, 255, currentValue * 255, false, true);
    }

    public GuiButton[] addPointButtons(final int id, final int rows, final int points) {
        final GuiButton[] buttons = new GuiButton[rows];
        final int start = (this.height - (rows - 1) * this.height / 16) / 2;

        for (int row = 0; row < rows; row++) {
            buttons[row] = squareButton(id + row, (this.width + 162) / 2, start + row * this.height / 16 + 4, "+");
            buttons[row].enabled = points > 0;
        }

        return buttons;
    }

    public GuiButton[] removePointButtons(final int id, final int rows) {
        final GuiButton[] buttons = new GuiButton[rows];
        final int start = (this.height - (rows - 1) * this.height / 16) / 2;

        for (int row = 0; row < rows; row++) {
            buttons[row] = squareButton(id + row, (this.width + 162) / 2 - 20, start + row * this.height / 16 + 4, "-");
        }

        return buttons;
    }

    public int getOptionX() {
        return Math.round(this.width * (1 - 1 / 24F)) - 100;
    }

    public int getOptionY(final int row) {
        return this.height / 16 + Math.max(this.height / 16 * row, 30 * row);
    }

    public void drawMiddleAttribute(final String format, final double value, final int row, final int rows) {
        FONT_RENDERER.drawString(String.format(format, FORMAT.format(value)), (this.width - 182) / 2, this.getHeight(rows, row), 0xFFFFFF);
    }
}
