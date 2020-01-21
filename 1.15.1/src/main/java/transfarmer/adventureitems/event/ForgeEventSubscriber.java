package transfarmer.adventureitems.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
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
import transfarmer.adventureitems.Main;
import transfarmer.adventureitems.capability.ISoulWeapon;
import transfarmer.adventureitems.capability.SoulWeaponProvider;
import transfarmer.adventureitems.gui.SoulWeaponMenu;
import transfarmer.adventureitems.network.ClientWeaponData;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;
import static net.minecraftforge.event.TickEvent.Phase.END;
import static transfarmer.adventureitems.Keybindings.KEYBINDINGS;
import static transfarmer.adventureitems.capability.SoulWeapon.WeaponType.getItems;
import static transfarmer.adventureitems.capability.SoulWeaponProvider.CAPABILITY;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class ForgeEventSubscriber {
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(Main.MODID, "soulweapon"), new SoulWeaponProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        updateSoulWeapon(event);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        updateSoulWeapon(event);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        updateSoulWeapon(event);
    }

    private static <T extends PlayerEvent> void updateSoulWeapon(T event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        player.getCapability(CAPABILITY).ifPresent((ISoulWeapon instance) ->
                Main.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                        new ClientWeaponData(instance.getCurrentTypeIndex(),
                                instance.getBigswordAttributes(),
                                instance.getSwordAttributes(),
                                instance.getDaggerAttributes())));
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(CAPABILITY).ifPresent((ISoulWeapon originalInstance) -> {
                ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
                player.getCapability(CAPABILITY).ifPresent((ISoulWeapon instance) -> {
                    Main.LOGGER.info("both capabilities are present on the server side");
                    instance.setCurrentTypeIndex(originalInstance.getCurrentTypeIndex());
                    instance.setAttributes(originalInstance.getBigswordAttributes(),
                            originalInstance.getSwordAttributes(),
                            originalInstance.getDaggerAttributes());
                });
            });
        }
    }

    // clear extraneous soul weapons in inventory

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.isCreative() && event.player.inventory.hasAny(getItems()) && event.phase == END) {
            event.player.getCapability(CAPABILITY).ifPresent((ISoulWeapon instance) ->
                event.player.inventory.clearMatchingItems((ItemStack itemStack) -> {
                    Item weapon = instance.getCurrentTypeIndex() == -1 ? null : instance.getItem();
                    return getItems().contains(itemStack.getItem())
                           && !itemStack.getItem().equals(weapon);
                }, event.player.inventory.getSizeInventory()));
        }
    }

    @OnlyIn(CLIENT)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (KEYBINDINGS[0].isKeyDown() && event.phase == END) {
            PlayerEntity player = Minecraft.getInstance().player;
            player.getCapability(CAPABILITY).ifPresent((ISoulWeapon instance) -> {
                Screen screen = null;

                if (player.getHeldItemMainhand().isItemEqual(new ItemStack(Items.WOODEN_SWORD))
                        || (ISoulWeapon.isSoulWeaponEquipped(player) && instance.getCurrentTypeIndex() == -1)) {
                    screen = new SoulWeaponMenu(new TranslationTextComponent("menu.adventureitems.weapons"));
                } else if (ISoulWeapon.isSoulWeaponEquipped(player) && instance.getCurrentTypeIndex() != -1) {
                    screen = new SoulWeaponMenu(new TranslationTextComponent("menu.adventureitems.attributes"),
                            instance.getName());
                }

                Minecraft.getInstance().displayGuiScreen(screen);
            });
        }
    }
}
