package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import transfarmer.soulboundarmory.capability.soulbound.IItemCapability;

public class GuiSkills extends GuiScreen {
    private static final ResourceLocation WINDOW = new ResourceLocation("textures/gui/advancements/window.png");

    @Override
    public void initGui() {
        super.initGui();
        final int x = this.width / 2 - 100;
        final int y = this.height / 2 - 10;
        final int width = 200;
        final int height = 20;
        this.addButton(new GuiButton(0, x, y, width, height, "button"));
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(final GuiButton button) {
        switch (button.id) {
            case 0:
                this.mc.player.sendMessage(new TextComponentString("you clicked a button"));
        }
    }
}
