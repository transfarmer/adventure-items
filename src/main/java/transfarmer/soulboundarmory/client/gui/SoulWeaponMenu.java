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
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponHelper;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.client.KeyBindings;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponAttribute;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponEnchantment;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponType;
import transfarmer.soulboundarmory.i18n.Mappings;
import transfarmer.soulboundarmory.network.weapon.server.*;
import transfarmer.util.ItemHelper;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.ResourceLocations.Client.XP_BAR;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponAttribute.*;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponDatum.*;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponEnchantment.*;

@SideOnly(CLIENT)
public class SoulWeaponMenu extends GuiScreen {
    private final GuiButton[] tabs = new GuiButton[4];
    private final GUIFactory guiFactory = new GUIFactory();
    private final ISoulWeapon capability = SoulWeaponProvider.get(Minecraft.getMinecraft().player);
    private final SoulWeaponType weaponType = this.capability.getCurrentType();

    public SoulWeaponMenu() {
        this.mc = Minecraft.getMinecraft();
    }

    public SoulWeaponMenu(final int tab) {
        this();
        this.capability.setCurrentTab(tab);
        Main.CHANNEL.sendToServer(new SWeaponTab(tab));
    }

    @Override
    public void initGui() {
        if (this.capability.getCurrentType() != null) {
            final String text = this.mc.player.inventory.currentItem != capability.getBoundSlot()
                ? Mappings.MENU_BUTTON_BIND : Mappings.MENU_BUTTON_UNBIND;

            this.addButton(new GuiButton(22, width / 24, height - height / 16 - 20, 112, 20, text));
            this.tabs[0] = addButton(guiFactory.tabButton(16, 0, Mappings.MENU_SELECTION));
            this.tabs[1] = addButton(guiFactory.tabButton(17, 1, Mappings.MENU_BUTTON_ATTRIBUTES));
            this.tabs[2] = addButton(guiFactory.tabButton(18, 2, Mappings.MENU_BUTTON_ENCHANTMENTS));
            this.tabs[3] = addButton(guiFactory.tabButton(19, 3, Mappings.MENU_BUTTON_SKILLS));
            this.tabs[this.capability.getCurrentTab()].enabled = false;

            Mouse.getDWheel();
        }

        switch (this.capability.getCurrentTab()) {
            case 0:
                showWeapons();
                break;
            case 1:
                showAttributes();
                break;
            case 2:
                showEnchantments();
                break;
            case 3:
                showSkills();
                break;
            case 4:
                showTraits();
        }

        addButton(guiFactory.centeredButton(3, 3 * height / 4, width / 8, "close"));
    }

    private void showWeapons() {
        final int buttonWidth = 128;
        final int buttonHeight = 20;
        final int xCenter = (width - buttonWidth) / 2;
        final int yCenter = (height - buttonHeight) / 2;
        final int ySep = 32;

        final GuiButton[] weaponButtons = {
            addButton(new GuiButton(0, xCenter, yCenter - ySep, buttonWidth, buttonHeight, Mappings.SOUL_GREATSWORD_NAME)),
            addButton(new GuiButton(1, xCenter, yCenter, buttonWidth, buttonHeight, Mappings.SOUL_SWORD_NAME)),
            addButton(new GuiButton(2, xCenter, yCenter + ySep, buttonWidth, buttonHeight, Mappings.SOUL_DAGGER_NAME))
        };

        if (SoulWeaponHelper.hasSoulWeapon(this.mc.player)) {
            weaponButtons[capability.getCurrentType().index].enabled = false;
        } else if (this.capability.getCurrentType() != null && !ItemHelper.hasItem(Items.WOODEN_SWORD, this.mc.player)) {
            for (final GuiButton button : weaponButtons) {
                button.enabled = false;
            }
        }
    }

    private void showAttributes() {
        final GuiButton resetButton = this.addButton(guiFactory.resetButton(20));
        final GuiButton[] addPointButtons = addAddPointButtons(4, SoulWeaponHelper.ATTRIBUTES, this.capability.getDatum(ATTRIBUTE_POINTS, this.weaponType));
        final GuiButton[] removePointButtons = addRemovePointButtons(23, SoulWeaponHelper.ATTRIBUTES);
        resetButton.enabled = this.capability.getDatum(SPENT_ATTRIBUTE_POINTS, this.weaponType) > 0;

        addPointButtons[2].enabled &= this.capability.getAttribute(CRITICAL, this.weaponType) < 100;

        removePointButtons[0].enabled = this.capability.getAttribute(ATTACK_SPEED, this.weaponType) > 0;
        removePointButtons[1].enabled = this.capability.getAttribute(ATTACK_DAMAGE, this.weaponType) > 0;

        for (int index = 2; index < SoulWeaponHelper.ATTRIBUTES; index++) {
            removePointButtons[index].enabled = this.capability.getAttribute(SoulWeaponAttribute.getAttribute(index), this.weaponType) > 0;
        }
    }

