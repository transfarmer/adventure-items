package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.widget.ButtonWidget;

public class ExtendedButtonWidget extends ButtonWidget {
    public int endX;
    public int endY;

    public ExtendedButtonWidget(final int x, final int y, final String text, final PressAction action) {
        this(x, y, 200, 20, text, action);
    }

    public ExtendedButtonWidget(final int x, final int y, final int width, final int height, final String text, final PressAction action) {
        super(x, y, width, height, text, action);

        this.endX = this.x + this.width;
        this.endY = this.y + this.height;
    }

    public boolean isMouseHoveringOver() {
        return this.isHovered;
    }
}
