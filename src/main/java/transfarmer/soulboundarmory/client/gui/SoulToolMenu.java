package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.item.IItemSoulboundTool;
import transfarmer.soulboundarmory.network.server.tool.C2SToolAttributePoints;
import transfarmer.soulboundarmory.network.server.tool.C2SToolBindSlot;
import transfarmer.soulboundarmory.network.server.tool.C2SToolTab;
import transfarmer.soulboundarmory.network.server.tool.C2SToolType;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;
import transfarmer.soulboundarmory.util.ItemUtil;

import javax.annotation.Nonnull;
import javax.annotation.meta.When;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.HARVEST_LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.REACH_DISTANCE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SKILLS;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SPENT_ATTRIBUTE_POINTS;

@SideOnly(CLIENT)
public class SoulToolMenu extends Menu {
    public SoulToolMenu() {
        super(3, Items.WOODEN_PICKAXE);
    }

    public SoulToolMenu(final int tab) {
        this();
        this.capability.setCurrentTab(tab);
        Main.CHANNEL.sendToServer(new C2SToolTab(tab));
    }

    @Override
    public void initGui() {
        super.initGui();

        if (ItemUtil.getClassEquippedItemStack(this.mc.player, IItemSoulboundTool.class) != null) {
            this.tabs[0] = addButton(guiFactory.tabButton(16, 0, Mappings.MENU_BUTTON_ATTRIBUTES));
            this.tabs[1] = addButton(guiFactory.tabButton(17, 1, Mappings.MENU_BUTTON_ENCHANTMENTS));
            this.tabs[2] = addButton(guiFactory.tabButton(18, 2, Mappings.MENU_BUTTON_SKILLS));
            this.tabs[this.capability.getCurrentTab()].enabled = false;
        }

        switch (this.capability.getCurrentTab()) {
            case 0:
                this.displayAttributes();
                break;
            case 1:
                this.displayEnchantments();
                break;
            case 2:
                this.displaySkills();
                break;
            default:
                this.displayConfirmation();
        }
    }

    @Override
    protected boolean displayXPBar() {
        return this.capability.getCurrentTab() >= 0 && this.capability.getCurrentTab() <= 2;
    }

    private void displayConfirmation() {
        final int buttonWidth = 128;
        final int buttonHeight = 20;
        final int xCenter = (width - buttonWidth) / 2;
        final int yCenter = (height - buttonHeight) / 2;
        final int ySep = 32;
        final GuiButton choiceButton = this.addButton(new GuiButton(0, xCenter, yCenter - ySep, buttonWidth, buttonHeight, Mappings.SOUL_PICK_NAME));

        if (this.capability.hasSoulItem() || !ItemUtil.hasItem(Items.WOODEN_PICKAXE, this.mc.player)) {
            choiceButton.enabled = false;
        }
    }

    protected void displayAttributes() {
        final GuiButton resetButton = this.addButton(this.guiFactory.resetButton(20));
        final GuiButton[] removePointButtons = guiFactory.removePointsButtons(23, this.capability.size(ATTRIBUTE));
        final GuiButton[] addPointButtons = guiFactory.addPointButtons(4, this.capability.size(ATTRIBUTE), this.capability.getDatum(this.item, ATTRIBUTE_POINTS));
        resetButton.enabled = this.capability.getDatum(this.item, SPENT_ATTRIBUTE_POINTS) > 0;

        for (int index = 0; index < this.capability.size(ATTRIBUTE); index++) {
            final Statistic statistic = this.capability.getStatistic(this.item, this.getAttribute(index));

            removePointButtons[index].enabled = statistic.greaterThan(statistic.min());
        }

        addPointButtons[2].enabled &= this.capability.getAttribute(this.item, HARVEST_LEVEL) < 3;
    }

    private void displaySkills() {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        switch (this.capability.getCurrentTab()) {
            case 0:
                this.drawAttributes();
                break;
            case 1:
                this.drawEnchantments();
                break;
            case 2:
                this.drawSkills();
                break;
            default:
                this.drawSelection();
        }
    }

    private void drawSelection() {
        if (!this.capability.hasSoulItem()) {
            this.drawCenteredString(this.fontRenderer, Mappings.MENU_CONFIRMATION,
                    Math.round(width / 2F), 40, 0xFFFFFF);
        }
    }

    private void drawAttributes() {
        final String efficiency = String.format("%s%s: %%s", Mappings.WEAPON_EFFICIENCY_FORMAT, Mappings.EFFICIENCY_NAME);
        final String harvestLevel = String.format("%s%s: %%s (%s)", Mappings.HARVEST_LEVEL_FORMAT, Mappings.HARVEST_LEVEL_NAME,
                Mappings.getMiningLevels()[(int) this.capability.getAttribute(this.item, HARVEST_LEVEL)]);
        final String reachDistance = String.format("%s%s: %%s", Mappings.REACH_DISTANCE_FORMAT, Mappings.REACH_DISTANCE_NAME);
        final int points = this.capability.getDatum(this.item, ATTRIBUTE_POINTS);

        if (points > 0) {
            this.drawCenteredString(this.fontRenderer, String.format("%s: %d", Mappings.MENU_POINTS, points),
                    Math.round(width / 2F), 4, 0xFFFFFF);
        }

        this.renderer.drawMiddleAttribute(efficiency, capability.getAttribute(this.item, EFFICIENCY_ATTRIBUTE), 0);
        this.renderer.drawMiddleAttribute(reachDistance, capability.getAttribute(this.item, REACH_DISTANCE), 1);
        this.renderer.drawMiddleAttribute(harvestLevel, capability.getAttribute(this.item, HARVEST_LEVEL), 2);
    }

    private void drawSkills() {
        for (int i = 0; i < this.capability.getDatum(this.item, SKILLS); i++) {
            this.drawCenteredString(this.fontRenderer, this.capability.getSkills()[i].getName(),
                    this.width / 2, (i + 2) * this.height / 16, 0xFFFFFF);
        }
    }

    @Override
    public void actionPerformed(final GuiButton button) {
        super.actionPerformed(button);

        switch (button.id) {
            case 0:
                final IItem type = this.capability.getItemType(button.id);
                final GuiScreen screen = !this.capability.hasSoulItem() ? null : new SoulToolMenu();

                if (screen == null) {
                    this.capability.setCurrentTab(0);
                    Main.CHANNEL.sendToServer(new C2SToolTab(this.capability.getCurrentTab()));
                }

                this.capability.setItemType(type);
                this.mc.displayGuiScreen(screen);
                Main.CHANNEL.sendToServer(new C2SToolType(type));

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

                Main.CHANNEL.sendToServer(new C2SToolAttributePoints(this.item, this.getAttribute(button.id - 4), amount));
                break;
            case 22:
                if (capability.getBoundSlot() == this.slot) {
                    capability.unbindSlot();
                } else {
                    capability.bindSlot(this.slot);
                }

                this.mc.displayGuiScreen(new SoulToolMenu());
                Main.CHANNEL.sendToServer(new C2SToolBindSlot(this.slot));
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

                Main.CHANNEL.sendToServer(new C2SToolAttributePoints(this.item, this.getAttribute(button.id - 23), -amount));
                break;
        }
    }

    @Nonnull(when = When.MAYBE)
    @Override
    protected IStatistic getAttribute(final int index) {
        switch (index) {
            case 0:
                return EFFICIENCY_ATTRIBUTE;
            case 1:
                return REACH_DISTANCE;
            case 2:
                return HARVEST_LEVEL;
            default:
                return null;
        }
    }
}
