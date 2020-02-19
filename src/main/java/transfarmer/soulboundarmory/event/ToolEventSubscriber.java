package transfarmer.soulboundarmory.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.tool.SoulToolHelper;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.data.IType;
import transfarmer.soulboundarmory.data.tool.SoulToolEnchantment;
import transfarmer.soulboundarmory.data.tool.SoulToolType;
import transfarmer.soulboundarmory.item.IItemSoulTool;

import static net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static transfarmer.soulboundarmory.data.tool.SoulToolAttribute.EFFICIENCY_ATTRIBUTE;

@EventBusSubscriber(modid = Main.MOD_ID)
public class ToolEventSubscriber {
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

                            continue;
                        }

                        if (!event.player.isCreative() && index != firstSlot) {
                            inventory.deleteStack(itemStack);
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
        if (!event.getEntityPlayer().world.isRemote) {
            event.setResult(ALLOW);

            SoulItemHelper.addItemStack(event.getItem().getItem(), event.getEntityPlayer());
        }
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
            final IItemSoulTool item = (IItemSoulTool) event.getEntityPlayer().getHeldItemMainhand().getItem();
            final IType type = capability.getCurrentType();

            if (item.isEffectiveAgainst(event.getState())) {
                float newSpeed = event.getOriginalSpeed() + capability.getAttribute(EFFICIENCY_ATTRIBUTE, type);
                final int efficiency = capability.getEnchantment(SoulToolEnchantment.SOUL_EFFICIENCY_ENCHANTMENT, type);

                if (efficiency > 0) {
                    newSpeed += 1 + efficiency * efficiency;
                }

                if (item.canHarvestBlock(event.getState(), event.getEntityPlayer())) {
                    event.setNewSpeed(newSpeed);
                } else {
                    event.setNewSpeed(newSpeed / 2.5F);
                }
            } else {
                event.setNewSpeed((event.getOriginalSpeed() - 1 + capability.getAttribute(EFFICIENCY_ATTRIBUTE, type)) / 4);
            }
        }
    }
}
