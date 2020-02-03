package transfarmer.soulweapons.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.data.SoulWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.LEVEL;
import static transfarmer.soulweapons.data.SoulWeaponDatum.XP;

@SideOnly(CLIENT)
public class TooltipXPBar extends Gui {
    public TooltipXPBar(SoulWeaponType type, int tooltipX, int tooltipY, int originalEnchantments) {
        final Minecraft mc = Minecraft.getMinecraft();
        final FontRenderer fontRenderer = mc.fontRenderer;
        final ISoulWeapon capability = Minecraft.getMinecraft().player.getCapability(CAPABILITY, null);
        int level = capability.getDatum(LEVEL, type);
        int barLeftX = tooltipX + 44;
        int barTopY = tooltipY + 60 + 10 * originalEnchantments;
        int length = 62;

        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.getTextureManager().bindTexture(Main.XP_BAR);

        this.drawTexturedModalRect(barLeftX - length / 2, barTopY, 0, 70, length, 5);
        this.drawTexturedModalRect(barLeftX - length / 2, barTopY, 0, 75,
            Math.round((float) capability.getDatum(XP, type) / capability.getNextLevelXP(type) * length), 5);

        String levelString = String.format("%d", level);
        int x1 = barLeftX - fontRenderer.getStringWidth(levelString) / 2;
        int y1 = barTopY - 6;
        fontRenderer.drawString(levelString, x1 + 1, y1, 0);
        fontRenderer.drawString(levelString, x1 - 1, y1, 0);
        fontRenderer.drawString(levelString, x1, y1 + 1, 0);
        fontRenderer.drawString(levelString, x1, y1 - 1, 0);
        fontRenderer.drawString(levelString, x1, y1, 0xF7193B);

        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
    }
}
