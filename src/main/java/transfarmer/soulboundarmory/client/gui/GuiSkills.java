package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import transfarmer.soulboundarmory.capability.soulbound.IItemCapability;

public class GuiSkills extends GuiScreen {
    private static final ResourceLocation WINDOW = new ResourceLocation("textures/gui/advancements/window.png");

    private final IItemCapability capability;

    public GuiSkills(final IItemCapability capability) {
        this.capability = capability;
    }

    @Override
    public void initGui() {
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
    }
}
