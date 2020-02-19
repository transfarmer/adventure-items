package transfarmer.soulboundarmory.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.Configuration;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolHelper;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponHelper;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.client.gui.SoulToolMenu;
import transfarmer.soulboundarmory.client.gui.SoulToolTooltipXPBar;
import transfarmer.soulboundarmory.client.gui.SoulWeaponMenu;
import transfarmer.soulboundarmory.client.gui.SoulWeaponTooltipXPBar;
import transfarmer.soulboundarmory.data.IType;
import transfarmer.soulboundarmory.data.tool.SoulToolDatum;
import transfarmer.soulboundarmory.data.tool.SoulToolType;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponDatum;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponType;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;
import transfarmer.soulboundarmory.network.client.tool.CToolData;
import transfarmer.soulboundarmory.network.client.weapon.CWeaponData;
import transfarmer.util.ItemHelper;

import java.util.List;

import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.ResourceLocations.SOULBOUND_TOOL;
import static transfarmer.soulboundarmory.ResourceLocations.SOULBOUND_WEAPON;
import static transfarmer.soulboundarmory.client.KeyBindings.MENU_KEY;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponType.GREATSWORD;
import static transfarmer.soulboundarmory.data.weapon.SoulWeaponType.SWORD;

@EventBusSubscriber(modid = Main.MOD_ID)
public class CommonEventSubscriber {
    @SubscribeEvent
    public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(SOULBOUND_WEAPON, new SoulWeaponProvider());
            event.addCapability(SOULBOUND_TOOL, new SoulToolProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerLoggedInEvent event) {
        updatePlayer(event.player);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(final PlayerChangedDimensionEvent event) {
        updatePlayer(event.player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(final PlayerRespawnEvent event) {
        updatePlayer(event.player);

        ISoulCapability capability = SoulWeaponProvider.get(event.player);
        IType type = capability.getCurrentType();

        if (!event.player.world.getGameRules().getBoolean("keepInventory")) {
            if (type != null && capability != null && capability.getDatum(SoulWeaponDatum.LEVEL, type) >= Configuration.preservationLevel) {
                event.player.addItemStackToInventory(capability.getItemStack(type));
            }

            capability = SoulToolProvider.get(event.player);
            type = capability.getCurrentType();

            if (type != null && capability != null && capability.getDatum(SoulToolDatum.LEVEL, type) >= Configuration.preservationLevel) {
                event.player.addItemStackToInventory(capability.getItemStack(type));
            }
        }
    }

    private static void updatePlayer(final EntityPlayer player) {
        if (player != null) {
            final ISoulWeapon weaponCapability = SoulWeaponProvider.get(player);
            final ISoulTool toolCapability = SoulToolProvider.get(player);

            if (weaponCapability == null || toolCapability == null) {
                throw new NullPointerException(String.format("weaponCapability: %s\ntoolCapability: %s", weaponCapability, toolCapability));
            }

            Main.CHANNEL.sendTo(new CWeaponData(player,
                    weaponCapability.getCurrentType(),
                    weaponCapability.getCurrentTab(),
                    weaponCapability.getAttackCooldown(),
                    weaponCapability.getBoundSlot(),
                    weaponCapability.getData(),
                    weaponCapability.getAttributes(),
                    weaponCapability.getEnchantments()), (EntityPlayerMP) player
            );
            Main.CHANNEL.sendTo(new CToolData(player,
                    toolCapability.getCurrentType(),
                    toolCapability.getCurrentTab(),
                    toolCapability.getBoundSlot(),
                    toolCapability.getData(),
                    toolCapability.getAttributes(),
                    toolCapability.getEnchantments()), (EntityPlayerMP) player
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerDrops(final PlayerDropsEvent event) {
        final EntityPlayer player = event.getEntityPlayer();

        if (!player.world.getGameRules().getBoolean("keepInventory")) {
            ISoulCapability capability = SoulWeaponProvider.get(player);
            IType type = capability.getCurrentType();

            if (type != null && capability.getDatum(SoulWeaponDatum.LEVEL, type) >= Configuration.preservationLevel) {
                event.getDrops().removeIf((final EntityItem item) -> SoulWeaponHelper.isSoulWeapon(item.getItem()));
            }

            capability = SoulToolProvider.get(player);
            type = capability.getCurrentType();

            if (type != null && capability.getDatum(SoulToolDatum.LEVEL, type) >= Configuration.preservationLevel) {
                event.getDrops().removeIf((final EntityItem item) -> SoulToolHelper.isSoulTool(item.getItem()));
            }
        }
    }

    @SubscribeEvent
    public static void onClone(final Clone event) {
        ISoulCapability originalInstance = SoulWeaponProvider.get(event.getOriginal());
        ISoulCapability instance = SoulWeaponProvider.get(event.getEntityPlayer());

        instance.setCurrentType(originalInstance.getCurrentType());
        instance.setCurrentTab(originalInstance.getCurrentTab());
        instance.bindSlot(originalInstance.getBoundSlot());
        instance.setStatistics(originalInstance.getData(), originalInstance.getAttributes(), originalInstance.getEnchantments());

        originalInstance = SoulToolProvider.get(event.getOriginal());
        instance = SoulToolProvider.get(event.getEntityPlayer());

        instance.setCurrentType(originalInstance.getCurrentType());
        instance.setCurrentTab(originalInstance.getCurrentTab());
        instance.bindSlot(originalInstance.getBoundSlot());
        instance.setStatistics(originalInstance.getData(), originalInstance.getAttributes(), originalInstance.getEnchantments());
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent event) {
        if (event.phase == END) {
            final Minecraft minecraft = Minecraft.getMinecraft();

            if (MENU_KEY.isPressed()) {
                final EntityPlayer player = minecraft.player;

                if (SoulWeaponHelper.isSoulWeaponEquipped(player)) {
                    minecraft.displayGuiScreen(new SoulWeaponMenu());
                } else if (SoulToolHelper.isSoulToolEquipped(player)) {
                    minecraft.displayGuiScreen(new SoulToolMenu());
                } else if (ItemHelper.isItemEquipped(Items.WOODEN_SWORD, player)) {
                    minecraft.displayGuiScreen(new SoulWeaponMenu(-1));
                } else if (ItemHelper.isItemEquipped(Items.WOODEN_PICKAXE, player)){
                    minecraft.displayGuiScreen(new SoulToolMenu(-1));
                }
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onItemTooltip(final ItemTooltipEvent event) {
        final EntityPlayer player = event.getEntityPlayer();

        if (player != null) {
            try {
                if (event.getItemStack().getItem() instanceof ItemSoulWeapon) {
                    final ISoulWeapon capability = SoulWeaponProvider.get(player);

                    final SoulWeaponType weaponType = SoulWeaponType.getType(event.getItemStack());
                    final List<String> tooltip = event.getToolTip();
                    final String[] newTooltip = capability.getTooltip(weaponType);
                    final int enchantments = event.getItemStack().getEnchantmentTagList().tagCount();

                    if (weaponType == GREATSWORD || weaponType == SWORD) {
                        tooltip.remove(5 + enchantments);
                    }

                    tooltip.remove(4 + enchantments);
                    tooltip.remove(3 + enchantments);

                    for (int i = 0; i < newTooltip.length; i++) {
                        tooltip.add(3 + enchantments + i, newTooltip[i]);
                    }
                } else if (event.getItemStack().getItem() instanceof IItemSoulTool) {
                    final ISoulTool capability = SoulToolProvider.get(player);

                    if (event.getItemStack().getItem() instanceof IItemSoulTool) {
                        final SoulToolType weaponType = SoulToolType.getType(event.getItemStack());
                        final List<String> tooltip = event.getToolTip();
                        final String[] newTooltip = capability.getTooltip(weaponType);
                        final int enchantments = event.getItemStack().getEnchantmentTagList().tagCount();

                        tooltip.remove(5 + enchantments);
                        tooltip.remove(4 + enchantments);
                        tooltip.remove(3 + enchantments);

                        for (int i = 0; i < newTooltip.length; i++) {
                            tooltip.add(3 + enchantments + i, newTooltip[i]);
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException exception) {
                exception.printStackTrace();
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onRenderTooltip(final RenderTooltipEvent.PostText event) {
        final SoulWeaponType tooltipWeapon = SoulWeaponType.getType(event.getStack());

        if (tooltipWeapon != null) {
            new SoulWeaponTooltipXPBar(tooltipWeapon, event.getX(), event.getY(), event.getStack().getEnchantmentTagList().tagCount());
        } else {
            final SoulToolType tooltipTool = SoulToolType.getType(event.getStack());

            if (tooltipTool != null) {
                new SoulToolTooltipXPBar(tooltipTool, event.getX(), event.getY(), event.getStack().getEnchantmentTagList().tagCount());
            }
        }
    }
}
