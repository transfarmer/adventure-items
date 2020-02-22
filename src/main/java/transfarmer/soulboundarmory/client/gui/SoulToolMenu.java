package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.network.server.tool.*;
import transfarmer.soulboundarmory.statistics.SoulAttribute;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute;
import transfarmer.soulboundarmory.statistics.tool.SoulToolEnchantment;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;
import transfarmer.util.ItemHelper;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.statistics.SoulAttribute.*;
import static transfarmer.soulboundarmory.statistics.SoulDatum.*;
import static transfarmer.soulboundarmory.statistics.SoulEnchantment.*;

@SideOnly(CLIENT)
public class SoulToolMenu extends Menu {
    public SoulToolMenu() {
        super(3, Items.WOODEN_PICKAXE);
    }

    public SoulToolMenu(final int tab) {
        this();
        this.capability.setCurrentTab(tab);
        Main.CHANNEL.sendToServer(new SToolTab(tab));
    }

    @Override
    public void initGui() {
        if (ItemHelper.getClassEquippedItemStack(this.mc.player, IItemSoulTool.class) != null) {
            final String text = this.mc.player.inventory.currentItem != this.capability.getBoundSlot()
                    ? Mappings.MENU_BUTTON_BIND
                    : Mappings.MENU_BUTTON_UNBIND;

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

        if (this.capability.hasSoulItem() || !ItemHelper.hasItem(Items.WOODEN_PICKAXE, this.mc.player)) {
            choiceButton.enabled = false;
        }
    }

    private void showAttributes() {
        final GuiButton resetButton = this.addButton(this.guiFactory.resetButton(20));
        final GuiButton[] removePointButtons = this.addRemovePointButtons(23, this.capability.getAttributeAmount());
        final GuiButton[] addPointButtons = this.addAddPointButtons(4, this.capability.getAttributeAmount(), this.capability.getDatum(ATTRIBUTE_POINTS, this.type));
        resetButton.enabled = this.capability.getDatum(SPENT_ATTRIBUTE_POINTS, this.type) > 0;

        for (int index = 0; index < this.capability.getAttributeAmount(); index++) {
            removePointButtons[index].enabled = this.capability.getAttribute(SoulAttribute.get(this.type, index), this.type) > 0;
        }

        addPointButtons[HARVEST_LEVEL.getIndex()].enabled &= this.capability.getAttribute(HARVEST_LEVEL, this.type) < 3;
    }

    private void showEnchantments() {
        final GuiButton resetButton = this.addButton(guiFactory.resetButton(21));
        final GuiButton[] removePointButtons = addRemovePointButtons(28, this.capability.getEnchantmentAmount());
        resetButton.enabled = this.capability.getDatum(SPENT_ENCHANTMENT_POINTS, this.type) > 0;

        this.addAddPointButtons(9, this.capability.getEnchantmentAmount(), this.capability.getDatum(ENCHANTMENT_POINTS, this.type));

        for (int index = 0; index < this.capability.getEnchantmentAmount(); index++) {
            removePointButtons[index].enabled = this.capability.getEnchantment(SoulToolEnchantment.get(index), this.type) > 0;
        }
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
        if (!this.capability.hasSoulItem()) {
            this.drawCenteredString(this.fontRenderer, Mappings.MENU_CONFIRMATION,
                    Math.round(width / 2F), 40, 0xFFFFFF);
        }
    }

    private void drawAttributes(final int mouseX, final int mouseY) {
        final String efficiency = String.format("%s%s: %%s", Mappings.WEAPON_EFFICIENCY_FORMAT, Mappings.EFFICIENCY_NAME);
        final String harvestLevel = String.format("%s%s: %%s (%s)", Mappings.HARVEST_LEVEL_FORMAT, Mappings.HARVEST_LEVEL_NAME,
                Mappings.getMiningLevels()[(int) this.capability.getAttribute(HARVEST_LEVEL, this.type)]);
        final String reachDistance = String.format("%s%s: %%s", Mappings.REACH_DISTANCE_FORMAT, Mappings.REACH_DISTANCE_NAME);
        final int points = this.capability.getDatum(ATTRIBUTE_POINTS, this.type);

        if (points > 0) {
            this.drawCenteredString(this.fontRenderer, String.format("%s: %d", Mappings.MENU_POINTS, points),
                    Math.round(width / 2F), 4, 0xFFFFFF);
        }

        this.renderer.drawMiddleAttribute(efficiency, capability.getAttribute(EFFICIENCY_ATTRIBUTE, this.type, true, true), 0);
        this.renderer.drawMiddleAttribute(reachDistance, capability.getAttribute(REACH_DISTANCE, this.type, true, true), 1);
        this.renderer.drawMiddleAttribute(harvestLevel, capability.getAttribute(HARVEST_LEVEL, this.type), 2);

        this.drawXPBar(mouseX, mouseY);
    }

    private void drawEnchantments(final int mouseX, final int mouseY) {
        final int points = this.capability.getDatum(ENCHANTMENT_POINTS, this.type);

        if (points > 0) {
            this.drawCenteredString(this.fontRenderer, String.format("%s: %d", Mappings.MENU_POINTS, points),
                    Math.round(width / 2F), 4, 0xFFFFFF);
        }

        this.renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.EFFICIENCY_ENCHANTMENT_NAME, this.capability.getEnchantment(SOUL_EFFICIENCY, this.type)), 0);
        this.renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.FORTUNE_NAME, this.capability.getEnchantment(SOUL_FORTUNE, this.type)), 1);
        this.renderer.drawMiddleEnchantment(String.format("%s: %s", Mappings.SILK_TOUCH_NAME, this.capability.getEnchantment(SOUL_SILK_TOUCH, this.type)), 2);

        this.drawXPBar(mouseX, mouseY);
    }

    private void drawSkills(final int mouseX, final int mouseY) {
        for (int i = 0; i < this.capability.getDatum(SKILLS, this.type); i++) {
            this.drawCenteredString(this.fontRenderer, this.capability.getCurrentType().getSkills()[i],
                    this.width / 2, (i + 2) * this.height / 16, 0xFFFFFF);
        }

        this.drawXPBar(mouseX, mouseY);
    }

    private void drawTraits(final int mouseX, final int mouseY) {}

    @Override
    public void actionPerformed(final GuiButton button) {
        switch (button.id) {
            case 0:
                final SoulType type = SoulToolType.get(button.id);
                final GuiScreen screen = !this.capability.hasSoulItem() ? null : new SoulToolMenu();

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
                    amount = this.capability.getDatum(ATTRIBUTE_POINTS, this.type);
                }

                Main.CHANNEL.sendToServer(new SToolAttributePoints(amount, SoulToolAttribute.get(button.id - 4), this.type));
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
                    amount = this.capability.getDatum(ENCHANTMENT_POINTS, this.type);
                }

                Main.CHANNEL.sendToServer(new SToolEnchantmentPoints(amount, SoulToolEnchantment.get(button.id - 9), this.type));
                break;
            case 16:
            case 17:
            case 18:
            case 19:
                this.mc.displayGuiScreen(new SoulToolMenu(button.id - 16));
                break;
            case 20:
                Main.CHANNEL.sendToServer(new SToolResetAttributes(this.type));
                break;
            case 21:
                Main.CHANNEL.sendToServer(new SToolResetEnchantments(this.type));
                break;
            case 22:
                if (capability.getBoundSlot() == this.slot) {
                    capability.unbindSlot();
                } else {
                    capability.bindSlot(this.slot);
                }

                this.mc.displayGuiScreen(new SoulToolMenu());
                Main.CHANNEL.sendToServer(new SToolBindSlot(this.slot));
                break;
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                amount = 1;

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                    amount = this.capability.getDatum(SPENT_ATTRIBUTE_POINTS, this.type);
                }

                Main.CHANNEL.sendToServer(new SToolAttributePoints(-amount, SoulToolAttribute.get(button.id - 23), this.type));
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
                    amount = this.capability.getDatum(SPENT_ENCHANTMENT_POINTS, this.type);
                }

                Main.CHANNEL.sendToServer(new SToolEnchantmentPoints(-amount, SoulToolEnchantment.get(button.id - 28), this.type));
        }
    }
}
