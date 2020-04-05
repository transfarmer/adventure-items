package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.network.server.weapon.*;
import transfarmer.soulboundarmory.statistics.SoulAttribute;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponEnchantment;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;
import transfarmer.soulboundarmory.util.ItemHelper;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.statistics.SoulEnchantment.*;
import static transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute.*;
import static transfarmer.soulboundarmory.statistics.SoulDatum.SoulWeaponDatum.*;

@SideOnly(CLIENT)
public class SoulWeaponMenu extends Menu {
    public SoulWeaponMenu() {
        super(4, Items.WOODEN_SWORD);
    }

    public SoulWeaponMenu(final int tab) {
        this();
        this.capability.setCurrentTab(tab);
        Main.CHANNEL.sendToServer(new SWeaponTab(tab));
    }

    @Override
    public void initGui() {
        super.initGui();

        if (SoulItemHelper.isSoulWeaponEquipped(this.mc.player)) {
            final String text = this.slot != capability.getBoundSlot()
                    ? Mappings.MENU_BUTTON_BIND
                    : Mappings.MENU_BUTTON_UNBIND;

            this.addButton(new GuiButton(22, width / 24, height - height / 16 - 20, 112, 20, text));
            this.tabs[0] = addButton(guiFactory.tabButton(16, 0, Mappings.MENU_SELECTION));
            this.tabs[1] = addButton(guiFactory.tabButton(17, 1, Mappings.MENU_BUTTON_ATTRIBUTES));
            this.tabs[2] = addButton(guiFactory.tabButton(18, 2, Mappings.MENU_BUTTON_ENCHANTMENTS));
            this.tabs[3] = addButton(guiFactory.tabButton(19, 3, Mappings.MENU_BUTTON_SKILLS));
            this.tabs[this.capability.getCurrentTab()].enabled = false;
        }

        switch (this.capability.getCurrentTab()) {
            case 0:
                this.showWeapons();
                break;
            case 1:
                this.showAttributes();
                break;
            case 2:
                this.showEnchantments();
                break;
            case 3:
                this.showSkills();
                break;
            case 4:
                this.showTraits();
        }

        this.addButton(guiFactory.centeredButton(3, 3 * height / 4, width / 8, Mappings.MENU_CLOSE));
    }

    @Override
    protected boolean displayXPBar() {
        return (this.capability.getCurrentTab() >= 1 && this.capability.getCurrentTab() <= 3);
    }

    private void showWeapons() {
        final int buttonWidth = 128;
        final int buttonHeight = 20;
        final int xCenter = (width - buttonWidth) / 2;
        final int yCenter = (height - buttonHeight) / 2;
        final int ySep = 32;

        final GuiButton[] weaponButtons = {
                this.addButton(new GuiButton(0, xCenter, yCenter - ySep, buttonWidth, buttonHeight, Mappings.SOUL_GREATSWORD_NAME)),
                this.addButton(new GuiButton(1, xCenter, yCenter, buttonWidth, buttonHeight, Mappings.SOUL_SWORD_NAME)),
                this.addButton(new GuiButton(2, xCenter, yCenter + ySep, buttonWidth, buttonHeight, Mappings.SOUL_DAGGER_NAME))
        };

        if (SoulItemHelper.hasSoulWeapon(this.mc.player)) {
            weaponButtons[capability.getCurrentType().getIndex()].enabled = false;
        } else if (this.capability.getCurrentType() != null && !ItemHelper.hasItem(Items.WOODEN_SWORD, this.mc.player)) {
            for (final GuiButton button : weaponButtons) {
                button.enabled = false;
            }
        }
    }

