package transfarmer.soulboundarmory.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;
import scala.actors.threadpool.Arrays;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeaponCapability;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.client.gui.GuiXPBar;
import transfarmer.soulboundarmory.config.ClientConfig;
import transfarmer.soulboundarmory.item.ItemSoulbound;

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
import static transfarmer.soulboundarmory.statistics.base.enumeration.Item.STAFF;

@EventBusSubscriber(value = CLIENT, modid = Main.MOD_ID)
public class ClientEventListeners {
    public static final GuiXPBar OVERLAY_XP_BAR = new GuiXPBar();
    public static final GuiXPBar TOOLTIP_XP_BAR = new GuiXPBar();

    @SubscribeEvent
    public static void onMouseInput(final MouseEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final IWeaponCapability capability = WeaponProvider.get(minecraft.player);

            if (capability.getItemType() == STAFF) {
                final int dWheel = event.getDwheel();

                if (dWheel != 0) {
                    capability.cycleSpells(-dWheel / 120);
                    minecraft.ingameGUI.setOverlayMessage(new TextComponentTranslation("§4§l%s", capability.getSpell()), false);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent event) {
        if (event.phase == END) {
            if (MENU_KEY.isPressed()) {
                final SoulboundCapability capability = SoulboundItemUtil.getFirstHeldCapability(Minecraft.getMinecraft().player);

                if (capability != null) {
                    capability.openGUI();
                }
            } else if (TOGGLE_XP_BAR_KEY.isPressed()) {
                ClientConfig.setOverlayXPBar(!ClientConfig.getOverlayXPBar());
                ClientConfig.instance().save();
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGameOverlay(final RenderGameOverlayEvent.Pre event) {
        if (event.getType() == EXPERIENCE && ClientConfig.getOverlayXPBar()) {
            final ScaledResolution resolution = event.getResolution();

            if (OVERLAY_XP_BAR.drawXPBar(resolution)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = LOW)
    public static void onItemTooltip(final ItemTooltipEvent event) {
        final EntityPlayer player = event.getEntityPlayer();

        if (player != null) {
            ItemStack itemStack = event.getItemStack();

            if (itemStack.getItem() instanceof ItemSoulbound) {
                final SoulboundCapability capability = SoulboundItemUtil.getFirstCapability(player, itemStack.getItem());
                final List<String> tooltip = event.getToolTip();
                final int startIndex = tooltip.indexOf(I18n.format("item.modifiers.mainhand")) + 1;
                final int toIndex = tooltip.size();
                final int fromIndex = Math.min(toIndex - 1, startIndex + itemStack.getAttributeModifiers(MAINHAND).size());

                Main.LOGGER.warn("fromIndex: " + fromIndex);
                Main.LOGGER.warn("toIndex: " + toIndex);
                Main.LOGGER.warn("startIndex: " + startIndex);
                Main.LOGGER.warn("size: " + tooltip.size());
                Main.LOGGER.warn(Arrays.toString(tooltip.toArray()));

                final List<String> prior = new ArrayList<>(tooltip).subList(0, startIndex);
                final List<String> insertion = capability.getTooltip(capability.getItemType(itemStack));
                final List<String> posterior = new ArrayList<>(tooltip).subList(fromIndex, toIndex);

                tooltip.clear();
                tooltip.addAll(prior);
                tooltip.addAll(insertion);
                tooltip.addAll(posterior);

                final int row = insertion.lastIndexOf("") + prior.size();

                TOOLTIP_XP_BAR.setData(row, FONT_RENDERER.getStringWidth(tooltip.get(row - 2)) - 4);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderTooltip(final RenderTooltipEvent.PostText event) {
        if (event.getStack().getItem() instanceof ItemSoulbound) {
            TOOLTIP_XP_BAR.drawTooltip(event.getX(), event.getY(), event.getStack());
        }
    }
}
