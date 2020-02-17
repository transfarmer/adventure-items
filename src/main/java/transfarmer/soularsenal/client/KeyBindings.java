package transfarmer.soularsenal.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SideOnly(CLIENT)
public class KeyBindings {
    public static final KeyBinding WEAPON_MENU = new KeyBinding("key.soulweapons.menu", Keyboard.KEY_X, "key.categories.soulweapons");
    public static final KeyBinding TOOL_MENU = new KeyBinding("key.soulTools.menu", Keyboard.KEY_Z, "key.categories.soulTools");
}
