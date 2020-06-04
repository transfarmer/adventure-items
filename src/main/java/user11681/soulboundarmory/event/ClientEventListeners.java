package user11681.soulboundarmory.event;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import user11681.anvil.entrypoint.ClientListenerInitializer;
import user11681.anvil.event.Listener;
import user11681.anvilevents.event.client.ItemTooltipEvent;
import user11681.anvilevents.event.client.LoadResourcesEvent;
import user11681.anvilevents.event.client.gui.hud.RenderExperienceBarEvent;
import user11681.anvilevents.event.client.gui.screen.RenderStackTooltipEvent;
import user11681.anvilevents.event.client.mouse.MouseScrollEvent;
import user11681.soulboundarmory.client.gui.ExperienceBarOverlay;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.item.SoulboundItem;

import static user11681.soulboundarmory.MainClient.CLIENT;
import static user11681.usersmanual.client.gui.screen.ExtendedScreen.TEXT_RENDERER;

@Environment(EnvType.CLIENT)
public class ClientEventListeners implements ClientListenerInitializer {
    public static ExperienceBarOverlay OVERLAY_BAR;
    public static ExperienceBarOverlay TOOLTIP_BAR;

    @Listener
    public static void onMouseScroll(final MouseScrollEvent event) {
        if (Screen.hasAltDown()) {
            final PlayerEntity player = CLIENT.player;

            if (player != null && player.world != null) {
                final ItemStorage<?> storage = Components.WEAPON_COMPONENT.get(player).getHeldItemStorage();

                if (storage instanceof StaffStorage) {
                    final int dY = (int) event.getDY();

                    if (dY != 0) {
                        final StaffStorage staffStorage = (StaffStorage) storage;

                        staffStorage.cycleSpells(-dY);
                        CLIENT.inGameHud.setOverlayMessage(new TranslatableText("§4§l%s", staffStorage.getSpell()), false);
                        event.setFail();
                    }
                }
            }
        }
    }

    @Listener
    public static void onRenderGameOverlay(final RenderExperienceBarEvent event) {
        if (Configuration.instance().client.overlayExperienceBar && OVERLAY_BAR.draw()) {
            event.setFail();
        }
    }

    @Listener
    public static void onItemTooltip(final ItemTooltipEvent event) {
        final PlayerEntity player = event.getPlayer();

        if (player != null) {
            final ItemStack itemStack = event.getItemStack();
            final Item item = itemStack.getItem();
            final ItemStorage<?> storage = ItemStorage.get(player, item);

            if (storage != null) {
                final List<Text> tooltip = event.getTooltip();
                final int startIndex = tooltip.indexOf(new TranslatableText(I18n.translate("item.modifiers.mainhand"))) + 1;
                final int toIndex = tooltip.size();
                final int fromIndex = Math.min(toIndex - 1, startIndex + ((SoulboundItem) item).getMainhandAttributeEntries(itemStack, player));

                final List<Text> prior = new ArrayList<>(tooltip).subList(0, startIndex);
                final List<Text> insertion = storage.getTooltip();
                final List<Text> posterior = new ArrayList<>(tooltip).subList(fromIndex, toIndex);

                tooltip.clear();
                tooltip.addAll(prior);
                tooltip.addAll(insertion);
                tooltip.addAll(posterior);

                final int row = insertion.lastIndexOf(new LiteralText("")) + prior.size();

                TOOLTIP_BAR.setData(row, TEXT_RENDERER.getStringWidth(tooltip.get(row - 2).asFormattedString()) - 4);
            }
        }
    }

    @Listener
    public static void onRenderTooltip(final RenderStackTooltipEvent.Post event) {
        if (event.getStack().getItem() instanceof SoulboundItem) {
            TOOLTIP_BAR.drawTooltip(event.getX(), event.getY(), event.getStack());
        }
    }

    @Listener
    public static void onLoadResources(final LoadResourcesEvent.Launch event) {
        RenderSystem.recordRenderCall(() -> {
            OVERLAY_BAR = new ExperienceBarOverlay();
            TOOLTIP_BAR = new ExperienceBarOverlay();
        });
    }

    @Override
    public Collection<Class<?>> get() {
        return Collections.singleton(this.getClass());
    }
}