    private void showAttributes() {
        final GuiButton resetButton = this.addButton(guiFactory.resetButton(20));
        final GuiButton[] addPointButtons = addAddPointButtons(4, this.capability.getAttributeAmount(), this.capability.getDatum(DATA.attributePoints, this.type));
        final GuiButton[] removePointButtons = addRemovePointButtons(23, this.capability.getAttributeAmount());
        resetButton.enabled = this.capability.getDatum(DATA.spentAttributePoints, this.type) > 0;

        addPointButtons[2].enabled &= this.capability.getAttribute(CRITICAL, this.type) < 100;

        for (int index = 0; index < this.capability.getAttributeAmount(); index++) {
            removePointButtons[index].enabled = this.capability.getAttribute(getAttribute(index), this.type) > 0;
        }
    }

    private void showEnchantments() {
        final GuiButton resetButton = this.addButton(guiFactory.resetButton(21));
        final GuiButton[] removePointButtons = addRemovePointButtons(28, this.capability.getEnchantmentAmount());
        resetButton.enabled = this.capability.getDatum(DATA.spentEnchantmentPoints, this.type) > 0;

        addAddPointButtons(9, this.capability.getEnchantmentAmount(), this.capability.getDatum(DATA.enchantmentPoints, this.type));

        for (int index = 0; index < this.capability.getEnchantmentAmount(); index++) {
            removePointButtons[index].enabled = this.capability.getEnchantment(SoulWeaponEnchantment.get(index), this.type) > 0;
        }
    }

    private void showSkills() {}

