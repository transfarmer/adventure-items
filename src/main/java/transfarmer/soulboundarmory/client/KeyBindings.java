package transfarmer.soulboundarmory.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import transfarmer.soulboundarmory.i18n.Mappings;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SideOnly(CLIENT)
public class KeyBindings {
    public static final KeyBinding MENU_KEY = new KeyBinding(Mappings.MENU_KEY_NAME, Keyboard.KEY_R, Mappings.KEY_CATEGORY);
}
