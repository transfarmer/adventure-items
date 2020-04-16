package transfarmer.soulboundarmory.event;

import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
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
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.ISoulCapability;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.capability.soulbound.tool.SoulToolProvider;
import transfarmer.soulboundarmory.capability.soulbound.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.item.ISoulItem;
import transfarmer.soulboundarmory.item.ItemSoulPick;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;
import transfarmer.soulboundarmory.network.client.S2CConfig;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolEnchantment;

import static net.minecraftforge.fml.common.eventhandler.Event.Result.DENY;
import static transfarmer.soulboundarmory.statistics.SoulDatum.DATA;
import static transfarmer.soulboundarmory.statistics.SoulDatum.SoulToolDatum.TOOL_DATA;
import static transfarmer.soulboundarmory.statistics.SoulType.PICK;
import static transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute.EFFICIENCY_ATTRIBUTE;

@EventBusSubscriber(modid = Main.MOD_ID)
public class PlayerEventSubscriber {
    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerLoggedInEvent event) {
        updatePlayer(event.player);

        Main.CHANNEL.sendTo(new S2CConfig(), (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(final PlayerChangedDimensionEvent event) {
        updatePlayer(event.player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(final PlayerRespawnEvent event) {
        updatePlayer(event.player);

        ISoulCapability capability = SoulWeaponProvider.get(event.player);
        SoulType type = capability.getCurrentType();

        if (!event.player.world.getGameRules().getBoolean("keepInventory")) {
            if (type != null && capability.getDatum(DATA.level, type) >= MainConfig.instance().getPreservationLevel()) {
                event.player.addItemStackToInventory(capability.getItemStack(type));
            }

            capability = SoulToolProvider.get(event.player);
            type = capability.getCurrentType();

            if (type != null && capability.getDatum(DATA.level, type) >= MainConfig.instance().getPreservationLevel()) {
                event.player.addItemStackToInventory(capability.getItemStack(type));
            }
        }
    }

    private static void updatePlayer(final EntityPlayer player) {
        final ISoulCapability weaponCapability = SoulWeaponProvider.get(player);
        final ISoulCapability toolCapability = SoulToolProvider.get(player);

        weaponCapability.initPlayer(player);
        toolCapability.initPlayer(player);

        weaponCapability.sync();
        toolCapability.sync();
    }

    @SubscribeEvent
    public static void onPlayerDrops(final PlayerDropsEvent event) {
        final EntityPlayer player = event.getEntityPlayer();

        if (!player.world.getGameRules().getBoolean("keepInventory")) {
            ISoulCapability capability = SoulWeaponProvider.get(player);
            SoulType type = capability.getCurrentType();

            if (type != null && capability.getDatum(DATA.level, type) >= MainConfig.instance().getPreservationLevel()) {
                event.getDrops().removeIf((final EntityItem item) -> item.getItem().getItem() instanceof ItemSoulWeapon);
            }

            capability = SoulToolProvider.get(player);
            type = capability.getCurrentType();

            if (type != null && capability.getDatum(DATA.level, type) >= MainConfig.instance().getPreservationLevel()) {
                event.getDrops().removeIf((final EntityItem item) -> item.getItem().getItem() instanceof IItemSoulTool);
            }
        }
    }

    @SubscribeEvent
    public static void onClone(final Clone event) {
        final ISoulCapability originalToolInstance = SoulToolProvider.get(event.getOriginal());
        final ISoulCapability originalWeaponInstance = SoulWeaponProvider.get(event.getOriginal());
        final ISoulCapability toolInstance = SoulToolProvider.get(event.getEntityPlayer());
        final ISoulCapability weaponInstance = SoulWeaponProvider.get(event.getEntityPlayer());

        weaponInstance.readFromNBT(originalWeaponInstance.writeToNBT());
        toolInstance.readFromNBT(originalToolInstance.writeToNBT());
    }

    @SubscribeEvent
    public static void onRightClickBlock(final RightClickBlock event) {
        final EntityPlayer player = event.getEntityPlayer();
        final ItemStack stackMainhand = player.getHeldItemMainhand();

        if (stackMainhand.getItem() instanceof ItemSoulWeapon && stackMainhand != event.getItemStack()) {
            event.setUseItem(DENY);
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(final BreakSpeed event) {
        if (event.getEntityPlayer().getHeldItemMainhand().getItem() instanceof ISoulItem) {
            final ISoulItem item = (ISoulItem) event.getEntityPlayer().getHeldItemMainhand().getItem();
            final ISoulCapability capability = SoulItemHelper.getCapability(event.getEntityPlayer(), (Item) item);
            final SoulType type = capability.getCurrentType();

            if (item instanceof IItemSoulTool) {
                if (((IItemSoulTool) item).isEffectiveAgainst(event.getState())) {
                    float newSpeed = event.getOriginalSpeed() + capability.getAttribute(EFFICIENCY_ATTRIBUTE, type);
                    final int efficiency = capability.getEnchantment(SoulToolEnchantment.SOUL_EFFICIENCY, type);
                    @SuppressWarnings("ConstantConditions") final PotionEffect haste = event.getEntityPlayer().getActivePotionEffect(Potion.getPotionFromResourceLocation("haste"));

                    if (efficiency > 0) {
                        newSpeed += 1 + efficiency * efficiency;
                    }

                    if (haste != null) {
                        newSpeed *= haste.getAmplifier() * 0.1;
                    }

                    if (((IItemSoulTool) item).canHarvestBlock(event.getState(), event.getEntityPlayer())) {
                        event.setNewSpeed(newSpeed);
                    } else {
                        event.setNewSpeed(newSpeed / 4F);
                    }
                } else {
                    event.setNewSpeed((event.getOriginalSpeed() - 1 + capability.getAttribute(EFFICIENCY_ATTRIBUTE, type)) / 8);
                }
            } else if (item instanceof ItemSoulWeapon) {
                final float newSpeed = capability.getAttribute(EFFICIENCY_ATTRIBUTE, capability.getCurrentType());

                event.setNewSpeed(event.getState().getMaterial() == Material.WEB
                        ? Math.max(15, newSpeed)
                        : newSpeed
                );
            }
        }
    }

    @SubscribeEvent
    public static void onHarvestDrops(final HarvestDropsEvent event) {
        final EntityPlayer player = event.getHarvester();

        if (player != null && player.getHeldItemMainhand().getItem() instanceof ItemSoulPick && SoulToolProvider.get(player).getDatum(TOOL_DATA.skills, PICK) >= 1) {
            event.setDropChance(0);

            for (final ItemStack drop : event.getDrops()) {
                if (!event.getWorld().isRemote && !drop.isEmpty() && event.getWorld().getGameRules().getBoolean("doTileDrops") && !event.getWorld().restoringBlockSnapshots) {
                    final Vec3d pos = player.getPositionVector();

                    event.getWorld().spawnEntity(new EntityItem(event.getWorld(), pos.x, pos.y, pos.z, drop));
                }
            }
        }
    }
}
