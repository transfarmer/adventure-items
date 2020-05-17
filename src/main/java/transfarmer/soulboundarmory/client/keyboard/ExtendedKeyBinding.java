package transfarmer.soulboundarmory.client.keyboard;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ExtendedKeyBinding extends FabricKeyBinding {
    protected ExtendedKeyBinding(final Identifier identifier, final Type type, final int code, final String category) {
        super(identifier, type, code, category);
    }

    @Override
    public void setPressed(final boolean pressed) {
        super.setPressed(pressed);

        if (pressed) {
            if (!this.wasPressed()) {
                this.onPress();
            } else {
                this.onHold();
            }
        }
    }

    protected void onPress() {
    }

    protected void onHold() {
    }
}
