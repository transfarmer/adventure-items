package transfarmer.adventureitems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import transfarmer.adventureitems.capabilities.ISoulWeapon;
import transfarmer.adventureitems.capabilities.Provider;


@EventBusSubscriber(value = Dist.CLIENT, modid = Main.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class Keybindings {
    public static KeyBinding[] keybindings = new KeyBinding[1];

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        Minecraft.getInstance().player.sendChatMessage("key pressed");
        if (Keybindings.keybindings[0].isPressed()) {
            Minecraft.getInstance().player.sendChatMessage("key pressed");
            ISoulWeapon type = (ISoulWeapon) Minecraft.getInstance().player.getCapability(Provider.TYPE);

            if (type.getCurrentType() != null) {
                Minecraft.getInstance().displayGuiScreen(new AttributeScreen(new TranslationTextComponent("menu.adventureitems.attributes")));
            } else {
                Minecraft.getInstance().displayGuiScreen(new AttributeScreen(new TranslationTextComponent("menu.adventureitems.weapons")));
            }
        }
    }

    public static void register() {
        keybindings[0] = new KeyBinding("key.attributes", 'R', "adventure items");

        for (KeyBinding keybinding : keybindings) {
            ClientRegistry.registerKeyBinding(keybinding);
        }
    }
}
    /*
    public static class RegisterTask implements Runnable {
        public void run() {
            keybindings[0] = new KeyBinding("key.attributes", 'R', "adventure items");

            for (KeyBinding keybinding : keybindings) {
                ClientRegistry.registerKeyBinding(keybinding);
            }
        }

        public static Supplier<Runnable> get() {
            return () -> {
                keybindings[0] = new KeyBinding("key.attributes", 'R', "adventure items");

                for (KeyBinding keybinding : keybindings) {
                    ClientRegistry.registerKeyBinding(keybinding);
                }

                return null;
            };
        }
    }
    */
