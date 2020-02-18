package transfarmer.soulboundarmory.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.tool.SoulToolHelper;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponHelper;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.client.gui.SoulToolMenu;
import transfarmer.soulboundarmory.client.gui.SoulWeaponMenu;
import transfarmer.util.ItemHelper;

import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.ResourceLocations.SOULBOUND_TOOL;
import static transfarmer.soulboundarmory.ResourceLocations.SOULBOUND_WEAPON;
import static transfarmer.soulboundarmory.client.KeyBindings.MENU_KEY;

@EventBusSubscriber(modid = Main.MOD_ID)
public class CommonEventSubscriber {
    @SubscribeEvent
    public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(SOULBOUND_WEAPON, new SoulWeaponProvider());
            event.addCapability(SOULBOUND_TOOL, new SoulToolProvider());
        }
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
                    minecraft.displayGuiScreen(new SoulWeaponMenu());
                } else if (ItemHelper.isItemEquipped(Items.WOODEN_PICKAXE, player)){
                    minecraft.displayGuiScreen(new SoulToolMenu());
                }
            }
        }
    }

}
