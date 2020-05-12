package transfarmer.soulboundarmory.event;

import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntityMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.component.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.component.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.component.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.ItemSoulbound;
import transfarmer.soulboundarmory.item.ItemSoulboundMeleeWeapon;
import transfarmer.soulboundarmory.item.SoulboundPickItem;
import transfarmer.soulboundarmory.item.SoulboundTool;
import transfarmer.soulboundarmory.item.SoulboundWeapon;
import transfarmer.soulboundarmory.network.S2C.S2CConfig;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import static net.minecraft.init.Enchantments.EFFICIENCY;
import static net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW;
import static net.minecraftforge.fml.common.eventhandler.Event.Result.DENY;
import static net.minecraftforge.fml.common.eventhandler.EventPriority.HIGHEST;
import static net.minecraftforge.fml.common.eventhandler.EventPriority.LOW;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static transfarmer.soulboundarmory.init.Skills.TELEPORTATION;
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.PICK;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.EFFICIENCY_ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;

@EventBusSubscriber(modid = Main.MOD_ID)
public class PlayerEventListeners {
    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerLoggedInEvent event) {
        updatePlayer(event.player);

        Main.CHANNEL.sendTo(new S2CConfig(), (PlayerEntityMP) event.player);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(final PlayerChangedDimensionEvent event) {
        updatePlayer(event.player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(final PlayerRespawnEvent event) {
        updatePlayer(event.player);
    }

    @SubscribeEvent(priority = HIGHEST)
    public static void onPlayerDrops(final PlayerDropsEvent event) {
        final PlayerEntity player = event.getPlayerEntity();

        if (!(player instanceof FakePlayer) && !player.world.getGameRules().getBoolean("keepInventory")) {
            ISoulboundComponent weapons = WeaponProvider.get(player);
            IItem type = weapons.getItemType();

            if (type != null && weapons.getDatum(type, LEVEL) >= MainConfig.instance().getPreservationLevel()) {
                event.getDrops().removeIf((final EntityItem item) -> item.getItem().getItem() instanceof SoulboundWeapon && SoulboundItemUtil.addItemStack(item.getItem(), player));
            }

            weapons = ToolProvider.get(player);
            type = weapons.getItemType();

            if (type != null && weapons.getDatum(type, LEVEL) >= MainConfig.instance().getPreservationLevel()) {
                event.getDrops().removeIf((final EntityItem item) -> item.getItem().getItem() instanceof SoulboundTool && SoulboundItemUtil.addItemStack(item.getItem(), player));
            }
        }
    }

    @SubscribeEvent(priority = HIGHEST)
    public static void onClone(final Clone event) {
        final PlayerEntity original = event.getOriginal();
        final PlayerEntity player = event.getPlayerEntity();

        final ISoulboundComponent originalTools = ToolProvider.get(original);
        final ISoulboundComponent originalWeapons = WeaponProvider.get(original);
        final ISoulboundComponent newTools = ToolProvider.get(player);
        final ISoulboundComponent newWeapons = WeaponProvider.get(player);

        newWeapons.deserializeNBT(originalWeapons.serializeNBT());
        newTools.deserializeNBT(originalTools.serializeNBT());

        IItem type = newWeapons.getItemType();

        if (!player.world.getGameRules().getBoolean("keepInventory")) {
            if (type != null && newWeapons.getDatum(type, LEVEL) >= MainConfig.instance().getPreservationLevel()) {
                player.addItemStackToInventory(newWeapons.getItemStack(type));
            }

            if ((type = newTools.getItemType()) != null && newTools.getDatum(type, LEVEL) >= MainConfig.instance().getPreservationLevel()) {
                player.addItemStackToInventory(newTools.getItemStack(type));
            }
        }
    }

    private static void updatePlayer(final PlayerEntity player) {
        final ISoulboundComponent weapons = WeaponProvider.get(player);
        final ISoulboundComponent tools = ToolProvider.get(player);

        weapons.initPlayer(player);
        tools.initPlayer(player);

        weapons.sync();
        tools.sync();
    }

    @SubscribeEvent
    public static void onRightClickBlock(final RightClickBlock event) {
        final PlayerEntity player = event.getPlayerEntity();
        final ItemStack stackMainhand = player.getMainHandStack();

        if (stackMainhand.getItem() instanceof SoulboundWeapon && stackMainhand != event.getItemStack()) {
            event.setUseItem(DENY);
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(final BreakSpeed event) {
        if (event.getPlayerEntity().getMainHandStack().getItem() instanceof ItemSoulbound) {
            final ItemSoulbound item = (ItemSoulbound) event.getPlayerEntity().getMainHandStack().getItem();
            final ISoulboundComponent capability = SoulboundItemUtil.getFirstCapability(event.getPlayerEntity(), (Item) item);
            final IItem type = capability.getItemType();

            if (item instanceof SoulboundTool) {
                if (((SoulboundTool) item).isEffectiveAgainst(event.getState())) {
                    float newSpeed = (float) (event.getOriginalSpeed() + capability.getAttribute(type, EFFICIENCY_ATTRIBUTE));
                    final int efficiency = capability.getEnchantment(type, EFFICIENCY);
                    //noinspection ConstantConditions
                    final PotionEffect haste = event.getPlayerEntity().getActivePotionEffect(Potion.getPotionFromIdentifier("haste"));

                    if (efficiency > 0) {
                        newSpeed += 1 + efficiency * efficiency;
                    }

                    if (haste != null) {
                        newSpeed *= haste.getAmplifier() * 0.1;
                    }

                    if (((SoulboundTool) item).canHarvestBlock(event.getState(), event.getPlayerEntity())) {
                        event.setNewSpeed(newSpeed);
                    } else {
                        event.setNewSpeed(newSpeed / 4F);
                    }
                } else {
                    event.setNewSpeed((float) ((event.getOriginalSpeed() - 1 + capability.getAttribute(type, EFFICIENCY_ATTRIBUTE)) / 8));
                }
            } else if (item instanceof ItemSoulboundMeleeWeapon) {
                final float newSpeed = (float) capability.getAttribute(capability.getItemType(), EFFICIENCY_ATTRIBUTE);

                event.setNewSpeed(event.getState().getMaterial() == Material.WEB
                        ? Math.max(15, newSpeed)
                        : newSpeed
                );
            }
        }
    }

    @SubscribeEvent
    public static void onHarvestDrops(final HarvestDropsEvent event) {
        final PlayerEntity player = event.getHarvester();

        if (player != null && player.getMainHandStack().getItem() instanceof SoulboundPickItem && ToolProvider.get(player).hasSkill(PICK, TELEPORTATION)) {
            event.setDropChance(0);

            for (final ItemStack drop : event.getDrops()) {
                if (!event.getWorld().isRemote && !drop.isEmpty() && event.getWorld().getGameRules().getBoolean("doTileDrops") && !event.getWorld().restoringBlockSnapshots) {
                    final Vec3d pos = player.getPositionVector();

                    event.getWorld().spawnEntity(new EntityItem(event.getWorld(), pos.x, pos.y, pos.z, drop));
                }
            }
        }
    }

    @SubscribeEvent(priority = LOW)
    public static void onEntityItemPickup(final EntityItemPickupEvent event) {
        event.setResult(ALLOW);

        SoulboundItemUtil.addItemStack(event.getItem().getItem(), event.getPlayerEntity());
    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent event) {
        if (event.phase == END) {
            final PlayerEntity player = event.player;

            ToolProvider.get(player).onTick();
            WeaponProvider.get(player).onTick();
        }
    }

//    @Environment(CLIENT)
//    @SubscribeEvent
//    public static void onLeftClickBlock(final LeftClickBlock event) {
//        final PlayerEntity player = event.getPlayerEntity();
//        final ISoulCapability capability = SoulItemHelper.getCapability(player, player.getOffHandStack());
//        final Minecraft minecraft = CLIENT;
//
//        if (capability instanceof SoulTool && capability.getDatum(SKILLS, capability.getCurrentType()) >= 1) {
//            final BlockPos blockPos = minecraft.objectMouseOver.getBlockPos();
//
//            if (!minecraft.world.isAirBlock(blockPos)) {
//                ModPlayerControllerMP.modController.clickBlockOffhand(blockPos, event.getFace());
//            }
//        }
//    }
}
