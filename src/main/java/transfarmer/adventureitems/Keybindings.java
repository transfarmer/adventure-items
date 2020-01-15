package transfarmer.adventureitems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import transfarmer.adventureitems.capability.ISoulWeapon;
import transfarmer.adventureitems.capability.SoulWeaponProvider;


@EventBusSubscriber(value = Dist.CLIENT, modid = Main.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class Keybindings {
    public static KeyBinding[] keybindings = new KeyBinding[1];

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keybindings.keybindings[0].isPressed()) {
            Minecraft.getInstance().player.getCapability(SoulWeaponProvider.TYPE).ifPresent((ISoulWeapon capability) -> {
                if (capability.getCurrentType() != null) {
                    Minecraft.getInstance().displayGuiScreen(new AttributeScreen(new TranslationTextComponent("menu.adventureitems.attributes"), capability));
                } else {
                    Minecraft.getInstance().displayGuiScreen(new AttributeScreen(new TranslationTextComponent("menu.adventureitems.weapons"), capability));
                }
            });
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
