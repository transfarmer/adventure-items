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
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.ISoulbound;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulItemHelper;
import transfarmer.soulboundarmory.client.gui.TooltipXPBar;
import transfarmer.soulboundarmory.item.ISoulboundItem;

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
    public static void onClientTick(final ClientTickEvent event) {
        if (event.phase == END) {
            final Minecraft minecraft = Minecraft.getMinecraft();

            if (MENU_KEY.isPressed()) {
                final EntityPlayer player = minecraft.player;
                final ISoulbound capability = SoulItemHelper.getFirstHeldCapability(player);

                if (capability != null) {
                    capability.openGUI();
                }
            }

            if (tick == 0) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiScreen() {
                    @Override
                    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
                        super.drawScreen(mouseX, mouseY, partialTicks);

                        this.drawDefaultBackground();
                        this.drawCenteredString(this.mc.fontRenderer, "soulbound armory warning", width / 2, 40, 0xFF8000);
                        this.drawCenteredString(this.mc.fontRenderer, "Soulbound armory versions 2.7.0 and above", width / 2, height / 2 - 5, 0xFFFFFF);
                        this.drawCenteredString(this.mc.fontRenderer, "will not load progress from previous versions.", width / 2, height / 2 + 5, 0xFFFFFF);
                        this.drawCenteredString(this.mc.fontRenderer, "Press the escape key to continue.", width / 2, height / 2 + 100, 0xFFFFFF);
                    }
                });

                tick++;
            }
        }
    }

    @SubscribeEvent(priority = LOW)
    public static void onItemTooltip(final ItemTooltipEvent event) {
        final EntityPlayer player = event.getEntityPlayer();

        if (player != null) {
            final ItemStack itemStack = event.getItemStack();

            if (itemStack.getItem() instanceof ISoulboundItem) {
                final ISoulbound capability = SoulItemHelper.getFirstCapability(player, itemStack.getItem());
                final List<String> tooltip = event.getToolTip();
                final int startIndex = tooltip.indexOf(I18n.format("item.modifiers.mainhand")) + 1;

                final String[] prior = tooltip.subList(0, startIndex).toArray(new String[0]);
                final List<String> insertion = capability.getTooltip(capability.getItemType(itemStack));
                final String[] posterior = tooltip.subList(startIndex + itemStack.getAttributeModifiers(MAINHAND).size(), tooltip.size()).toArray(new String[0]);

                tooltip.clear();
                tooltip.addAll(Arrays.asList(prior));
                tooltip.addAll(insertion);
                tooltip.addAll(Arrays.asList(posterior));

                TooltipXPBar.setRow(insertion.lastIndexOf("") + prior.length);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderTooltip(final RenderTooltipEvent.PostText event) {
        if (event.getStack().getItem() instanceof ISoulboundItem && SoulItemHelper.getFirstCapability(Minecraft.getMinecraft().player, event.getStack()).getItemType() != null) {
            TooltipXPBar.render(event.getX(), event.getY(), event.getStack());
        }
    }
}
