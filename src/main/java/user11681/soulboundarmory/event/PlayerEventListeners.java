package user11681.soulboundarmory.event;

import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.block.Material;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.world.GameRules;
import user11681.anvil.event.Listener;
import user11681.anvilevents.event.block.BlockDropEvent;
import user11681.anvilevents.event.entity.player.BlockBreakSpeedEvent;
import user11681.anvilevents.event.entity.player.ItemPickupEvent;
import user11681.anvilevents.event.entity.player.PlayerConnectedEvent;
import user11681.anvilevents.event.entity.player.PlayerCopyEvent;
import user11681.anvilevents.event.entity.player.PlayerDropInventoryEvent;
import user11681.anvilevents.event.entity.player.PlayerTickEvent;
import user11681.anvilevents.event.entity.player.UseBlockEvent;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.item.tool.ToolStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.WeaponStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.component.soulbound.player.SoulboundItemUtil;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.item.ModItems;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.item.SoulboundWeaponItem;
import user11681.soulboundarmory.network.Packets;
import user11681.soulboundarmory.network.common.ExtendedPacketBuffer;

import static user11681.soulboundarmory.component.statistics.StatisticType.EFFICIENCY;
import static user11681.soulboundarmory.component.statistics.StatisticType.LEVEL;
import static user11681.soulboundarmory.skill.Skills.PULL;

public class PlayerEventListeners {
    @Listener
    public static void onPlayerLoggedIn(final PlayerConnectedEvent event) {
        Main.PACKET_REGISTRY.sendToPlayer(event.getPlayer(), Packets.S2C_CONFIG, new ExtendedPacketBuffer());
    }

    @Listener
    public static void onPlayerDrops(final PlayerDropInventoryEvent event) {
        final PlayerEntity player = event.getPlayer();

        if (!player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            for (final ComponentType<? extends SoulboundComponent> type : Components.SOULBOUND_COMPONENTS) {
                final ItemStorage<?> storage = type.get(player).getStorage();

                if (storage != null && storage.getDatum(LEVEL) >= Configuration.instance().preservationLevel) {
                    for (final DefaultedList<ItemStack> inventory : event.getDrops()) {
                        for (int i = 0, inventorySize = inventory.size(); i < inventorySize; i++) {
                            final ItemStack itemStack = inventory.get(i);

                            if (storage.getBaseItemClass().isInstance(itemStack.getItem()) && SoulboundItemUtil.addItemStack(itemStack, player)) {
                                inventory.set(i, ItemStack.EMPTY);
                            }
                        }
                    }
                }
            }
        }
    }

    @Listener
    public static void onClone(final PlayerCopyEvent event) {
        final PlayerEntity original = event.getOldPlayer();
        final PlayerEntity player = event.getPlayer();

        for (final ComponentType<? extends SoulboundComponent> type : Components.SOULBOUND_COMPONENTS) {
            final SoulboundComponent originalComponent = type.get(original);
            final SoulboundComponent currentComponent = type.get(player);

            currentComponent.fromTag(originalComponent.toTag(new CompoundTag()));

            if (!player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
                final ItemStorage<?> storage = currentComponent.getStorage();

                if (storage != null && storage.getDatum(LEVEL) >= Configuration.instance().preservationLevel) {
                    player.giveItemStack(storage.getItemStack());
                }
            }
        }
    }

    public static void onRightClickBlock(final UseBlockEvent event) {
        final PlayerEntity player = event.getPlayer();
        final ItemStack mainStack = player.getMainHandStack();

        if (mainStack.getItem() instanceof SoulboundWeaponItem && mainStack != event.getItemStack()) {
            event.setFail();
        }
    }

    public static void onBreakSpeed(final BlockBreakSpeedEvent event) {
        final PlayerEntity player = event.getPlayer();

        if (player.getMainHandStack().getItem() instanceof SoulboundItem) {
            final Item item = player.getMainHandStack().getItem();
            final ItemStorage<?> storage = ItemStorage.get(player, item);

            if (storage instanceof ToolStorage) {
                if (item.isEffectiveOn(event.getState())) {
                    float newSpeed = (float) (event.getSpeed() + storage.getAttribute(EFFICIENCY));
                    final int efficiency = storage.getEnchantment(Enchantments.EFFICIENCY);
                    final int haste = StatusEffectUtil.getHasteAmplifier(player);

                    if (efficiency > 0) {
                        newSpeed += 1 + efficiency * efficiency;
                    }

                    if (haste != 0) {
                        newSpeed *= haste * 0.1;
                    }

                    if (item.canMine(event.getState(), player.world, event.getBlockPos(), player)) {
                        event.setSpeed(newSpeed);
                    } else {
                        event.setSpeed(newSpeed / 4F);
                    }
                } else {
                    event.setSpeed((float) ((event.getSpeed() - 1 + storage.getAttribute(EFFICIENCY)) / 8));
                }
            } else if (storage instanceof WeaponStorage) {
                final float newSpeed = (float) storage.getAttribute(EFFICIENCY);

                event.setSpeed(event.getState().getMaterial() == Material.COBWEB
                        ? Math.max(15, newSpeed)
                        : newSpeed
                );
            }
        }
    }

    @Listener
    public static void onBlockDrop(final BlockDropEvent event) {
        final Entity entity = event.getMiner();

        if (entity instanceof PlayerEntity && event.getTool().getItem() == ModItems.SOULBOUND_PICK && Components.TOOL_COMPONENT.get(entity).getStorage().hasSkill(PULL)) {
            event.setPos(entity.getBlockPos());
        }
    }

    @Listener(priority = 20)
    public static void onEntityItemPickup(final ItemPickupEvent event) {
        event.setFail();

        SoulboundItemUtil.addItemStack(event.getItemEntity().getStack(), event.getPlayer());
    }

    public static void onPlayerTick(final PlayerTickEvent.Post event) {
        final PlayerEntity player = event.getPlayer();

        for (final ComponentType<? extends SoulboundComponent> type : Components.SOULBOUND_COMPONENTS) {
            type.get(player).tick();
        }
    }
}
