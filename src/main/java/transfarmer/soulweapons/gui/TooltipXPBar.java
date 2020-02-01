package transfarmer.soulweapons.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.weapon.SoulWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

@SideOnly(CLIENT)
public class TooltipXPBar extends Gui {
    public TooltipXPBar(SoulWeaponType weaponType, int tooltipX, int tooltipY) {
        final Minecraft mc = Minecraft.getMinecraft();
        final FontRenderer fontRenderer = mc.fontRenderer;
        final ResourceLocation ICONS = new ResourceLocation(Main.MODID, "textures/gui/icons_test.png");
        final ISoulWeapon instance = Minecraft.getMinecraft().player.getCapability(CAPABILITY, null);
        int level = instance.getLevel(weaponType);
        int x0 = 44 + tooltipX;
        int y0 = 62 + tooltipY;
        int length = 62;

        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        // GlStateManager.shadeModel(GL11.GL_SMOOTH);
        // GlStateManager.enableBlend();
        // GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(ICONS);

        this.drawTexturedModalRect(x0 - length / 2, y0, 0, 10, length, 5);
        this.drawTexturedModalRect(x0 - length / 2, y0, 0, 15,
            Math.round((float) instance.getXP(weaponType) / instance.getNextLevelXP(weaponType) * length), 5);

        String levelString = String.format("%d", level);
        int x1 = x0 - fontRenderer.getStringWidth(levelString) / 2;
        int y1 = y0 - 6;
        // int color = 80FF20;
        int color = 0xA2101C;
        fontRenderer.drawString(levelString, x1 + 1, y1, 0);
        fontRenderer.drawString(levelString, x1 - 1, y1, 0);
        fontRenderer.drawString(levelString, x1, y1 + 1, 0);
        fontRenderer.drawString(levelString, x1, y1 - 1, 0);
        fontRenderer.drawString(levelString, x1, y1, color);

        // GlStateManager.disableBlend();
        // GlStateManager.enableTexture2D();
        // GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
    }

    public static void renderBar(int x, int y, int width, int height, int color) {

    }
}
