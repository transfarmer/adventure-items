package transfarmer.adventureitems.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import transfarmer.adventureitems.gui.AttributeScreen;
import transfarmer.adventureitems.Main;
import transfarmer.adventureitems.capability.ISoulWeapon;
import transfarmer.adventureitems.capability.SoulWeaponProvider;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;
import static net.minecraftforge.event.TickEvent.Phase.END;
import static transfarmer.adventureitems.Keybindings.KEYBINDINGS;


@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class ForgeEventSubscriber {
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(Main.MODID, "type"), new SoulWeaponProvider());
        }
    }

    @OnlyIn(CLIENT)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (KEYBINDINGS[0].isKeyDown() && event.phase == END) {
            Minecraft.getInstance().player.getCapability(SoulWeaponProvider.WEAPON_TYPE).ifPresent((ISoulWeapon capability) -> {
                Screen screen;

                if (capability.getCurrentType() != null) {
                    screen = new AttributeScreen(new TranslationTextComponent("menu.adventureitems.attributes"));
                } else {
                    screen = new AttributeScreen(new TranslationTextComponent("menu.adventureitems.weapons"));
                }

                Minecraft.getInstance().displayGuiScreen(screen);
            });
        }
    }
}
