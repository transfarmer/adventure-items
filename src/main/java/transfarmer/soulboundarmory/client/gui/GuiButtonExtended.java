package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.GuiButton;

public class GuiButtonExtended extends GuiButton {
    public int endX;
    public int endY;

    public GuiButtonExtended(final int id, final int x, final int y, final String text) {
        this(id, x, y, 200, 20, text);
    }

    public GuiButtonExtended(final int id, final int x, final int y, final int width, final int height, final String text) {
        super(id, x, y, width, height, text);

        this.endX = this.x + this.width;
        this.endY = this.y + this.height;
    }

    public boolean isMouseHoveringOver() {
        return this.hovered;
    }
}
