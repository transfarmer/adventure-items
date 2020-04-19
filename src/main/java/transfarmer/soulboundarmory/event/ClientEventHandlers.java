package transfarmer.soulboundarmory.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import transfarmer.soulboundarmory.Main;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

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
}
