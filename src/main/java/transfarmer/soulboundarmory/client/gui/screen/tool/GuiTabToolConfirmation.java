package transfarmer.soulboundarmory.client.gui.screen.tool;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Items;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTab;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTabSoulbound;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.util.ItemUtil;

import java.util.List;

import static transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider.TOOLS;

public class GuiTabToolConfirmation extends GuiTabSoulbound {
    public GuiTabToolConfirmation(final List<GuiTab> tabs) {
        super(TOOLS, tabs);
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_CONFIRMATION;
    }

    @Override
    public void initGui() {
        super.initGui();

        final int buttonWidth = 128;
        final int buttonHeight = 20;
        final int xCenter = (width - buttonWidth) / 2;
        final int yCenter = (height - buttonHeight) / 2;
        final int ySep = 32;
        final GuiButton choiceButton = this.addButton(new GuiButton(0, xCenter, yCenter - ySep, buttonWidth, buttonHeight, Mappings.SOUL_PICK_NAME));

        if (this.capability.hasSoulboundItem() || !ItemUtil.hasItem(Items.WOODEN_PICKAXE, this.mc.player)) {
            choiceButton.enabled = false;
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawCenteredString(this.fontRenderer, Mappings.MENU_CONFIRMATION, Math.round(width / 2F), 40, 0xFFFFFF);
    }
}
