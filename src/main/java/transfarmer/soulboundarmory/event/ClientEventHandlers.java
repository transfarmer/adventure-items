package transfarmer.soulboundarmory.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.ICapability;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.client.gui.TooltipXPBar;
import transfarmer.soulboundarmory.item.ISoulboundItem;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.fml.common.eventhandler.EventPriority.LOW;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.client.KeyBindings.MENU_KEY;

@EventBusSubscriber(value = CLIENT, modid = Main.MOD_ID)
public class ClientEventHandlers {
    private static int tick = 0;

    @SubscribeEvent
    public static void on(final ClientTickEvent event) {
        if (event.phase == Phase.END && tick == 0) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiScreen() {
                @Override
                public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
                    super.drawScreen(mouseX, mouseY, partialTicks);

                    this.drawDefaultBackground();
                    this.drawCenteredString(this.mc.fontRenderer, "soulbound armory warning", width / 2, 40, 0xFF8000);
                    this.drawCenteredString(this.mc.fontRenderer, "This version of soulbound armory is a beta test.", width / 2, height / 2, 0xFFFFFF);
                    this.drawCenteredString(this.mc.fontRenderer, "It and all future versions will not load progress from previous versions.", width / 2, height / 2 + 20, 0xFFFFFF);
                    this.drawCenteredString(this.mc.fontRenderer, "Press the escape key to continue.", width / 2, height / 2 + 100, 0xFFFFFF);
                }
            });

            tick++;
        }
    }

    @SubscribeEvent(priority = LOW)
    public static void onItemTooltip(final ItemTooltipEvent event) {
        final EntityPlayer player = event.getEntityPlayer();

        if (player != null) {
            final ItemStack itemStack = event.getItemStack();

            if (itemStack.getItem() instanceof ISoulboundItem) {
                final ICapability capability = SoulItemHelper.getFirstCapability(player, itemStack.getItem());
                final IItem type = capability.getItemType(itemStack);
                final List<String> tooltip = event.getToolTip();
                final int startIndex = tooltip.indexOf(I18n.format("item.modifiers.mainhand")) + 1;

                final String[] prior = tooltip.subList(0, startIndex).toArray(new String[0]);
                final List<String> insertion = capability.getTooltip(type);
                final String[] posterior = tooltip.subList(startIndex + itemStack.getAttributeModifiers(MAINHAND).size(), tooltip.size()).toArray(new String[0]);

                tooltip.clear();
                tooltip.addAll(Arrays.asList(prior));
                tooltip.addAll(insertion);
                tooltip.addAll(Arrays.asList(posterior));
                TooltipXPBar.setRow(prior.length + insertion.lastIndexOf(""));
            }
        }
    }

    @SubscribeEvent
    public static void onRenderTooltip(final RenderTooltipEvent.PostText event) {
        if (event.getStack().getItem() instanceof ISoulboundItem) {
            TooltipXPBar.render(event.getX(), event.getY(), event.getStack());
        }
    }

    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent event) {
        if (event.phase == END) {
            final Minecraft minecraft = Minecraft.getMinecraft();

            if (MENU_KEY.isPressed()) {
                final EntityPlayer player = minecraft.player;
                final ICapability capability = SoulItemHelper.getFirstHeldCapability(player);

                if (capability != null) {
                    capability.onKeyPress();
                }
            }
        }
    }
}
