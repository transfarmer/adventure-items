package transfarmer.adventureitems;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;


@EventBusSubscriber(value = Dist.CLIENT, modid = Main.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class Keybindings {
    public static final KeyBinding[] KEYBINDINGS = new KeyBinding[1];

    public static void register() {
        KEYBINDINGS[0] = new KeyBinding("key.adventureitems.attributes", 'R', "adventure items");

        for (KeyBinding keybinding : KEYBINDINGS) {
            ClientRegistry.registerKeyBinding(keybinding);
        }
    }
}
