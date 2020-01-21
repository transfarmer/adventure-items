package transfarmer.adventureitems;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SideOnly(CLIENT)
public class Keybindings {
    public static final KeyBinding[] KEYBINDINGS = new KeyBinding[1];

    public static void register() {
        KEYBINDINGS[0] = new KeyBinding("key.adventureitems.attributes", 'R', "adventure items");

        for (KeyBinding keybinding : KEYBINDINGS) {
            Main.LOGGER.info("new keybinding registered");
            ClientRegistry.registerKeyBinding(keybinding);
        }

        Main.LOGGER.info("all keybindings registered");
    }
}
