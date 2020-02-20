package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import transfarmer.soulboundarmory.Configuration;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolHelper;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.client.KeyBindings;
import transfarmer.soulboundarmory.statistics.IType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute;
import transfarmer.soulboundarmory.statistics.tool.SoulToolEnchantment;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.network.server.tool.*;
import transfarmer.util.ItemHelper;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.Main.ResourceLocations.Client.XP_BAR;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute.HARVEST_LEVEL;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute.getAttribute;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolDatum.*;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolEnchantment.*;

@SideOnly(CLIENT)
public class SoulToolMenu extends GuiScreen {
    private final GuiButton[] tabs = new GuiButton[3];
    private final GUIFactory guiFactory = new GUIFactory();
    private final Renderer RENDERER = new Renderer();
    private final ISoulTool capability = SoulToolProvider.get(Minecraft.getMinecraft().player);
    private final IType toolType = this.capability.getCurrentType();

    public SoulToolMenu() {
        this.mc = Minecraft.getMinecraft();
    }

    public SoulToolMenu(final int tab) {
        this();
        this.capability.setCurrentTab(tab);
        Main.CHANNEL.sendToServer(new SToolTab(tab));
    }

    @Override
    public void initGui() {
        if (this.mc.player.getHeldItemMainhand().getItem() instanceof IItemSoulTool) {
            final String text = this.mc.player.inventory.currentItem != capability.getBoundSlot()
                    ? Mappings.MENU_BUTTON_BIND : Mappings.MENU_BUTTON_UNBIND;

            this.addButton(new GuiButton(22, width / 24, height - height / 16 - 20, 112, 20, text));
            this.tabs[0] = addButton(guiFactory.tabButton(16, 0, Mappings.MENU_BUTTON_ATTRIBUTES));
            this.tabs[1] = addButton(guiFactory.tabButton(17, 1, Mappings.MENU_BUTTON_ENCHANTMENTS));
            this.tabs[2] = addButton(guiFactory.tabButton(18, 2, Mappings.MENU_BUTTON_SKILLS));
            this.tabs[this.capability.getCurrentTab()].enabled = false;
        }

        switch (this.capability.getCurrentTab()) {
            case 0:
                this.showAttributes();
                break;
            case 1:
                this.showEnchantments();
                break;
            case 2:
                this.showSkills();
                break;
            case 3:
                this.showTraits();
            default:
                this.showConfirmation();
        }

        this.addButton(this.guiFactory.centeredButton(3, 3 * height / 4, width / 8, "close"));
    }

    private void showConfirmation() {
        final int buttonWidth = 128;
        final int buttonHeight = 20;
        final int xCenter = (width - buttonWidth) / 2;
        final int yCenter = (height - buttonHeight) / 2;
        final int ySep = 32;
        final GuiButton choiceButton = this.addButton(new GuiButton(0, xCenter, yCenter - ySep, buttonWidth, buttonHeight, Mappings.SOUL_PICK_NAME));

        if (SoulToolHelper.hasSoulTool(this.mc.player) || !ItemHelper.hasItem(Items.WOODEN_PICKAXE, this.mc.player)) {
            choiceButton.enabled = false;
        }
    }

    private void showAttributes() {
        final GuiButton resetButton = this.addButton(this.guiFactory.resetButton(20));
        final GuiButton[] removePointButtons = this.addRemovePointButtons(23, this.capability.getAttributeAmount());
        final GuiButton[] addPointButtons = this.addAddPointButtons(4, this.capability.getAttributeAmount(), this.capability.getDatum(ATTRIBUTE_POINTS, this.toolType));
        resetButton.enabled = this.capability.getDatum(SPENT_ATTRIBUTE_POINTS, this.toolType) > 0;

        for (int index = 0; index < this.capability.getAttributeAmount(); index++) {
            removePointButtons[index].enabled = this.capability.getAttribute(getAttribute(index), this.toolType) > 0;
        }

        addPointButtons[HARVEST_LEVEL.getIndex()].enabled &= this.capability.getAttribute(HARVEST_LEVEL, this.toolType) < 3;
    }

