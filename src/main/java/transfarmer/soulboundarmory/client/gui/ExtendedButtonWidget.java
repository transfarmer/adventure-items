package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;

public class ExtendedButtonWidget extends ButtonWidget {
    protected final PressAction secondaryAction;
    public final int endX;
    public final int endY;

    public ExtendedButtonWidget(final int x, final int y, final String text, final PressAction action) {
        this(x, y, 200, 20, text, action);
    }

    public ExtendedButtonWidget(final int x, final int y, final int width, final int height, final String text,
                                final PressAction action) {
        super(x, y, width, height, text, action);

        this.secondaryAction = null;

        this.endX = this.x + this.width;
        this.endY = this.y + this.height;
    }

    public ExtendedButtonWidget(final int x, final int y, final int width, final int height, final String text,
                                final PressAction primaryAction, final PressAction secondaryAction) {
        super(x, y, width, height, text, primaryAction);

        this.secondaryAction = secondaryAction;

        this.endX = this.x + this.width;
        this.endY = this.y + this.height;
    }

    public boolean isMouseHoveringOver() {
        return this.isHovered;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) {
            if (this.active && this.visible) {
                if (this.isValidSecondaryButton(button)) {
                    if (this.clicked(mouseX, mouseY)) {
                        this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                        this.onRightClick();

                        return true;
                    }
                }

            }

            return false;
        }

        return false;
    }

    public boolean isValidSecondaryButton(final int button) {
        return this.secondaryAction != null && button == 1;
    }

    public void onRightClick() {
        if (this.secondaryAction != null) {
            this.secondaryAction.onPress(this);
        }
    }
}
