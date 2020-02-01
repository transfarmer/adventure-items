package transfarmer.soulweapons.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.SoulWeaponType;
import transfarmer.soulweapons.capability.ISoulWeapon;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

@SideOnly(CLIENT)
public class TooltipXPBar extends Gui {
    public TooltipXPBar(SoulWeaponType weaponType) {
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        final Minecraft mc = Minecraft.getMinecraft();
        final FontRenderer fontRenderer = mc.fontRenderer;
        final ISoulWeapon instance = Minecraft.getMinecraft().player.getCapability(CAPABILITY, null);
        final ResourceLocation ICONS = new ResourceLocation(Main.MODID, "textures/gui/icons.png");

        float widthScaleFactor = (float) scaledResolution.getScaledWidth() / mc.displayWidth;
        float heightScaleFactor = (float) scaledResolution.getScaledHeight() / mc.displayHeight;

        this.zLevel = 1000F;

        int x0 = 56 + (int) (Mouse.getX() * widthScaleFactor);
        int y0 = 50 + (int) (scaledResolution.getScaledHeight() - Mouse.getY() * heightScaleFactor);
        int length = 62;

        mc.getTextureManager().bindTexture(ICONS);
        int level = instance.getLevel(weaponType);

        this.drawTexturedModalRect(x0 - length / 2, y0, 0, 0, length, 5);
        this.drawTexturedModalRect(x0 - length / 2, y0, 0, 5, Math.round((float) instance.getXP(weaponType) / instance.getNextLevelXP(weaponType) * length), 5);

        String levelString = String.format("%d", level);
        int x1 = x0 - fontRenderer.getStringWidth(levelString) / 2;
        int y1 = y0 - 8;
        fontRenderer.drawString(levelString, x1 + 1, y1, 0);
        fontRenderer.drawString(levelString, x1 - 1, y1, 0);
        fontRenderer.drawString(levelString, x1, y1 + 1, 0);
        fontRenderer.drawString(levelString, x1, y1 - 1, 0);
        fontRenderer.drawString(levelString, x1, y1, Integer.parseInt("80FF20", 16));
    }
}