    private void showEnchantments() {
        final GuiButton resetButton = this.addButton(guiFactory.resetButton(21));
        final GuiButton[] removePointButtons = addRemovePointButtons(28, this.capability.getEnchantmentAmount());
        resetButton.enabled = this.capability.getDatum(SPENT_ENCHANTMENT_POINTS, this.toolType) > 0;

        this.addAddPointButtons(9, this.capability.getEnchantmentAmount(), this.capability.getDatum(ENCHANTMENT_POINTS, this.toolType));

        for (int index = 0; index < this.capability.getEnchantmentAmount(); index++) {
            removePointButtons[index].enabled = this.capability.getEnchantment(SoulToolEnchantment.getEnchantment(index), this.toolType) > 0;
        }
    }

    private GuiButton[] addAddPointButtons(final int id, final int rows, final int points) {
        final GuiButton[] buttons = new GuiButton[rows];

        for (int row = 0; row < rows; row++) {
            buttons[row] = addButton(guiFactory.addSquareButton(id + row, (width + 162) / 2, (row + 1) * height / 16 + 4, "+"));
            buttons[row].enabled = points > 0;
        }

        return buttons;
    }

    private GuiButton[] addRemovePointButtons(final int id, final int rows) {
        final GuiButton[] buttons = new GuiButton[rows];

        for (int row = 0; row < rows; row++) {
            buttons[row] = this.addButton(guiFactory.addSquareButton(id + row, (width + 162) / 2 - 20, (row + 1) * height / 16 + 4, "-"));
        }

        return buttons;
    }

    private void showSkills() {}

