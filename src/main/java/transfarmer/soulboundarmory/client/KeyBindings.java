package transfarmer.soulboundarmory.client;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import transfarmer.soulboundarmory.client.i18n.Mappings;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@Environment(CLIENT)
public class KeyBindings {
    public static final KeyBinding MENU_KEY = new KeyBinding(Mappings.MENU_KEY, Keyboard.KEY_R, Mappings.KEY_CATEGORY);
    public static final KeyBinding TOGGLE_XP_BAR_KEY = new KeyBinding(Mappings.TOGGLE_XP_BAR, Keyboard.KEY_X, Mappings.KEY_CATEGORY);
}
