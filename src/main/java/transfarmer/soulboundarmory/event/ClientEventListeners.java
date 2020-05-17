package transfarmer.soulboundarmory.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.component.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.component.soulbound.weapon.IWeaponComponent;
import transfarmer.soulboundarmory.client.gui.GuiXPBar;
import transfarmer.soulboundarmory.config.ClientConfig;
import transfarmer.soulboundarmory.item.SoulboundItem;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.EXPERIENCE;
import static net.minecraftforge.fml.common.eventhandler.EventPriority.LOW;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.client.KeyBindings.MENU_KEY;
import static transfarmer.soulboundarmory.client.KeyBindings.TOGGLE_XP_BAR_KEY;
import static transfarmer.soulboundarmory.client.gui.screen.common.ExtendedScreen.TEXT_RENDERER;
import static transfarmer.soulboundarmory.statistics.Item.STAFF;

@EventBusSubscriber(value = CLIENT, modid = Main.MOD_ID)
public class ClientEventListeners {
    public static final GuiXPBar OVERLAY_XP_BAR = new GuiXPBar();
    public static final GuiXPBar TOOLTIP_XP_BAR = new GuiXPBar();

    @SubscribeEvent
    public static void onMouseInput(final MouseEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)) {
            final Minecraft minecraft = CLIENT;
            final IWeaponComponent component = WeaponProvider.get(minecraft.player);

            if (component.getItemType() == STAFF) {
                final int dWheel = event.getDwheel();

                if (dWheel != 0) {
                    component.cycleSpells(-dWheel / 120);
                    minecraft.ingameGUI.setOverlayMessage(new TextComponentTranslation("§4§l%s", component.getSpell()), false);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent event) {
        if (event.phase == END) {
            if (MENU_KEY.isPressed()) {
                final ISoulboundItemComponent component = SoulboundItemUtil.getFirstHeldComponent(CLIENT.player);

                if (component != null) {
                    component.openGUI();
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
        final PlayerEntity player = event.getPlayerEntity();

        if (player != null) {
            final ItemStack itemStack = event.getItemStack();
            final Item item = itemStack.getItem();

            if (item instanceof SoulboundItem) {
                final ISoulboundItemComponent component = SoulboundItemUtil.getFirstComponent(player, item);
                final List<String> tooltip = event.getToolTip();
                final int startIndex = tooltip.indexOf(I18n.translate("item.modifiers.mainhand")) + 1;
                final int toIndex = tooltip.size();
                final int fromIndex = Math.min(toIndex - 1, startIndex + ((SoulboundItem) item).getMainhandAttributeEntries(itemStack, player));

                final List<String> prior = new ArrayList<>(tooltip).subList(0, startIndex);
                final List<String> insertion = component.getTooltip(component.getItemType(itemStack));
                final List<String> posterior = new ArrayList<>(tooltip).subList(fromIndex, toIndex);

                tooltip.clear();
                tooltip.addAll(prior);
                tooltip.addAll(insertion);
                tooltip.addAll(posterior);

                final int row = insertion.lastIndexOf("") + prior.size();

                TOOLTIP_XP_BAR.setData(row, TEXT_RENDERER.getStringWidth(tooltip.get(row - 2)) - 4);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderTooltip(final RenderTooltipEvent.PostText event) {
        if (event.getStack().getItem() instanceof SoulboundItem) {
            TOOLTIP_XP_BAR.drawTooltip(event.getX(), event.getY(), event.getStack());
        }
    }
}
