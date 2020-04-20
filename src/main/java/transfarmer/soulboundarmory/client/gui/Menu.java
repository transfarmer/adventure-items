package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiSlider;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.ICapabilityEnchantable;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.client.KeyBindings;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.config.ColorConfig;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.ISoulboundItem;
import transfarmer.soulboundarmory.network.server.C2SBindSlot;
import transfarmer.soulboundarmory.network.server.C2SEnchant;
import transfarmer.soulboundarmory.network.server.C2SItemType;
import transfarmer.soulboundarmory.network.server.C2SReset;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;
import transfarmer.soulboundarmory.util.IndexedMap;
import transfarmer.soulboundarmory.util.ItemUtil;

import javax.annotation.Nonnull;
import javax.annotation.meta.When;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import static transfarmer.soulboundarmory.Main.ResourceLocations.XP_BAR;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ENCHANTMENT;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

public abstract class Menu extends GuiScreen {
    protected final GUIFactory guiFactory;
    protected final Renderer renderer;
    protected final GuiButton[] tabs;
    protected final Item[] consumableItems;
    protected final ICapabilityEnchantable capability;
    protected final IItem item;
    protected final int slot;
    protected GuiSlider sliderRed;
    protected GuiSlider sliderGreen;
    protected GuiSlider sliderBlue;
    protected GuiSlider sliderAlpha;

    public Menu(final int tabs, final Item... consumableItems) {
        this.mc = Minecraft.getMinecraft();
        this.guiFactory = new GUIFactory();
        this.renderer = new Renderer();
        this.tabs = new GuiButton[tabs];
        this.consumableItems = consumableItems;

        ItemStack equippedItemStack = ItemUtil.getClassEquippedItemStack(this.mc.player, ISoulboundItem.class);

        if (equippedItemStack == null) {
            equippedItemStack = ItemUtil.getEquippedItemStack(this.mc.player, this.consumableItems);
        }

        this.capability = SoulItemHelper.getFirstCapability(this.mc.player, equippedItemStack);
        this.item = this.capability.getItemType();
        this.slot = this.mc.player.inventory.getSlotFor(equippedItemStack);
    }

    @Override
    public void initGui() {
        if (ColorConfig.getDisplaySliders() && this.displayXPBar()) {
            this.addButton(this.sliderRed = this.guiFactory.colorSlider(100, 0, ColorConfig.getRed(), Mappings.RED + ": "));
            this.addButton(this.sliderGreen = this.guiFactory.colorSlider(101, 1, ColorConfig.getGreen(), Mappings.GREEN + ": "));
            this.addButton(this.sliderBlue = this.guiFactory.colorSlider(102, 2, ColorConfig.getBlue(), Mappings.BLUE + ": "));
            this.addButton(this.sliderAlpha = this.guiFactory.colorSlider(103, 3, ColorConfig.getAlpha(), Mappings.ALPHA + ": "));
        }

        if (ItemUtil.getClassEquippedItemStack(this.mc.player, ISoulboundItem.class) != null) {
            this.addButton(new GuiButton(22, width / 24, height - height / 16 - 20, 96, 20, this.slot != capability.getBoundSlot()
                    ? Mappings.MENU_BUTTON_BIND
                    : Mappings.MENU_BUTTON_UNBIND)
            );
        }
    }

    protected void displayEnchantments() {
        final IndexedMap<Enchantment, Integer> enchantments = this.capability.getEnchantments(this.item);
        final int size = enchantments.size();
        final GuiButton resetButton = this.addButton(guiFactory.resetButton(21));
        final GuiButton[] removePointButtons = new GuiButton[size];

        for (int row = 0; row < size; row++) {
            removePointButtons[row] = this.addButton(guiFactory.squareButton(2000 + Enchantment.getEnchantmentID(enchantments.getKey(row)), (width + 162) / 2 - 20, (row + 1) * height / 16 + 4, "-"));
        }

        resetButton.enabled = this.capability.getDatum(this.item, SPENT_ENCHANTMENT_POINTS) > 0;

        for (int row = 0; row < size; row++) {
            final GuiButton button = this.addButton(this.guiFactory.squareButton(1000 + Enchantment.getEnchantmentID(enchantments.getKey(row)), (width + 162) / 2, (row + 1) * height / 16 + 4, "+"));
            button.enabled = this.capability.getDatum(this.item, ENCHANTMENT_POINTS) > 0;
        }

        for (int i = 0; i < size; i++) {
            removePointButtons[i].enabled = enchantments.getValue(i) > 0;
        }
    }

