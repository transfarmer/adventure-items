package transfarmer.adventureitems.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.PacketDistributor;
import transfarmer.adventureitems.gui.AttributeScreen;
import transfarmer.adventureitems.Main;
import transfarmer.adventureitems.capability.ISoulWeapon;
import transfarmer.adventureitems.capability.SoulWeaponProvider;
import transfarmer.adventureitems.SoulWeapons;
import transfarmer.adventureitems.network.ApplyWeaponType;

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

    @SubscribeEvent
    public static void onPlayerLoggedin(PlayerEvent.PlayerLoggedInEvent event) {
        updateSoulWeapon(event);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        updateSoulWeapon(event);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        updateSoulWeapon(event);
    }

    private static <T extends PlayerEvent> void updateSoulWeapon(T event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        player.getCapability(SoulWeaponProvider.WEAPON_TYPE).ifPresent((ISoulWeapon capability) -> {
            if (capability.getCurrentType() == null) return;

            Main.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new ApplyWeaponType(capability.getCurrentType()));
        });
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.inventory.hasAny(SoulWeapons.getSoulWeapons()) && event.phase == END) {
            PlayerEntity player = event.player;
            PlayerInventory inventory = player.inventory;

        }
    }

    @OnlyIn(CLIENT)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (KEYBINDINGS[0].isKeyDown() && event.phase == END) {
            PlayerEntity player = Minecraft.getInstance().player;
            player.getCapability(SoulWeaponProvider.WEAPON_TYPE).ifPresent((ISoulWeapon capability) -> {
                Screen screen = null;

                if (player.getHeldItemMainhand().isItemEqual(new ItemStack(Items.WOODEN_SWORD))) {
                    screen = new AttributeScreen(new TranslationTextComponent("menu.adventureitems.weapons"));
                } else if (capability.isSoulWeaponEquipped(player)) {
                    screen = new AttributeScreen(new TranslationTextComponent("menu.adventureitems.attributes"));
                }

                Minecraft.getInstance().displayGuiScreen(screen);
            });
        }
    }
}
