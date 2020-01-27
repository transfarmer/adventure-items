package transfarmer.soulweapons.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.SoulAttributeModifier;
import transfarmer.soulweapons.SoulWeaponType;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.capability.SoulWeaponProvider;
import transfarmer.soulweapons.gui.SoulWeaponMenu;
import transfarmer.soulweapons.network.ClientWeaponData;

import java.util.List;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import static net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.KeyBindings.WEAPON_MENU;
import static transfarmer.soulweapons.SoulWeaponType.NONE;
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
            InventoryPlayer inventory = event.player.inventory;

            if (ISoulWeapon.isSoulWeaponEquipped(event.player)) {
                final SoulWeaponType heldItemType = SoulWeaponType.getType(inventory.getCurrentItem().getItem());

                if (heldItemType != instance.getCurrentType()) {
                    instance.setCurrentType(heldItemType);
                }
            }

            if (instance.getCurrentType() != NONE) {
                if (!event.player.isCreative()) {
                    for (final Item item : SoulWeaponType.getItems()) {
                        if (item != instance.getItem()) {
                            inventory.clearMatchingItems(item, 0, inventory.getSizeInventory(), null);
                        }
                    }
                }


                for (final ItemStack itemStack : inventory.mainInventory) {
                    // Main.LOGGER.info("is creative " + event.player.isCreative());
                    // Main.LOGGER.warn("is soul weapon " + SoulWeaponType.isSoulWeapon(itemStack));
                    // Main.LOGGER.error("item equals instance item " + itemStack.getItem().equals(instance.getItem()));
                    // Main.LOGGER.error("are attributes equal " + itemStack.getAttributeModifiers(MAINHAND).equals(newItemStack.getAttributeModifiers(MAINHAND)));
                    // Main.LOGGER.error(itemStack.getItem());
                    // Main.LOGGER.error(instance.getItem());
                    if (SoulWeaponType.isSoulWeapon(itemStack)) {
                        ItemStack newItemStack = instance.getItemStack(itemStack);

                        itemStack.getAttributeModifiers(MAINHAND).forEach((key, value) -> {
                            newItemStack.getAttributeModifiers(MAINHAND).forEach((key1, value1) -> {
                            });
                        });

                        if (!SoulAttributeModifier.areAttributesEqual(itemStack, newItemStack, MAINHAND)) {
                            inventory.setInventorySlotContents(inventory.getSlotFor(itemStack), newItemStack);
                        }
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

            if (ISoulWeapon.isSoulWeaponEquipped(player) && instance.getCurrentType() != NONE) {
                Minecraft.getMinecraft().displayGuiScreen(new SoulWeaponMenu(I18n.format("menu.soulweapons.attributes")));
            } else if (player.getHeldItemMainhand().getItem().equals(Items.WOODEN_SWORD)
                || (ISoulWeapon.isSoulWeaponEquipped(player) && instance.getCurrentType() == NONE)) {
                Minecraft.getMinecraft().displayGuiScreen(new SoulWeaponMenu(I18n.format("menu.soulweapons.weapons")));
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        EntityPlayer player = event.getEntityPlayer();

        if (player == null) return;

        if (SoulWeaponType.getItems().contains(event.getItemStack().getItem())) {
            List<String> tooltip = event.getToolTip();
            String[] newTooltip = player.getCapability(CAPABILITY, null).getTooltip(event.getItemStack());
            tooltip.remove(4);
            tooltip.remove(3);

            for (int i = 0; i < newTooltip.length; i++) {
                tooltip.add(3 + i, newTooltip[i]);
            }
        }
    }
}
