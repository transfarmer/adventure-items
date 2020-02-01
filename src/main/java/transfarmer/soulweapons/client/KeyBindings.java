package transfarmer.soulweapons.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SideOnly(CLIENT)
public class KeyBindings {
    public static final KeyBinding WEAPON_MENU = new KeyBinding("key.soulweapons.menu", Keyboard.KEY_R, "key.categories.soulweapons");
}
