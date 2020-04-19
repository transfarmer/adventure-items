package transfarmer.soulboundarmory.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.frozen.FrozenProvider;
import transfarmer.soulboundarmory.capability.frozen.IFrozen;
import transfarmer.soulboundarmory.capability.soulbound.IItemCapability;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.client.gui.SoulToolMenu;
import transfarmer.soulboundarmory.client.gui.SoulWeaponMenu;
import transfarmer.soulboundarmory.item.IItemSoulTool;
import transfarmer.soulboundarmory.item.ItemSoulWeapon;

import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.client.KeyBindings.MENU_KEY;

@EventBusSubscriber(modid = Main.MOD_ID)
public class TickEventHandlers {
    @SubscribeEvent
    public static void onLivingUpdate(final LivingUpdateEvent event) {
        final IFrozen capability = FrozenProvider.get(event.getEntityLiving());

        if (capability != null) {
            event.setCanceled(capability.update());
        }

    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent event) {
        if (event.phase == END) {
            final IItemCapability weaponCapability = WeaponProvider.get(event.player);
            final IItemCapability toolCapability = ToolProvider.get(event.player);

            weaponCapability.onTick();
            toolCapability.onTick();
        }
    }

    @SideOnly(CLIENT)
    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent event) {
        if (event.phase == END) {
            final Minecraft minecraft = Minecraft.getMinecraft();

            if (MENU_KEY.isPressed()) {
                final EntityPlayer player = minecraft.player;

                if (player.getHeldItemMainhand().getItem() instanceof ItemSoulWeapon) {
                    minecraft.displayGuiScreen(new SoulWeaponMenu());
                } else if (player.getHeldItemMainhand().getItem() instanceof IItemSoulTool) {
                    minecraft.displayGuiScreen(new SoulToolMenu());
                } else if (player.getHeldItemMainhand().getItem() == Items.WOODEN_SWORD) {
                    minecraft.displayGuiScreen(new SoulWeaponMenu(0));
                } else if (player.getHeldItemMainhand().getItem() == Items.WOODEN_PICKAXE) {
                    minecraft.displayGuiScreen(new SoulToolMenu(-1));
                } else if (player.getHeldItemOffhand().getItem() instanceof ItemSoulWeapon) {
                    minecraft.displayGuiScreen(new SoulWeaponMenu());
                } else if (player.getHeldItemOffhand().getItem() instanceof IItemSoulTool) {
                    minecraft.displayGuiScreen(new SoulToolMenu());
                } else if (player.getHeldItemOffhand().getItem() == Items.WOODEN_SWORD) {
                    minecraft.displayGuiScreen(new SoulWeaponMenu(0));
                } else if (player.getHeldItemOffhand().getItem() == Items.WOODEN_PICKAXE) {
                    minecraft.displayGuiScreen(new SoulToolMenu(-1));
                }
            }
        }
    }
}
