package transfarmer.soulweapons.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.Configuration;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.capability.SoulWeapon;
import transfarmer.soulweapons.capability.SoulWeaponHelper;
import transfarmer.soulweapons.data.SoulWeaponAttribute;
import transfarmer.soulweapons.data.SoulWeaponEnchantment;
import transfarmer.soulweapons.data.SoulWeaponType;
import transfarmer.soulweapons.network.ServerAddAttribute;
import transfarmer.soulweapons.network.ServerAddEnchantment;
import transfarmer.soulweapons.network.ServerTab;
import transfarmer.soulweapons.network.ServerWeaponType;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.ATTACK_DAMAGE;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.ATTACK_SPEED;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.CRITICAL;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.EFFICIENCY;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.KNOCKBACK_ATTRIBUTE;
import static transfarmer.soulweapons.data.SoulWeaponDatum.ENCHANTMENT_POINTS;
import static transfarmer.soulweapons.data.SoulWeaponDatum.LEVEL;
import static transfarmer.soulweapons.data.SoulWeaponDatum.POINTS;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SKILLS;
import static transfarmer.soulweapons.data.SoulWeaponDatum.XP;
import static transfarmer.soulweapons.data.SoulWeaponEnchantment.BANE_OF_ARTHROPODS;
import static transfarmer.soulweapons.data.SoulWeaponEnchantment.FIRE_ASPECT;
import static transfarmer.soulweapons.data.SoulWeaponEnchantment.KNOCKBACK;
import static transfarmer.soulweapons.data.SoulWeaponEnchantment.LOOTING;
import static transfarmer.soulweapons.data.SoulWeaponEnchantment.SHARPNESS;
import static transfarmer.soulweapons.data.SoulWeaponEnchantment.SMITE;
import static transfarmer.soulweapons.data.SoulWeaponEnchantment.SWEEPING_EDGE;

@SideOnly(CLIENT)
public class SoulWeaponMenu extends GuiScreen {
    private final GuiButton[] tabs = new GuiButton[4];
    private final GUIFactory guiFactory = new GUIFactory();
    private final ISoulWeapon capability = Minecraft.getMinecraft().player.getCapability(CAPABILITY, null);
    private final SoulWeaponType weaponType = this.capability.getCurrentType();

    public SoulWeaponMenu() {}

    public SoulWeaponMenu(final int tab) {
        this.capability.setCurrentTab(tab);
        Main.CHANNEL.sendToServer(new ServerTab(tab));
    }

    @Override
    public void initGui() {
        if (SoulWeaponHelper.hasSoulWeapon(Minecraft.getMinecraft().player)) {
            this.tabs[0] = addButton(guiFactory.tabButton(16, 0, I18n.format("menu.soulweapons.selection")));
            this.tabs[1] = addButton(guiFactory.tabButton(17, 1, I18n.format("menu.soulweapons.attributes")));
            this.tabs[2] = addButton(guiFactory.tabButton(18, 2, I18n.format("menu.soulweapons.enchantments")));
            this.tabs[3] = addButton(guiFactory.tabButton(19, 3, I18n.format("menu.soulweapons.skills")));
            this.tabs[this.capability.getCurrentTab()].enabled = false;
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
        }

        addButton(guiFactory.centeredButton(3, 3 * height / 4, width / 8, "close"));
    }

    private void showWeapons() {
        int buttonWidth = 128;
        int buttonHeight = 20;
        int xCenter = (width - buttonWidth) / 2;
        int ySep = 32;

        final GuiButton[] weaponButtons = {
            addButton(new GuiButton(0, xCenter, height / 2 - ySep, buttonWidth, buttonHeight, I18n.format("item.soulweapons.soulGreatsword"))),
            addButton(new GuiButton(1, xCenter, height / 2, buttonWidth, buttonHeight, I18n.format("item.soulweapons.soulSword"))),
            addButton(new GuiButton(2,xCenter, height / 2 + ySep, buttonWidth, buttonHeight, I18n.format("item.soulweapons.soulDagger")))
        };

        if (SoulWeaponHelper.hasSoulWeapon(this.mc.player)) {
            weaponButtons[capability.getCurrentType().index].enabled = false;
        }
    }

    private void showAttributes() {
        showPointButtons(4, 4, this.capability.getDatum(POINTS, this.weaponType));
    }

    private void showEnchantments() {
        showPointButtons(9, 6, this.capability.getDatum(ENCHANTMENT_POINTS, this.weaponType));
    }

    private void showSkills() {
    }

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

    private void showPointButtons(int id, int rows, int points) {
        for (int row = 0; row <= rows; row++) {
            GuiButton button = addButton(guiFactory.addPointButton(id + row, (width + 162) / 2, (row + 1) * height / 16 + 4));
            button.enabled = points > 0;
        }
    }

    private void drawWeapons(final Renderer renderer, final int mouseX, final int mouseY) {
        if (!SoulWeaponHelper.hasSoulWeapon(this.mc.player)) {
            this.drawCenteredString(this.fontRenderer, I18n.format("menu.soulweapons.selection"),
                Math.round(width / 2F), 40, 0xFFFFFF);
        }
    }

