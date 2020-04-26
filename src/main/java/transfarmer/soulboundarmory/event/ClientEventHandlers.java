package transfarmer.soulboundarmory.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulItemHelper;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.client.gui.GuiXPBar;
import transfarmer.soulboundarmory.config.ClientConfig;
import transfarmer.soulboundarmory.item.ISoulboundItem;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.EXPERIENCE;
import static net.minecraftforge.fml.common.eventhandler.EventPriority.LOW;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.client.KeyBindings.MENU_KEY;
import static transfarmer.soulboundarmory.client.KeyBindings.TOGGLE_XP_BAR_KEY;
import static transfarmer.soulboundarmory.client.gui.screen.common.GuiExtended.FONT_RENDERER;

@EventBusSubscriber(value = CLIENT, modid = Main.MOD_ID)
public class ClientEventHandlers {
    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent event) {
        if (event.phase == END) {
            if (MENU_KEY.isPressed()) {
                final EntityPlayer player = Minecraft.getMinecraft().player;
                final SoulboundCapability capability = SoulItemHelper.getFirstHeldCapability(player);

                if (capability != null) {
                    capability.refresh();
                }
            } else if (TOGGLE_XP_BAR_KEY.isPressed()) {
                ClientConfig.setOverlayXPBar(!ClientConfig.getOverlayXPBar());
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGameOverlay(final RenderGameOverlayEvent.Pre event) {
        if (ClientConfig.getOverlayXPBar() && event.getType() == EXPERIENCE) {
            final ScaledResolution resolution = event.getResolution();

            GuiXPBar.update(Minecraft.getMinecraft().player.getHeldItemMainhand());
            if (GuiXPBar.drawXPBar((resolution.getScaledWidth() - 182) / 2, resolution.getScaledHeight() - 29)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = LOW)
    public static void onItemTooltip(final ItemTooltipEvent event) {
        final EntityPlayer player = event.getEntityPlayer();

        if (player != null) {
            final ItemStack itemStack = event.getItemStack();

            if (itemStack.getItem() instanceof ISoulboundItem) {
                final SoulboundCapability capability = SoulItemHelper.getFirstCapability(player, itemStack.getItem());
                final List<String> tooltip = event.getToolTip();
                final int startIndex = tooltip.indexOf(I18n.format("item.modifiers.mainhand")) + 1;

                final List<String> prior = new ArrayList<>(tooltip).subList(0, startIndex);
                final List<String> insertion = capability.getTooltip(capability.getItemType(itemStack));
                final List<String> posterior = new ArrayList<>(tooltip).subList(startIndex + itemStack.getAttributeModifiers(MAINHAND).size(), tooltip.size());

                tooltip.clear();
                tooltip.addAll(prior);
                tooltip.addAll(insertion);
                tooltip.addAll(posterior);

                final int row = insertion.lastIndexOf("") + prior.size();

                GuiXPBar.setData(row, FONT_RENDERER.getStringWidth(tooltip.get(row - 2)) - 4);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderTooltip(final RenderTooltipEvent.PostText event) {
        if (event.getStack().getItem() instanceof ISoulboundItem) {
            GuiXPBar.drawTooltip(event.getX(), event.getY(), event.getStack());
        }
    }
}
