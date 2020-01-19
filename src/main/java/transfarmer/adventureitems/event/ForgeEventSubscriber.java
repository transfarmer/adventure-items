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
import transfarmer.adventureitems.gui.SoulWeaponMenu;
import transfarmer.adventureitems.Main;
import transfarmer.adventureitems.capability.ISoulWeapon;
import transfarmer.adventureitems.capability.SoulWeaponProvider;
import transfarmer.adventureitems.SoulWeapons;
import transfarmer.adventureitems.network.UpdateWeaponData;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;
import static net.minecraftforge.event.TickEvent.Phase.END;
import static transfarmer.adventureitems.Keybindings.KEYBINDINGS;
import static transfarmer.adventureitems.capability.SoulWeaponProvider.SOUL_WEAPON;

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

    private static <T extends PlayerEvent> void updateSoulWeapon(T event) {
        PlayerEntity player = event.getPlayer();
        player.getCapability(SOUL_WEAPON).ifPresent((ISoulWeapon capability) -> {
            if (capability.getWeaponType() == null) return;
            Main.LOGGER.info(capability.getWeaponType());

            Main.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                    new UpdateWeaponData(capability.getWeaponType(), capability.getLevel(), capability.getPoints(),
                            capability.getSpecial(), capability.getMaxSpecial(), capability.getHardness(),
                            capability.getKnockback(), capability.getAttackDamage(), capability.getCritical()
                    ));
        });
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerEntity original = event.getOriginal();
        original.getCapability(SOUL_WEAPON).ifPresent((ISoulWeapon originalCapability) ->
            event.getPlayer().getCapability(SOUL_WEAPON).ifPresent((ISoulWeapon capability) ->
                capability.setData(originalCapability.getWeaponType(), originalCapability.getLevel(),
                        originalCapability.getPoints(), originalCapability.getSpecial(),
                        originalCapability.getMaxSpecial(), originalCapability.getHardness(),
                        originalCapability.getKnockback(), originalCapability.getAttackDamage(),
                        originalCapability.getCritical())
            )
        );
    }

    /* PlayerEntity has a field WeaponType weaponType and 3 types exist: BIGSWORD, SWORD, and DAGGER.
    * If event.player has items of any of these types that do not match its capability weaponType value, then
    * they are removed. */

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.inventory.hasAny(SoulWeapons.getSoulWeapons()) && event.phase == END) {
            event.player.getCapability(SOUL_WEAPON).ifPresent((ISoulWeapon capability) ->
                event.player.inventory.clearMatchingItems((ItemStack itemStack) -> {
                    Item weapon = capability.getWeaponType() == null ? null : capability.getWeaponType().getItem();
                    return SoulWeapons.getSoulWeapons().contains(itemStack.getItem())
                           && !itemStack.getItem().equals(weapon);
                },
                event.player.inventory.getSizeInventory()
                )
            );
        }
    }

    @OnlyIn(CLIENT)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (KEYBINDINGS[0].isKeyDown() && event.phase == END) {
            PlayerEntity player = Minecraft.getInstance().player;
            player.getCapability(SOUL_WEAPON).ifPresent((ISoulWeapon capability) -> {
                Screen screen = null;

                if (player.getHeldItemMainhand().isItemEqual(new ItemStack(Items.WOODEN_SWORD))
                    || (capability.isSoulWeaponEquipped(player) && capability.getWeaponType() == null)) {
                    screen = new SoulWeaponMenu(new TranslationTextComponent("menu.adventureitems.weapons"));
                } else if (capability.isSoulWeaponEquipped(player) && capability.getWeaponType() != null) {
                    screen = new SoulWeaponMenu(new TranslationTextComponent("menu.adventureitems.attributes"),
                            capability.getWeaponType());
                }

                Minecraft.getInstance().displayGuiScreen(screen);
            });
        }
    }
}