    private void showTraits() {}

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        switch (this.capability.getCurrentTab()) {
            case 0:
                this.drawWeapons();
                break;
            case 1:
                this.drawAttributes();
                break;
            case 2:
                this.drawEnchantments();
                break;
            case 3:
                this.drawSkills();
        }
    }

    private void drawWeapons() {
        if (!SoulItemHelper.hasSoulWeapon(this.mc.player)) {
            this.drawCenteredString(this.fontRenderer, Mappings.MENU_SELECTION,
                    Math.round(width / 2F), 40, 0xFFFFFF);
        }
    }

    private void drawAttributes() {
        final String attackSpeed = String.format("%s%s: %%s", Mappings.ATTACK_SPEED_FORMAT, Mappings.ATTACK_SPEED_NAME);
        final String attackDamage = String.format("%s%s: %%s", Mappings.ATTACK_DAMAGE_FORMAT, Mappings.ATTACK_DAMAGE_NAME);
        final String critical = String.format("%s%s: %%s%%%%", Mappings.CRITICAL_FORMAT, Mappings.CRITICAL_NAME);
        final String knockback = String.format("%s%s: %%s", Mappings.KNOCKBACK_ATTRIBUTE_FORMAT, Mappings.KNOCKBACK_ATTRIBUTE_NAME);
        final String efficiency = String.format("%s%s: %%s", Mappings.WEAPON_EFFICIENCY_FORMAT, Mappings.EFFICIENCY_NAME);
        final int points = this.capability.getDatum(DATA.attributePoints, this.type);

        if (points > 0) {
            this.drawCenteredString(this.fontRenderer, String.format("%s: %d", Mappings.MENU_POINTS, points),
                    Math.round(width / 2F), 4, 0xFFFFFF);
        }

        this.renderer.drawMiddleAttribute(attackSpeed, capability.getAttribute(ATTACK_SPEED, this.type, true, true), 0);
        this.renderer.drawMiddleAttribute(attackDamage, capability.getAttribute(ATTACK_DAMAGE, this.type, true, true), 1);
        this.renderer.drawMiddleAttribute(critical, capability.getAttribute(CRITICAL, this.type), 2);
        this.renderer.drawMiddleAttribute(knockback, capability.getAttribute(KNOCKBACK_ATTRIBUTE, this.type), 3);
        this.renderer.drawMiddleAttribute(efficiency, capability.getAttribute(EFFICIENCY_ATTRIBUTE, this.type), 4);
    }

    private void drawEnchantments() {
        final int points = this.capability.getDatum(DATA.enchantmentPoints, this.type);

        if (points > 0) {
            this.drawCenteredString(this.fontRenderer, String.format("%s: %d", Mappings.MENU_POINTS, points),
                    Math.round(width / 2F), 4, 0xFFFFFF);
        }

        this.renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.SHARPNESS_NAME, this.capability.getEnchantment(SOUL_SHARPNESS, this.type)), 0);
        this.renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.SWEEPING_EDGE_NAME, this.capability.getEnchantment(SOUL_SWEEPING_EDGE, this.type)), 1);
        this.renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.LOOTING_NAME, this.capability.getEnchantment(SOUL_LOOTING, this.type)), 2);
        this.renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.FIRE_ASPECT_NAME, this.capability.getEnchantment(SOUL_FIRE_ASPECT, this.type)), 3);
        this.renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.KNOCKBACK_ENCHANTMENT_NAME, this.capability.getEnchantment(SOUL_KNOCKBACK, this.type)), 4);
        this.renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.SMITE_NAME, this.capability.getEnchantment(SOUL_SMITE, this.type)), 5);
        this.renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.BANE_OF_ARTHROPODS_NAME, this.capability.getEnchantment(SOUL_BANE_OF_ARTHROPODS, this.type)), 6);
    }

    private void drawSkills() {
        for (int i = 0; i < capability.getDatum(DATA.skills, this.type); i++) {
            this.drawCenteredString(this.fontRenderer, capability.getCurrentType().getSkills()[i],
                    width / 2, (i + 2) * height / 16, 0xFFFFFF);
        }
    }

    @Override
    public void actionPerformed(final GuiButton button) {
        super.actionPerformed(button);

        switch (button.id) {
            case 0:
            case 1:
            case 2:
                final SoulType type = SoulWeaponType.get(button.id);
                final GuiScreen screen = !SoulItemHelper.hasSoulWeapon(this.mc.player)
                        ? null
                        : new SoulWeaponMenu();

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
                    amount = this.capability.getDatum(DATA.attributePoints, this.type);
                }

                Main.CHANNEL.sendToServer(new SWeaponAttributePoints(amount, getAttribute(button.id - 4), this.type));
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
                    amount = this.capability.getDatum(DATA.enchantmentPoints, this.type);
                }

                Main.CHANNEL.sendToServer(new SWeaponEnchantmentPoints(amount, SoulWeaponEnchantment.get(button.id - 9), this.type));
                break;
            case 16:
            case 17:
            case 18:
            case 19:
                final int tab = button.id - 16;
                this.mc.displayGuiScreen(new SoulWeaponMenu(tab));
                break;
            case 20:
                Main.CHANNEL.sendToServer(new SWeaponResetAttributes(this.type));
                break;
            case 21:
                Main.CHANNEL.sendToServer(new SWeaponResetEnchantments(this.type));
                break;
            case 22:
                if (capability.getBoundSlot() == this.slot) {
                    capability.unbindSlot();
                } else {
                    capability.bindSlot(this.slot);
                }

                this.mc.displayGuiScreen(new SoulWeaponMenu());
                Main.CHANNEL.sendToServer(new SWeaponBindSlot(this.slot));
                break;
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                amount = 1;

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                    amount = this.capability.getDatum(DATA.spentAttributePoints, this.type);
                }

                Main.CHANNEL.sendToServer(new SWeaponAttributePoints(-amount, getAttribute(button.id - 23), this.type));
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
                    amount = this.capability.getDatum(DATA.spentEnchantmentPoints, this.type);
                }

                Main.CHANNEL.sendToServer(new SWeaponEnchantmentPoints(-amount, SoulWeaponEnchantment.get(button.id - 28), this.type));
        }
    }

    private static SoulAttribute getAttribute(final int index) {
        switch (index) {
            case 0:
                return ATTACK_SPEED;
            case 1:
                return ATTACK_DAMAGE;
            case 2:
                return CRITICAL;
            case 3:
                return KNOCKBACK_ATTRIBUTE;
            case 4:
                return EFFICIENCY_ATTRIBUTE;
            default:
                return null;
        }
    }
}
