package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.network.server.weapon.C2SWeaponAttributePoints;
import transfarmer.soulboundarmory.network.server.weapon.C2SWeaponTab;
import transfarmer.soulboundarmory.network.server.weapon.C2SWeaponType;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;
import transfarmer.soulboundarmory.util.ItemUtil;

import javax.annotation.Nonnull;
import javax.annotation.meta.When;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTACK_DAMAGE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.CRITICAL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.KNOCKBACK_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SKILLS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ATTRIBUTE_POINTS;

@SideOnly(CLIENT)
public class SoulWeaponMenu extends Menu {
    public SoulWeaponMenu() {
        super(4, Items.WOODEN_SWORD);
    }

    public SoulWeaponMenu(final int tab) {
        this();
        this.capability.setCurrentTab(tab);
        Main.CHANNEL.sendToServer(new C2SWeaponTab(tab));
    }

    @Override
    public void initGui() {
        super.initGui();

        if (SoulItemHelper.isSoulWeaponEquipped(this.mc.player)) {
            this.tabs[0] = addButton(guiFactory.tabButton(16, 0, Mappings.MENU_SELECTION));
            this.tabs[1] = addButton(guiFactory.tabButton(17, 1, Mappings.MENU_BUTTON_ATTRIBUTES));
            this.tabs[2] = addButton(guiFactory.tabButton(18, 2, Mappings.MENU_BUTTON_ENCHANTMENTS));
            this.tabs[3] = addButton(guiFactory.tabButton(19, 3, Mappings.MENU_BUTTON_SKILLS));
            this.tabs[this.capability.getCurrentTab()].enabled = false;
        }

        switch (this.capability.getCurrentTab()) {
            case 0:
                this.displayWeapons();
                break;
            case 1:
                this.displayAttributes();
                break;
            case 2:
                this.displayEnchantments();
                break;
            case 3:
                this.displaySkills();
        }
    }

    @Override
    protected boolean displayXPBar() {
        return (this.capability.getCurrentTab() >= 1 && this.capability.getCurrentTab() <= 3);
    }

    private void displayWeapons() {
        final int buttonWidth = 128;
        final int buttonHeight = 20;
        final int xCenter = (width - buttonWidth) / 2;
        final int yCenter = (height - buttonHeight) / 2;
        final int ySep = 32;

        final GuiButton[] weaponButtons = {
                this.addButton(new GuiButton(0, xCenter, yCenter + ySep, buttonWidth, buttonHeight, Mappings.SOUL_DAGGER_NAME)),
                this.addButton(new GuiButton(1, xCenter, yCenter, buttonWidth, buttonHeight, Mappings.SOUL_SWORD_NAME)),
                this.addButton(new GuiButton(2, xCenter, yCenter - ySep, buttonWidth, buttonHeight, Mappings.SOUL_GREATSWORD_NAME))
        };

        if (SoulItemHelper.hasSoulWeapon(this.mc.player)) {
            weaponButtons[this.capability.getIndex()].enabled = false;
        } else if (this.capability.getItemType() != null && !ItemUtil.hasItem(Items.WOODEN_SWORD, this.mc.player)) {
            for (final GuiButton button : weaponButtons) {
                button.enabled = false;
            }
        }
    }

    protected void displayAttributes() {
        final int size = this.capability.size(ATTRIBUTE) - 1;
        final GuiButton resetButton = this.addButton(guiFactory.resetButton(20));
        final GuiButton[] addPointButtons = guiFactory.addPointButtons(4, size, this.capability.getDatum(this.item, ATTRIBUTE_POINTS));
        final GuiButton[] removePointButtons = guiFactory.removePointsButtons(23, size);
        resetButton.enabled = this.capability.getDatum(this.item, SPENT_ATTRIBUTE_POINTS) > 0;

        addPointButtons[2].enabled &= this.capability.getAttribute(this.item, CRITICAL) < 1;

        for (int index = 0; index < size; index++) {
            final Statistic statistic = this.capability.getStatistic(this.item, this.getAttribute(index));

            removePointButtons[index].enabled = statistic.greaterThan(statistic.min());
        }
    }

    private void displaySkills() {}

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
        final int points = this.capability.getDatum(this.item, ATTRIBUTE_POINTS);

        if (points > 0) {
            this.drawCenteredString(this.fontRenderer, String.format("%s: %d", Mappings.MENU_POINTS, points),
                    Math.round(width / 2F), 4, 0xFFFFFF);
        }

        this.renderer.drawMiddleAttribute(attackSpeed, capability.getAttribute(this.item, ATTACK_SPEED), 0);
        this.renderer.drawMiddleAttribute(attackDamage, capability.getAttributeTotal(this.item, ATTACK_DAMAGE), 1);
        this.renderer.drawMiddleAttribute(critical, capability.getAttribute(this.item, CRITICAL) * 100, 2);
        this.renderer.drawMiddleAttribute(knockback, capability.getAttribute(this.item, KNOCKBACK_ATTRIBUTE), 3);
        this.renderer.drawMiddleAttribute(efficiency, capability.getAttribute(this.item, EFFICIENCY_ATTRIBUTE), 4);
    }

    private void drawSkills() {
        for (int i = 0; i < capability.getDatum(this.item, SKILLS); i++) {
            this.drawCenteredString(this.fontRenderer, capability.getSkills()[i].getName(),
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
                final IItem type = this.capability.getItemType(button.id);
                final GuiScreen screen = !SoulItemHelper.hasSoulWeapon(this.mc.player)
                        ? null
                        : new SoulWeaponMenu();

                if (screen == null) {
                    this.capability.setCurrentTab(1);
                    Main.CHANNEL.sendToServer(new C2SWeaponTab(this.capability.getCurrentTab()));
                }

                this.capability.setItemType(type);
                this.mc.displayGuiScreen(screen);
                Main.CHANNEL.sendToServer(new C2SWeaponType(type));

                break;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                int amount = 1;

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                    amount = this.capability.getDatum(this.item, ATTRIBUTE_POINTS);
                }

                Main.CHANNEL.sendToServer(new C2SWeaponAttributePoints(this.item, this.getAttribute(button.id - 4), amount));
                break;
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                amount = 1;

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                    amount = this.capability.getDatum(this.item, SPENT_ATTRIBUTE_POINTS);
                }

                Main.CHANNEL.sendToServer(new C2SWeaponAttributePoints(this.item, this.getAttribute(button.id - 23), -amount));
                break;
        }
    }

    @Override
    @Nonnull(when = When.MAYBE)
    protected IStatistic getAttribute(final int index) {
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
