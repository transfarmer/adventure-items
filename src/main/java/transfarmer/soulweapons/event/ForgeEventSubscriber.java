package transfarmer.soulweapons.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.WeaponType;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.capability.SoulWeaponProvider;
import transfarmer.soulweapons.gui.SoulWeaponMenu;
import transfarmer.soulweapons.network.ClientWeaponData;

import static net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import static net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.KeyBindings.WEAPON_MENU;
import static transfarmer.soulweapons.WeaponType.NONE;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

@EventBusSubscriber(modid = Main.MODID)
public class ForgeEventSubscriber {
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(Main.MODID, "soulweapon"), new SoulWeaponProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        for (int[] weapon : event.player.getCapability(CAPABILITY, null).getAttributes()) {
            if (weapon.length == 0) {
                Main.CHANNEL.sendTo(new ClientWeaponData(), (EntityPlayerMP) event.player);
                return;
            }
        }

        updatePlayer(event.player);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        updatePlayer(event.player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        updatePlayer(event.player);
    }

    private static void updatePlayer(EntityPlayer player) {
        ISoulWeapon instance = player.getCapability(CAPABILITY, null);
        Main.CHANNEL.sendTo(new ClientWeaponData(instance.getCurrentType(), instance.getAttributes()),
            (EntityPlayerMP) player);
    }

    @SubscribeEvent
    public static void onClone(Clone event) {
        if (event.isWasDeath()) {
            ISoulWeapon originalInstance = event.getOriginal().getCapability(CAPABILITY, null);
            ISoulWeapon instance = event.getEntityPlayer().getCapability(CAPABILITY, null);
            instance.setCurrentType(originalInstance.getCurrentType());
            instance.setAttributes(originalInstance.getAttributes());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (ISoulWeapon.hasSoulWeapon(event.player) && event.phase == END) {
            ISoulWeapon instance = event.player.getCapability(CAPABILITY, null);

            if (ISoulWeapon.isSoulWeaponEquipped(event.player)) {
                instance.setCurrentType(WeaponType.getType(event.player.inventory.getCurrentItem().getItem()));
            }

            if (!event.player.isCreative() && instance.getCurrentType() != NONE) {
                for (Item item : WeaponType.getItems()) {
                    if (item != instance.getItem()) {
                        event.player.inventory.clearMatchingItems(item, 0,
                            event.player.inventory.getSizeInventory(), null);
                    }
                }
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (WEAPON_MENU.isKeyDown() && event.phase == END) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            ISoulWeapon instance = player.getCapability(CAPABILITY, null);
            GuiScreen screen = null;

            if (ISoulWeapon.isSoulWeaponEquipped(player) && instance.getCurrentType() != NONE) {
                screen = new SoulWeaponMenu(I18n.format("menu.soulweapons.attributes"), instance.getCurrentType());
            } else if (player.getHeldItemMainhand().isItemEqual(new ItemStack(Items.WOODEN_SWORD))
                || (ISoulWeapon.isSoulWeaponEquipped(player) && instance.getCurrentType() == NONE)) {
                screen = new SoulWeaponMenu(I18n.format("menu.soulweapons.weapons"));
            }

            Minecraft.getMinecraft().displayGuiScreen(screen);
        }
    }
}
