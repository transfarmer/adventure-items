package transfarmer.soulboundarmory.event;

import nerdhub.cardinal.components.api.component.Component;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import transfarmer.soulboundarmory.MainClient;
import transfarmer.soulboundarmory.client.gui.XPBarGUI;
import transfarmer.soulboundarmory.component.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;

@Environment(EnvType.CLIENT)
public class ClientEventListeners {
    public static final XPBarGUI OVERLAY_XP_BAR = new XPBarGUI();
    public static final XPBarGUI TOOLTIP_XP_BAR = new XPBarGUI();

//    public static void onMouseInput(final MouseEvent event) {
//        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)) {
//            final Minecraft minecraft = CLIENT;
//            final IWeaponComponent component = WeaponProvider.get(minecraft.player);
//
//            if (component.getItemType() == STAFF) {
//                final int dWheel = event.getDwheel();
//
//                if (dWheel != 0) {
//                    component.cycleSpells(-dWheel / 120);
//                    minecraft.ingameGUI.setOverlayMessage(new TextComponentTranslation("§4§l%s", component.getSpell()), false);
//                    event.setCanceled(true);
//                }
//            }
//        }
//    }

    public static void onClientTick(final ClientTickCallback event) {
        if (MainClient.GUI_KEY_BINDING.isPressed()) {
            final ISoulboundItemComponent<? extends Component> component = SoulboundItemUtil.getFirstComponent(MainClient.PLAYER);

            if (component != null) {
                component.openGUI();
            }
//        } else if (MainClient.TOGGLE_XP_BAR_KEY_BINDING.isPressed()) {
//            ClientConfig.setOverlayXPBar(!ClientConfig.getOverlayXPBar());
//            ClientConfig.instance().save();
        }
    }
//
//    public static void onRenderGameOverlay(final RenderGameOverlayEvent.Pre event) {
//        if (event.getType() == EXPERIENCE && ClientConfig.getOverlayXPBar()) {
//            final ScaledResolution resolution = event.getResolution();
//
//            if (OVERLAY_XP_BAR.drawXPBar(resolution)) {
//                event.setCanceled(true);
//            }
//        }
//    }
//
//    public static void onItemTooltip(final ItemTooltipEvent event) {
//        final PlayerEntity player = event.getPlayerEntity();
//
//        if (player != null) {
//            final ItemStack itemStack = event.getItemStack();
//            final Item item = itemStack.getItem();
//
//            if (item instanceof SoulboundItem) {
//                final ISoulboundItemComponent component = SoulboundItemUtil.getFirstComponent(player, item);
//                final List<String> tooltip = event.getToolTip();
//                final int startIndex = tooltip.indexOf(I18n.translate("item.modifiers.mainhand")) + 1;
//                final int toIndex = tooltip.size();
//                final int fromIndex = Math.min(toIndex - 1, startIndex + ((SoulboundItem) item).getMainhandAttributeEntries(itemStack, player));
//
//                final List<String> prior = new ArrayList<>(tooltip).subList(0, startIndex);
//                final List<String> insertion = component.getTooltip(component.getItemType(itemStack));
//                final List<String> posterior = new ArrayList<>(tooltip).subList(fromIndex, toIndex);
//
//                tooltip.clear();
//                tooltip.addAll(prior);
//                tooltip.addAll(insertion);
//                tooltip.addAll(posterior);
//
//                final int row = insertion.lastIndexOf("") + prior.size();
//
//                TOOLTIP_XP_BAR.setData(row, TEXT_RENDERER.getStringWidth(tooltip.get(row - 2)) - 4);
//            }
//        }
//    }
//
//    public static void onRenderTooltip(final RenderTooltipEvent.PostText event) {
//        if (event.getStack().getItem() instanceof SoulboundItem) {
//            TOOLTIP_XP_BAR.drawTooltip(event.getX(), event.getY(), event.getStack());
//        }
//    }
}