    protected void displaySkills() {
        this.mc.displayGuiScreen(new GuiSkills(this.capability));
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.displayXPBar()) {
            this.drawXPBar(mouseX, mouseY);
        }
    }

    protected void drawEnchantments() {
        final IndexedMap<Enchantment, Integer> enchantments = this.capability.getEnchantments();
        final int points = this.capability.getDatum(this.item, ENCHANTMENT_POINTS);

        if (points > 0) {
            this.drawCenteredString(this.fontRenderer, String.format("%s: %d", Mappings.MENU_POINTS, points),
                    Math.round(width / 2F), 4, 0xFFFFFF);
        }

        for (int i = 0; i < enchantments.size(); i++) {
            this.renderer.drawMiddleEnchantment(enchantments.getKey(i).getTranslatedName(enchantments.getValue(i)), i);
        }
    }

    protected void drawXPBar(final int mouseX, final int mouseY) {
        if (ColorConfig.getAlpha() >= 26F / 255) {
            final int barLeftX = (width - 182) / 2;
            final int barTopY = height - 29;
            final int xp = capability.getDatum(this.item, XP);

            GlStateManager.color(ColorConfig.getRed(), ColorConfig.getGreen(), ColorConfig.getBlue(), ColorConfig.getAlpha());
            this.mc.getTextureManager().bindTexture(XP_BAR);
            this.drawTexturedModalRect(barLeftX, barTopY, 0, 0, 182, 5);
            this.drawTexturedModalRect(barLeftX, barTopY, 0, 5, this.capability.canLevelUp(this.item)
                    ? Math.min(182, Math.round((float) xp / capability.getNextLevelXP(this.item) * 182))
                    : 182, 5);
            this.mc.getTextureManager().deleteTexture(XP_BAR);

            final int level = this.capability.getDatum(this.item, LEVEL);
            final String levelString = String.format("%d", level);
            final int levelLeftX = (width - this.fontRenderer.getStringWidth(levelString)) / 2;
            final int levelTopY = barTopY - 6;
            final int color = (new java.awt.Color(ColorConfig.getRed(), ColorConfig.getGreen(), ColorConfig.getBlue(), ColorConfig.getAlpha())).getRGB();

            this.fontRenderer.drawString(levelString, levelLeftX + 1, levelTopY, 0);
            this.fontRenderer.drawString(levelString, levelLeftX - 1, levelTopY, 0);
            this.fontRenderer.drawString(levelString, levelLeftX, levelTopY + 1, 0);
            this.fontRenderer.drawString(levelString, levelLeftX, levelTopY - 1, 0);
            this.fontRenderer.drawString(levelString, levelLeftX, levelTopY, color);

            if (mouseX >= levelLeftX && mouseX <= levelLeftX + this.fontRenderer.getStringWidth(levelString)
                    && mouseY >= levelTopY && mouseY <= levelTopY + this.fontRenderer.FONT_HEIGHT && MainConfig.instance().getMaxLevel() >= 0) {
                this.drawHoveringText(String.format("%d/%d", level, MainConfig.instance().getMaxLevel()), mouseX, mouseY);
            } else if (this.isMouseOverXPBar(mouseX, mouseY)) {
                this.drawHoveringText(this.capability.canLevelUp(this.item)
                        ? String.format("%d/%d", xp, capability.getNextLevelXP(this.item))
                        : String.format("%d", xp), mouseX, mouseY);
            }

            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
        }
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
            this.capability.refresh();
        } else {
            try {
                super.mouseClicked(mouseX, mouseY, button);
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    protected boolean displayXPBar() {
        return this.capability.getType() != null;
    }

    private boolean isMouseOverXPBar(final int mouseX, final int mouseY) {
        final int barLeftX = (width - 182) / 2;
        final int barTopY = height - 29;

        return this.displayXPBar() && mouseX >= barLeftX && mouseX <= barLeftX + 182 && mouseY >= barTopY && mouseY <= barTopY + 4;
    }

    private int mouseOverSlider(final int mouseX, final int mouseY) {
        for (int slider = 0; slider < 4; slider++) {
            if (mouseX >= this.guiFactory.getColorSliderX() && mouseX <= this.guiFactory.getColorSliderX() + 100
                    && mouseY >= this.guiFactory.getColorSliderY(slider) && mouseY <= this.guiFactory.getColorSliderY(slider) + 20) {
                return slider;
            }
        }

        return -1;
    }

    @Override
    public void handleMouseInput() {
        try {
            super.handleMouseInput();
        } catch (final IOException exception) {
            exception.printStackTrace();
        }

        final int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        final int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        final int dWheel = Mouse.getDWheel() / 120;
        final int row = this.mouseOverSlider(mouseX, mouseY);

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
        } else if (dWheel != 0 && this.capability != null) {
            this.capability.refresh(MathHelper.clamp(this.capability.getCurrentTab() - dWheel, 0, this.tabs.length - 1));
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
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
    protected void keyTyped(final char typedChar, final int keyCode) {
        if (keyCode == 1 || keyCode == KeyBindings.MENU_KEY.getKeyCode() || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        Enchantment enchantment = Enchantment.getEnchantmentByID(button.id - 1000);

        if (enchantment != null) {
            int amount = 1;

            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                amount = this.capability.getDatum(this.item, ENCHANTMENT_POINTS);
            }

            Main.CHANNEL.sendToServer(new C2SEnchant(this.capability.getType(), this.item, enchantment, amount));
        } else if ((enchantment = Enchantment.getEnchantmentByID(button.id - 2000)) != null) {
            int amount = 1;

            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                amount = this.capability.getDatum(this.item, SPENT_ENCHANTMENT_POINTS);
            }

            Main.CHANNEL.sendToServer(new C2SEnchant(this.capability.getType(), this.item, enchantment, -amount));
        } else {
            switch (button.id) {
                case 0:
                case 1:
                case 2:
                    Main.CHANNEL.sendToServer(new C2SItemType(this.capability.getType(), this.capability.getItemType(button.id)));
                    break;
                case 16:
                case 17:
                case 18:
                case 19:
                    this.capability.refresh(button.id - 16);
                    break;
                case 20:
                    Main.CHANNEL.sendToServer(new C2SReset(this.capability.getType(), this.item, ATTRIBUTE));
                    break;
                case 21:
                    Main.CHANNEL.sendToServer(new C2SReset(this.capability.getType(), this.item, ENCHANTMENT));
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
    }

    private void updateSettings() {
        ColorConfig.instance().setRed(this.sliderRed.getValueInt() / 255F);
        ColorConfig.instance().setGreen(this.sliderGreen.getValueInt() / 255F);
        ColorConfig.instance().setBlue(this.sliderBlue.getValueInt() / 255F);
        ColorConfig.instance().setAlpha(this.sliderAlpha.getValueInt() / 255F);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    protected class GUIFactory {
        public GuiButton tabButton(final int id, final int row, final String text) {
            return new GuiButton(id, width / 24, height / 16 + Math.max(height / 16 * row, 30 * row), Math.max(96, Math.round(width / 7.5F)), 20, text);
        }

        public GuiButton centeredButton(final int id, final int y, final int buttonWidth, final String text) {
            return new GuiButton(id, (width - buttonWidth) / 2, y, buttonWidth, 20, text);
        }

        public GuiButton squareButton(final int id, final int x, final int y, final String text) {
            return new GuiButton(id, x - 10, y - 10, 20, 20, text);
        }

        public GuiButton resetButton(final int id) {
            return new GuiButton(id, width - width / 24 - 112, height - height / 16 - 20, 112, 20, Mappings.MENU_BUTTON_RESET);
        }

        public GuiSlider colorSlider(final int id, final int row, final double currentValue, final String text) {
            return new GuiSlider(id, this.getColorSliderX(), this.getColorSliderY(row), 100, 20, text, "", 0, 255, currentValue * 255, false, true);
        }

        public GuiButton[] addPointButtons(final int id, final int rows, final int points) {
            final GuiButton[] buttons = new GuiButton[rows];

            for (int row = 0; row < rows; row++) {
                buttons[row] = addButton(squareButton(id + row, (width + 162) / 2, (row + 1) * height / 16 + 4, "+"));
                buttons[row].enabled = points > 0;
            }

            return buttons;
        }

        public int getColorSliderX() {
            return Math.round(width * (1 - 1 / 24F)) - 100;
        }

        public int getColorSliderY(final int row) {
            return height / 16 + Math.max(height / 16 * row, 30 * row);
        }

        public GuiButton[] removePointsButtons(final int id, final int rows) {
            final GuiButton[] buttons = new GuiButton[rows];

            for (int row = 0; row < rows; row++) {
                buttons[row] = addButton(squareButton(id + row, (width + 162) / 2 - 20, (row + 1) * height / 16 + 4, "-"));
            }

            return buttons;
        }
    }

    public class Renderer {
        private final NumberFormat FORMAT = DecimalFormat.getInstance();

        public void drawMiddleAttribute(String format, double value, int row) {
            drawString(fontRenderer, String.format(format, FORMAT.format(value)), (width - 182) / 2, (row + 1) * height / 16, 0xFFFFFF);
        }

        public void drawMiddleEnchantment(String entry, int row) {
            drawString(fontRenderer, entry, (width - 182) / 2, (row + 1) * height / 16, 0xFFFFFF);
        }
    }

    @Nonnull(when = When.MAYBE)
    protected abstract IStatistic getAttribute(final int index);
}