    private void showEnchantments() {
        final GuiButton resetButton = this.addButton(guiFactory.resetButton(21));
        final GuiButton[] removePointButtons = addRemovePointButtons(28, SoulWeaponHelper.ENCHANTMENTS);
        resetButton.enabled = this.capability.getDatum(SPENT_ENCHANTMENT_POINTS, this.weaponType) > 0;

        addAddPointButtons(9, SoulWeaponHelper.ENCHANTMENTS, this.capability.getDatum(ENCHANTMENT_POINTS, this.weaponType));

        for (int index = 0; index < SoulWeaponHelper.ENCHANTMENTS; index++) {
            removePointButtons[index].enabled = this.capability.getEnchantment(SoulWeaponEnchantment.getEnchantment(index), this.weaponType) > 0;
        }
    }

    private void showSkills() {}

    private void showTraits() {}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final Renderer RENDERER = new Renderer();

        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        switch (this.capability.getCurrentTab()) {
            case 0:
                drawWeapons(RENDERER, mouseX, mouseY);
                break;
            case 1:
                drawAttributes(RENDERER, mouseX, mouseY);
                break;
            case 2:
                drawEnchantments(RENDERER, mouseX, mouseY);
                break;
            case 3:
                drawSkills(RENDERER, mouseX, mouseY);
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

    private void drawWeapons(final Renderer renderer, final int mouseX, final int mouseY) {
        if (!SoulWeaponHelper.hasSoulWeapon(this.mc.player)) {
            this.drawCenteredString(this.fontRenderer, Mappings.MENU_SELECTION,
                Math.round(width / 2F), 40, 0xFFFFFF);
        }
    }

    private void drawAttributes(final Renderer renderer, final int mouseX, final int mouseY) {
        final String attackSpeed = String.format("%s%s: %%s", Mappings.ATTACK_SPEED_FORMAT, Mappings.ATTACK_SPEED_NAME);
        final String attackDamage = String.format("%s%s: %%s", Mappings.ATTACK_DAMAGE_FORMAT, Mappings.ATTACK_DAMAGE_NAME);
        final String critical = String.format("%s%s: %%s%%%%", Mappings.CRITICAL_FORMAT, Mappings.CRITICAL_NAME);
        final String knockback = String.format("%s%s: %%s", Mappings.KNOCKBACK_ATTRIBUTE_FORMAT, Mappings.KNOCKBACK_ATTRIBUTE_NAME);
        final String efficiency = String.format("%s%s: %%s", Mappings.WEAPON_EFFICIENCY_FORMAT, Mappings.EFFICIENCY_NAME);
        final int points = this.capability.getDatum(ATTRIBUTE_POINTS, this.weaponType);

        if (points > 0) {
            this.drawCenteredString(this.fontRenderer, String.format("%s: %d", Mappings.MENU_POINTS, points),
                Math.round(width / 2F), 4, 0xFFFFFF);
        }

        renderer.drawMiddleAttribute(attackSpeed, capability.getAttackSpeed(this.weaponType) + 4, 0);
        renderer.drawMiddleAttribute(attackDamage, capability.getAttackDamage(this.weaponType) + 1, 1);
        renderer.drawMiddleAttribute(critical, capability.getAttribute(CRITICAL, this.weaponType), 2);
        renderer.drawMiddleAttribute(knockback, capability.getAttribute(KNOCKBACK_ATTRIBUTE, this.weaponType), 3);
        renderer.drawMiddleAttribute(efficiency, capability.getAttribute(EFFICIENCY, this.weaponType), 4);

        this.drawXPBar(mouseX, mouseY);
    }

    private void drawEnchantments(final Renderer renderer, final int mouseX, final int mouseY) {
        final int points = this.capability.getDatum(ENCHANTMENT_POINTS, this.weaponType);

        if (points > 0) {
            this.drawCenteredString(this.fontRenderer, String.format("%s: %d", Mappings.MENU_POINTS, points),
                Math.round(width / 2F), 4, 0xFFFFFF);
        }

        renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.SHARPNESS_NAME, this.capability.getEnchantment(SHARPNESS, this.weaponType)), 0);
        renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.SWEEPING_EDGE_NAME, this.capability.getEnchantment(SWEEPING_EDGE, this.weaponType)), 1);
        renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.LOOTING_NAME, this.capability.getEnchantment(LOOTING, this.weaponType)), 2);
        renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.FIRE_ASPECT_NAME, this.capability.getEnchantment(FIRE_ASPECT, this.weaponType)), 3);
        renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.KNOCKBACK_ENCHANTMENT_NAME, this.capability.getEnchantment(KNOCKBACK, this.weaponType)), 4);
        renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.SMITE_NAME, this.capability.getEnchantment(SMITE, this.weaponType)), 5);
        renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.BANE_OF_ARTHROPODS_NAME, this.capability.getEnchantment(BANE_OF_ARTHROPODS, this.weaponType)), 6);

        this.drawXPBar(mouseX, mouseY);
    }

    private void drawSkills(final Renderer renderer, final int mouseX, final int mouseY) {
        for (int i = 0; i < capability.getDatum(SKILLS, this.weaponType); i++) {
            this.drawCenteredString(this.fontRenderer, SoulWeaponHelper.getSkills()[capability.getCurrentType().index][i],
                width / 2, (i + 2) * height / 16, 0xFFFFFF);
        }

        this.drawXPBar(mouseX, mouseY);
    }

    private void drawTraits(final Renderer renderer, final int mouseX, final int mouseY) {}

    private void drawXPBar(int mouseX, int mouseY) {
        final int barLeftX = (width - 182) / 2;
        final int barTopY = (height - 4) / 2;

        GlStateManager.color(1F, 1F, 1F, 1F);
        this.mc.getTextureManager().bindTexture(XP_BAR);
        this.drawTexturedModalRect(barLeftX, barTopY, 0, 40, 182, 5);
        this.drawTexturedModalRect(barLeftX, barTopY, 0, 45, Math.min(182, Math.round((float) capability.getDatum(XP, this.weaponType) / capability.getNextLevelXP(this.weaponType) * 182)), 5);
        this.mc.getTextureManager().deleteTexture(XP_BAR);

        final int level = this.capability.getDatum(LEVEL, this.weaponType);
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
            this.drawHoveringText(String.format("%d/%d", capability.getDatum(LEVEL, this.weaponType), Configuration.maxLevel), mouseX, mouseY);
        } else if (mouseX >= (width - 182) / 2 && mouseX <= barLeftX + 182 && mouseY >= barTopY && mouseY <= barTopY + 4) {
            final String string = this.capability.getDatum(LEVEL, this.weaponType) < Configuration.maxLevel
                ? String.format("%d/%d", capability.getDatum(XP, this.weaponType), capability.getNextLevelXP(this.weaponType))
                : String.format("%d", capability.getDatum(XP, this.weaponType));
            this.drawHoveringText(string, mouseX, mouseY);
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
            case 1:
            case 2:
                final SoulWeaponType type = SoulWeaponType.getType(button.id);
                final GuiScreen screen = !SoulWeaponHelper.hasSoulWeapon(this.mc.player)
                    ? null : new SoulWeaponMenu();

                if (screen == null) {
                    this.capability.setCurrentTab(1);
                    Main.CHANNEL.sendToServer(new SWeaponTab(this.capability.getCurrentTab()));
                }

                this.capability.setCurrentType(type);
                this.mc.displayGuiScreen(screen);
                Main.CHANNEL.sendToServer(new SWeaponType(type));

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
                    amount = this.capability.getDatum(ATTRIBUTE_POINTS, this.weaponType);
                }

                Main.CHANNEL.sendToServer(new SWeaponAttributePoints(amount, SoulWeaponAttribute.getAttribute(button.id - 4), this.weaponType));
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
                    amount = this.capability.getDatum(ENCHANTMENT_POINTS, this.weaponType);
                }

                Main.CHANNEL.sendToServer(new SWeaponEnchantmentPoints(amount, SoulWeaponEnchantment.getEnchantment(button.id - 9), this.weaponType));
                break;
            case 16:
            case 17:
            case 18:
            case 19:
                final int tab = button.id - 16;
                this.mc.displayGuiScreen(new SoulWeaponMenu(tab));
                break;
            case 20:
                Main.CHANNEL.sendToServer(new SWeaponResetAttributes(this.weaponType));
                break;
            case 21:
                Main.CHANNEL.sendToServer(new SWeaponResetEnchantments(this.weaponType));
                break;
            case 22:
                final int slot = this.mc.player.inventory.currentItem;

                if (capability.getBoundSlot() == slot) {
                    capability.unbindSlot();
                } else {
                    capability.setBoundSlot(slot);
                }

                this.mc.displayGuiScreen(new SoulWeaponMenu());
                Main.CHANNEL.sendToServer(new SWeaponBindSlot(slot));
                break;
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                amount = 1;

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                    amount = this.capability.getDatum(SPENT_ATTRIBUTE_POINTS, this.weaponType);
                }

                Main.CHANNEL.sendToServer(new SWeaponAttributePoints(-amount, SoulWeaponAttribute.getAttribute(button.id - 23), this.weaponType));
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
                    amount = this.capability.getDatum(SPENT_ENCHANTMENT_POINTS, this.weaponType);
                }

                Main.CHANNEL.sendToServer(new SWeaponEnchantmentPoints(-amount, SoulWeaponEnchantment.getEnchantment(button.id - 28), this.weaponType));
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

        if ((dWheel = Mouse.getDWheel()) != 0) {
            this.mc.displayGuiScreen(new SoulWeaponMenu(MathHelper.clamp(this.capability.getCurrentTab() - (int) Math.signum(dWheel), 0, 3)));
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
