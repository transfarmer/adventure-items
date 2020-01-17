package transfarmer.adventureitems;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;


@OnlyIn(CLIENT)
public class Keybindings {
    public static final KeyBinding[] KEYBINDINGS = new KeyBinding[1];

    public static void register() {
        KEYBINDINGS[0] = new KeyBinding("key.adventureitems.attributes", 'R', "adventure items");

        for (KeyBinding keybinding : KEYBINDINGS) {
            ClientRegistry.registerKeyBinding(keybinding);
        }
    }
}
