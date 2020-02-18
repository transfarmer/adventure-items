package transfarmer.soulboundarmory.event;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.Configuration;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolHelper;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.client.gui.SoulToolTooltipXPBar;
import transfarmer.soulboundarmory.data.tool.SoulToolType;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.network.tool.client.CToolData;

import java.util.List;

import static net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.data.tool.SoulToolAttribute.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soulboundarmory.data.tool.SoulToolDatum.LEVEL;

@EventBusSubscriber(modid = Main.MOD_ID)
public class ToolEventSubscriber {
    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        updatePlayer(event.player);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
        updatePlayer(event.player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        updatePlayer(event.player);

        final ISoulTool capability = SoulToolProvider.get(event.player);

        if (capability.getDatum(LEVEL, capability.getCurrentType()) >= Configuration.preservationLevel
                && !event.player.world.getGameRules().getBoolean("keepInventory")) {
            event.player.addItemStackToInventory(capability.getItemStack(capability.getCurrentType()));
        }
    }

    private static void updatePlayer(final EntityPlayer player) {
        final ISoulTool capability = SoulToolProvider.get(player);

        Main.CHANNEL.sendTo(new CToolData(
                capability.getCurrentType(),
                capability.getCurrentTab(),
                capability.getBoundSlot(),
                capability.getData(),
                capability.getAttributes(),
                capability.getEnchantments()), (EntityPlayerMP) player
        );
    }

    @SubscribeEvent
    public static void onClone(final Clone event) {
        final ISoulTool originalInstance = SoulToolProvider.get(event.getOriginal());
        final ISoulTool instance = SoulToolProvider.get(event.getEntityPlayer());

        instance.setCurrentType(originalInstance.getCurrentType());
        instance.setCurrentTab(originalInstance.getCurrentTab());
        instance.bindSlot(originalInstance.getBoundSlot());
        instance.setStatistics(originalInstance.getData(), originalInstance.getAttributes(), originalInstance.getEnchantments());
    }

    @SubscribeEvent
    public static void onPlayerDrops(final PlayerDropsEvent event) {
        final EntityPlayer player = event.getEntityPlayer();
        final ISoulTool capability = SoulToolProvider.get(player);

        if (capability.getDatum(LEVEL, capability.getCurrentType()) >= Configuration.preservationLevel
                && !player.world.getGameRules().getBoolean("keepInventory")) {

            event.getDrops().removeIf((EntityItem item) -> SoulToolHelper.isSoulTool(item.getItem()));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent event) {
        if (event.phase != END) return;

        if (SoulToolHelper.hasSoulTool(event.player)) {
            ISoulTool instance = SoulToolProvider.get(event.player);
            InventoryPlayer inventory = event.player.inventory;

            if (SoulToolHelper.isSoulToolEquipped(event.player)) {
                final SoulToolType type = SoulToolType.getType(inventory.getCurrentItem().getItem());

                if (type != instance.getCurrentType()) {
                    instance.setCurrentType(type);
                }
            }

            if (instance.getCurrentType() != null) {
                int firstSlot = -1;

                for (final ItemStack itemStack : inventory.mainInventory) {
                    if (SoulToolHelper.isSoulTool(itemStack)) {
                        final int index = inventory.mainInventory.indexOf(itemStack);

                        if (itemStack.getItem() == instance.getCurrentType().getItem() && firstSlot == -1) {
                            firstSlot = index;

                            if (instance.getBoundSlot() != -1) {
                                instance.bindSlot(index);
                            }
                        }
                    }
                }
            }

            for (final ItemStack itemStack : inventory.mainInventory) {
                if (!SoulToolHelper.isSoulTool(itemStack)) continue;

                final ItemStack newItemStack = instance.getItemStack(itemStack);

                if (!SoulToolHelper.areDataEqual(itemStack, newItemStack)) {
                    if (itemStack.hasDisplayName()) {
                        newItemStack.setStackDisplayName(itemStack.getDisplayName());
                    }

                    inventory.setInventorySlotContents(inventory.mainInventory.indexOf(itemStack), newItemStack);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityItemPickup(final EntityItemPickupEvent event) {
        event.setResult(ALLOW);

        SoulToolHelper.addItemStack(event.getItem().getItem(), event.getEntityPlayer());
    }

    @SubscribeEvent
    public static void onHarvestDrops(final HarvestDropsEvent event) {
        if (event.getHarvester() != null) {
            final EntityPlayer player = event.getHarvester();
            final Item item = player.getHeldItemMainhand().getItem();

            if (item instanceof IItemSoulTool) {
                if (((IItemSoulTool) item).canHarvestBlock(event.getState(), player)) {
                    event.setDropChance(1);
                } else if (((IItemSoulTool) item).isEffectiveAgainst(event.getState())) {
                    event.setDropChance(0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(final BreakSpeed event) {
        if (event.getEntityPlayer().getHeldItemMainhand().getItem() instanceof IItemSoulTool) {
            final ISoulTool capability = SoulToolProvider.get(event.getEntityPlayer());
            if (((IItemSoulTool) event.getEntityPlayer().getHeldItemMainhand().getItem()).isEffectiveAgainst(event.getState())) {
                event.setNewSpeed(event.getOriginalSpeed() + capability.getAttribute(EFFICIENCY_ATTRIBUTE, capability.getCurrentType()));
            } else {
                event.setNewSpeed(event.getOriginalSpeed() - 1 + capability.getAttribute(EFFICIENCY_ATTRIBUTE, capability.getCurrentType()) / 2);
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onItemTooltip(final ItemTooltipEvent event) {
        final EntityPlayer player = event.getEntityPlayer();

        if (player != null) {
            final ISoulTool instance = SoulToolProvider.get(player);

            if (event.getItemStack().getItem() instanceof IItemSoulTool) {
                final SoulToolType weaponType = SoulToolType.getType(event.getItemStack());
                final List<String> tooltip = event.getToolTip();
                final String[] newTooltip = instance.getTooltip(weaponType);
                final int enchantments = event.getItemStack().getEnchantmentTagList().tagCount();

                tooltip.remove(5 + enchantments);
                tooltip.remove(4 + enchantments);
                tooltip.remove(3 + enchantments);

                for (int i = 0; i < newTooltip.length; i++) {
                    tooltip.add(3 + enchantments + i, newTooltip[i]);
                }
            }
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onRenderTooltip(final RenderTooltipEvent.PostText event) {
        final SoulToolType tooltipTool = SoulToolType.getType(event.getStack());

        if (tooltipTool != null) {
            new SoulToolTooltipXPBar(tooltipTool, event.getX(), event.getY(), event.getStack().getEnchantmentTagList().tagCount());
        }
    }
}
