package transfarmer.adventureitems.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.adventureitems.Main;
import transfarmer.adventureitems.capability.ISoulWeapon;
import transfarmer.adventureitems.capability.SoulWeapon;
import transfarmer.adventureitems.capability.SoulWeaponProvider;
import transfarmer.adventureitems.gui.SoulWeaponMenu;
import transfarmer.adventureitems.network.ClientWeaponData;

import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.adventureitems.Keybindings.KEYBINDINGS;
import static transfarmer.adventureitems.capability.SoulWeaponProvider.CAPABILITY;

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
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        Main.CHANNEL.sendTo(new ClientWeaponData(player.getCapability(CAPABILITY, null).getAttributes()), player);
    }

    @SubscribeEvent
    public static void onClone(Clone event) {
        if (event.isWasDeath()) {
            ISoulWeapon originalInstance = event.getOriginal().getCapability(CAPABILITY, null);
            ISoulWeapon instance = event.getEntityPlayer().getCapability(CAPABILITY, null);
            instance.setCurrentTypeIndex(originalInstance.getCurrentTypeIndex());
            instance.setAttributes(originalInstance.getAttributes());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.isCreative() && ISoulWeapon.hasSoulWeapon(event.player) && event.phase == END) {
            ISoulWeapon instance = event.player.getCapability(CAPABILITY, null);

            if (instance.getCurrentTypeIndex() != -1) {
                Item[] items = SoulWeapon.WeaponType.getItems();
                items[instance.getCurrentTypeIndex()] = null;

                for (Item item : items) {
                    if (item != null) {
                        event.player.inventory.clearMatchingItems(item, 0, event.player.inventory.getSizeInventory(),
                                new NBTTagCompound());
                    }
                }
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (KEYBINDINGS[0].isKeyDown() && event.phase == END) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            ISoulWeapon instance = player.getCapability(CAPABILITY, null);
            GuiScreen screen = null;

            if (player.getHeldItemMainhand().isItemEqual(new ItemStack(Items.WOODEN_SWORD))
                    || (ISoulWeapon.isSoulWeaponEquipped(player) && instance.getCurrentTypeIndex() == -1)) {
                screen = new SoulWeaponMenu(I18n.format("menu.adventureitems.weapons"));
            } else if (ISoulWeapon.isSoulWeaponEquipped(player) && instance.getCurrentTypeIndex() != -1) {
                screen = new SoulWeaponMenu(I18n.format("menu.adventureitems.attributes"), instance.getWeaponName());
            }

            Minecraft.getMinecraft().displayGuiScreen(screen);
        }
    }
}