    private void drawAttributes(final Renderer renderer, final int mouseX, final int mouseY) {
        final String attackSpeed = String.format("%s%s: %%s", I18n.format("format.soulweapons.attackSpeed"), I18n.format("attribute.soulweapons.attackSpeed"));
        final String attackDamage = String.format("%s%s: %%s", I18n.format("format.soulweapons.attackDamage"), I18n.format("attribute.soulweapons.attackDamage"));
        final String critical = String.format("%s%s: %%s%%%%", I18n.format("format.soulweapons.critical"), I18n.format("attribute.soulweapons.critical"));
        final String knockback = String.format("%s%s: %%s", I18n.format("format.soulweapons.knockback"), I18n.format("attribute.soulweapons.knockback"));
        final String efficiency = String.format("%s%s: %%s", I18n.format("format.soulweapons.efficiency"), I18n.format("attribute.soulweapons.efficiency"));

        renderer.drawMiddleAttribute(attackSpeed, capability.getAttribute(ATTACK_SPEED, this.weaponType) + 4, 0);
        renderer.drawMiddleAttribute(attackDamage, capability.getAttribute(ATTACK_DAMAGE, this.weaponType) + 1, 1);
        renderer.drawMiddleAttribute(critical, capability.getAttribute(CRITICAL, this.weaponType), 2);
        renderer.drawMiddleAttribute(knockback, capability.getAttribute(KNOCKBACK_ATTRIBUTE, this.weaponType), 3);
        renderer.drawMiddleAttribute(efficiency, capability.getAttribute(EFFICIENCY, this.weaponType), 4);

        this.drawXPBar(mouseX, mouseY);
    }

    private void drawEnchantments(final Renderer renderer, final int mouseX, final int mouseY) {
        renderer.drawMiddleEnchantment(String.format("%s: %s", I18n.format("enchantment.soulweapons.sharpness"), this.capability.getEnchantment(SHARPNESS, this.weaponType)), 0);
        renderer.drawMiddleEnchantment(String.format("%s: %s", I18n.format("enchantment.soulweapons.sweepingEdge"), this.capability.getEnchantment(SWEEPING_EDGE, this.weaponType)), 1);
        renderer.drawMiddleEnchantment(String.format("%s: %s", I18n.format("enchantment.soulweapons.looting"), this.capability.getEnchantment(LOOTING, this.weaponType)), 2);
        renderer.drawMiddleEnchantment(String.format("%s: %s", I18n.format("enchantment.soulweapons.fireAspect"), this.capability.getEnchantment(FIRE_ASPECT, this.weaponType)), 3);
        renderer.drawMiddleEnchantment(String.format("%s: %s", I18n.format("enchantment.soulweapons.knockback"), this.capability.getEnchantment(KNOCKBACK, this.weaponType)), 4);
        renderer.drawMiddleEnchantment(String.format("%s: %s", I18n.format("enchantment.soulweapons.smite"), this.capability.getEnchantment(SMITE, this.weaponType)), 5);
        renderer.drawMiddleEnchantment(String.format("%s: %s", I18n.format("enchantment.soulweapons.baneOfArthropods"), this.capability.getEnchantment(BANE_OF_ARTHROPODS, this.weaponType)), 6);

        this.drawXPBar(mouseX, mouseY);
    }

    private void drawSkills(final Renderer renderer, final int mouseX, final int mouseY) {
        for (int i = 0; i < capability.getDatum(SKILLS, this.weaponType); i++) {
            this.drawCenteredString(this.fontRenderer, SoulWeapon.getSkillNames()[capability.getCurrentType().index][i],
                width / 2, (i + 2) * height / 16, 0xFFFFFF);
        }
    }

    private void drawXPBar(int mouseX, int mouseY) {
        final int barLeftX = (width - 182) / 2;
        final int barTopY = (height - 4) / 2;

        this.mc.getTextureManager().bindTexture(Main.XP_BAR);
        GlStateManager.color(1F, 1F, 1F, 1F);

        this.drawTexturedModalRect(barLeftX, barTopY, 0, 40, 182, 5);
        this.drawTexturedModalRect(barLeftX, barTopY, 0, 45, Math.round((float) capability.getDatum(XP, this.weaponType) / capability.getNextLevelXP(this.weaponType) * 182), 5);

        this.mc.getTextureManager().deleteTexture(Main.XP_BAR);

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
            this.drawHoveringText(String.format("%d/%d", capability.getDatum(XP, this.weaponType), capability.getNextLevelXP(this.weaponType)), mouseX, mouseY);
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
            case 1:
            case 2:
                final SoulWeaponType type = SoulWeaponType.getType(button.id);
                GuiScreen screen;

                if (!SoulWeaponHelper.hasSoulWeapon(this.mc.player)) {
                    capability.setCurrentType(type);
                    this.capability.setCurrentTab(1);
                    screen = null;
                } else {
                    capability.setCurrentType(type);
                    screen = new SoulWeaponMenu();
                }

                this.mc.displayGuiScreen(screen);
                Main.CHANNEL.sendToServer(new ServerWeaponType(type));
                Main.CHANNEL.sendToServer(new ServerTab(1));

                break;
            case 3:
                this.mc.displayGuiScreen(null);
                break;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                Main.CHANNEL.sendToServer(new ServerAddAttribute(SoulWeaponAttribute.getAttribute(button.id - 4)));
                break;
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                Main.CHANNEL.sendToServer(new ServerAddEnchantment(SoulWeaponEnchantment.getEnchantment(button.id - 9)));
                break;
            case 16:
            case 17:
            case 18:
            case 19:
                final int tab = button.id - 16;
                this.mc.displayGuiScreen(new SoulWeaponMenu(tab));
                break;
        }
    }

    public class GUIFactory {
        public GuiButton tabButton(int id, int order, String text) {
            return new GuiButton(id, width / 24, height / 16 + Math.max(height / 16 * (Configuration.menuOffset - 1 + order), 30 * order), Math.max(96, Math.round(width / 7.5F)), 20, text);
        }

        public GuiButton centeredButton(int id, int y, int buttonWidth, String text) {
            return new GuiButton(id, (width - buttonWidth) / 2, y, buttonWidth, 20, text);
        }

        public GuiButton addPointButton(int id, int x, int y) {
            return new GuiButton(id, x - 10, y - 10, 20, 20, "+");
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