    private void showTraits() {}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        switch (this.capability.getCurrentTab()) {
            case 0:
                this.drawAttributes(mouseX, mouseY);
                break;
            case 1:
                this.drawEnchantments(mouseX, mouseY);
                break;
            case 2:
                this.drawSkills(mouseX, mouseY);
            default:
                this.drawSelection(mouseX, mouseY);
        }
    }

    private void drawSelection(final int mouseX, final int mouseY) {
        if (!SoulToolHelper.hasSoulTool(this.mc.player)) {
            this.drawCenteredString(this.fontRenderer, Mappings.MENU_CONFIRMATION,
                    Math.round(width / 2F), 40, 0xFFFFFF);
        }
    }

    private void drawAttributes(final int mouseX, final int mouseY) {
        final String efficiency = String.format("%s%s: %%s", Mappings.WEAPON_EFFICIENCY_FORMAT, Mappings.EFFICIENCY_NAME);
        final String harvestLevel = String.format("%s%s: %%s (%s)", Mappings.HARVEST_LEVEL_FORMAT, Mappings.HARVEST_LEVEL_NAME,
                Mappings.getMiningLevels()[Math.min((int) this.capability.getAttribute(HARVEST_LEVEL, this.toolType), 3)]);
        final String reachDistance = String.format("%s%s: %%s", Mappings.REACH_DISTANCE_FORMAT, Mappings.REACH_DISTANCE_NAME);
        final int points = this.capability.getDatum(ATTRIBUTE_POINTS, this.toolType);

        if (points > 0) {
            this.drawCenteredString(this.fontRenderer, String.format("%s: %d", Mappings.MENU_POINTS, points),
                    Math.round(width / 2F), 4, 0xFFFFFF);
        }

        this.RENDERER.drawMiddleAttribute(efficiency, capability.getEffectiveEfficiency(this.toolType), 0);
        this.RENDERER.drawMiddleAttribute(reachDistance, capability.getEffectiveReachDistance(this.toolType), 1);
        this.RENDERER.drawMiddleAttribute(harvestLevel, capability.getAttribute(HARVEST_LEVEL, this.toolType), 2);

        this.drawXPBar(mouseX, mouseY);
    }

    private void drawEnchantments(final int mouseX, final int mouseY) {
        final int points = this.capability.getDatum(ENCHANTMENT_POINTS, this.toolType);

        if (points > 0) {
            this.drawCenteredString(this.fontRenderer, String.format("%s: %d", Mappings.MENU_POINTS, points),
                    Math.round(width / 2F), 4, 0xFFFFFF);
        }

        this.RENDERER.drawMiddleEnchantment(String.format("%s: %s", Mappings.EFFICIENCY_ENCHANTMENT_NAME, this.capability.getEnchantment(SOUL_EFFICIENCY_ENCHANTMENT, this.toolType)), 0);
        this.RENDERER.drawMiddleEnchantment(String.format("%s: %s", Mappings.FORTUNE_NAME, this.capability.getEnchantment(SOUL_FORTUNE, this.toolType)), 1);
        this.RENDERER.drawMiddleEnchantment(String.format("%s: %s", Mappings.SILK_TOUCH_NAME, this.capability.getEnchantment(SOUL_SILK_TOUCH, this.toolType)), 2);

        this.drawXPBar(mouseX, mouseY);
    }

    private void drawSkills(final int mouseX, final int mouseY) {
        for (int i = 0; i < this.capability.getDatum(SKILLS, this.toolType); i++) {
            this.drawCenteredString(this.fontRenderer, this.capability.getCurrentType().getSkills()[i],
                    this.width / 2, (i + 2) * this.height / 16, 0xFFFFFF);
        }

        this.drawXPBar(mouseX, mouseY);
    }

    private void drawTraits(final int mouseX, final int mouseY) {}

    private void drawXPBar(int mouseX, int mouseY) {
        final int barLeftX = (width - 182) / 2;
        final int barTopY = (height - 4) / 2;

        GlStateManager.color(1F, 1F, 1F, 1F);
        this.mc.getTextureManager().bindTexture(XP_BAR);
        this.drawTexturedModalRect(barLeftX, barTopY, 0, 40, 182, 5);
        this.drawTexturedModalRect(barLeftX, barTopY, 0, 45, Math.min(182, Math.round((float) capability.getDatum(XP, this.toolType) / capability.getNextLevelXP(this.toolType) * 182)), 5);
        this.mc.getTextureManager().deleteTexture(XP_BAR);

        final int level = this.capability.getDatum(LEVEL, this.toolType);
        final String levelString = String.format("%d", level);
        final int levelLeftX = Math.round((width - this.fontRenderer.getStringWidth(levelString)) / 2F) + 1;
        final int levelTopY = height / 2 - 8;
        this.fontRenderer.drawString(levelString, levelLeftX + 1, levelTopY, 0);
        this.fontRenderer.drawString(levelString, levelLeftX - 1, levelTopY, 0);
        this.fontRenderer.drawString(levelString, levelLeftX, levelTopY + 1, 0);
        this.fontRenderer.drawString(levelString, levelLeftX, levelTopY - 1, 0);
        this.fontRenderer.drawString(levelString, levelLeftX, levelTopY, 0xEC00B8);

        if (mouseX >= levelLeftX && mouseX <= levelLeftX + this.fontRenderer.getStringWidth(levelString)
                && mouseY >= levelTopY && mouseY <= levelTopY + this.fontRenderer.FONT_HEIGHT) {
            this.drawHoveringText(String.format("%d/%d", capability.getDatum(LEVEL, this.toolType), Configuration.maxLevel), mouseX, mouseY);
        } else if (mouseX >= (width - 182) / 2 && mouseX <= barLeftX + 182 && mouseY >= barTopY && mouseY <= barTopY + 4) {
            final String string = this.capability.getDatum(LEVEL, this.toolType) < Configuration.maxLevel
                    ? String.format("%d/%d", capability.getDatum(XP, this.toolType), capability.getNextLevelXP(this.toolType))
                    : String.format("%d", capability.getDatum(XP, this.toolType));
            this.drawHoveringText(string, mouseX, mouseY);
        }
    }

    @Override
    public void actionPerformed(final GuiButton button) {
        switch (button.id) {
            case 0:
                final SoulToolType type = SoulToolType.getType(button.id);
                final GuiScreen screen = !SoulToolHelper.hasSoulTool(this.mc.player)
                        ? null : new SoulToolMenu();

                if (screen == null) {
                    this.capability.setCurrentTab(0);
                    Main.CHANNEL.sendToServer(new SToolTab(this.capability.getCurrentTab()));
                }

                this.capability.setCurrentType(type);
                this.mc.displayGuiScreen(screen);
                Main.CHANNEL.sendToServer(new SToolType(type));

                break;
            case 3:
                this.mc.displayGuiScreen(null);
                break;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                int amount = 1;

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                    amount = this.capability.getDatum(ATTRIBUTE_POINTS, this.toolType);
                }

                Main.CHANNEL.sendToServer(new SToolAttributePoints(amount, SoulToolAttribute.getAttribute(button.id - 4), this.toolType));
                break;
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                amount = 1;

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                    amount = this.capability.getDatum(ENCHANTMENT_POINTS, this.toolType);
                }

                Main.CHANNEL.sendToServer(new SToolEnchantmentPoints(amount, SoulToolEnchantment.getEnchantment(button.id - 9), this.toolType));
                break;
            case 16:
            case 17:
            case 18:
            case 19:
                this.mc.displayGuiScreen(new SoulToolMenu(button.id - 16));
                break;
            case 20:
                Main.CHANNEL.sendToServer(new SToolResetAttributes(this.toolType));
                break;
            case 21:
                Main.CHANNEL.sendToServer(new SToolResetEnchantments(this.toolType));
                break;
            case 22:
                final int slot = this.mc.player.inventory.currentItem;

                if (capability.getBoundSlot() == slot) {
                    capability.unbindSlot();
                } else {
                    capability.bindSlot(slot);
                }

                this.mc.displayGuiScreen(new SoulToolMenu());
                Main.CHANNEL.sendToServer(new SToolBindSlot(slot));
                break;
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                amount = 1;

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                    amount = this.capability.getDatum(SPENT_ATTRIBUTE_POINTS, this.toolType);
                }

                Main.CHANNEL.sendToServer(new SToolAttributePoints(-amount, SoulToolAttribute.getAttribute(button.id - 23), this.toolType));
                break;
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                amount = 1;

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                    amount = this.capability.getDatum(SPENT_ENCHANTMENT_POINTS, this.toolType);
                }

                Main.CHANNEL.sendToServer(new SToolEnchantmentPoints(-amount, SoulToolEnchantment.getEnchantment(button.id - 28), this.toolType));
        }
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) {
        if (keyCode == 1 || keyCode == KeyBindings.MENU_KEY.getKeyCode() || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void handleMouseInput() {
        try {
            super.handleMouseInput();
        } catch (final IOException exception) {
            exception.printStackTrace();
        }

        final int dWheel;

        if ((dWheel = Mouse.getDWheel()) != 0 && SoulToolHelper.isSoulToolEquipped(this.mc.player)) {
            this.mc.displayGuiScreen(new SoulToolMenu(MathHelper.clamp(this.capability.getCurrentTab() - (int) Math.signum(dWheel), 0, 2)));
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public class GUIFactory {
        public GuiButton tabButton(final int id, final int row, final String text) {
            return new GuiButton(id, width / 24, height / 16 + Math.max(height / 16 * (Configuration.menuOffset - 1 + row), 30 * row), Math.max(96, Math.round(width / 7.5F)), 20, text);
        }

        public GuiButton centeredButton(final int id, final int y, final int buttonWidth, final String text) {
            return new GuiButton(id, (width - buttonWidth) / 2, y, buttonWidth, 20, text);
        }

        public GuiButton addSquareButton(final int id, final int x, final int y, final String text) {
            return new GuiButton(id, x - 10, y - 10, 20, 20, text);
        }

        public GuiButton resetButton(final int id) {
            return new GuiButton(id, width - width / 24 - 112, height - height / 16 - 20, 112, 20, Mappings.MENU_BUTTON_RESET);
        }
    }

    public class Renderer {
        private final NumberFormat FORMAT = DecimalFormat.getInstance();

        public void drawLeftAttribute(String name, float value, int row) {
            drawString(fontRenderer, String.format(name, FORMAT.format(value)), width / 16, (row + Configuration.menuOffset) * height / 16, 0xFFFFFF);
        }

        public void drawMiddleAttribute(String format, float value, int row) {
            drawString(fontRenderer, String.format(format, FORMAT.format(value)), (width - 182) / 2, (row + Configuration.menuOffset) * height / 16, 0xFFFFFF);
        }

        public void drawMiddleEnchantment(String entry, int row) {
            drawString(fontRenderer, entry, (width - 182) / 2, (row + Configuration.menuOffset) * height / 16, 0xFFFFFF);
        }
    }
}
