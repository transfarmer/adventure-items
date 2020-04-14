package transfarmer.soulboundarmory.event;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;
import transfarmer.soulboundarmory.network.client.S2CConfig;
import transfarmer.soulboundarmory.statistics.SoulType;

import static transfarmer.soulboundarmory.statistics.SoulDatum.DATA;

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

        weaponInstance.readNBT(originalWeaponInstance.writeNBT());
        toolInstance.readNBT(originalToolInstance.writeNBT());
    }

}
