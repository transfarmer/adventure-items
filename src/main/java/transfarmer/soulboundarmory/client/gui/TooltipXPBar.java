package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.ISoulCapability;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.config.ColorConfig;
import transfarmer.soulboundarmory.statistics.SoulType;

import java.awt.*;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.Main.ResourceLocations.XP_BAR;
import static transfarmer.soulboundarmory.statistics.SoulDatum.DATA;

@SideOnly(CLIENT)
public class TooltipXPBar extends Gui {
    protected final Minecraft minecraft;
    protected final FontRenderer fontRenderer;
    protected final ISoulCapability capability;
    protected final SoulType type;

    protected TooltipXPBar(final ItemStack itemStack) {
        this.minecraft = Minecraft.getMinecraft();
        this.fontRenderer = this.minecraft.fontRenderer;
        this.capability = SoulItemHelper.getCapability(minecraft.player, itemStack.getItem());
        this.type = capability.getType(itemStack);
    }

    protected void renderXPBar(final int tooltipX, final int tooltipY, final ItemStack itemStack) {
        final int originalEnchantments = itemStack.getEnchantmentTagList().tagCount();
        int level = capability.getDatum(DATA.level, type);
        int barLeftX = tooltipX + 44;
        int barTopY = tooltipY + (originalEnchantments + capability.getTooltip(type).indexOf("") + 4) * 10;
        int length = 62;

        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.color(ColorConfig.getRed(), ColorConfig.getGreen(), ColorConfig.getBlue(), ColorConfig.getAlpha());
        minecraft.getTextureManager().bindTexture(XP_BAR);

        this.drawTexturedModalRect(barLeftX - length / 2, barTopY, 0, 10, length, 5);
        this.drawTexturedModalRect(barLeftX - length / 2, barTopY, 0, 15,
                Math.min(length, Math.round((float) capability.getDatum(DATA.xp, type) / capability.getNextLevelXP(type) * length)), 5);

        minecraft.getTextureManager().deleteTexture(XP_BAR);

        String levelString = String.format("%d", level);
        int x1 = barLeftX - fontRenderer.getStringWidth(levelString) / 2;
        int y1 = barTopY - 6;
        fontRenderer.drawString(levelString, x1 + 1, y1, 0);
        fontRenderer.drawString(levelString, x1 - 1, y1, 0);
        fontRenderer.drawString(levelString, x1, y1 + 1, 0);
        fontRenderer.drawString(levelString, x1, y1 - 1, 0);
        fontRenderer.drawString(levelString, x1, y1,
                new Color(ColorConfig.getRed(), ColorConfig.getGreen(), ColorConfig.getBlue(), ColorConfig.getAlpha()).getRGB());

        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
    }

    public static void render(final int tooltipX, final int tooltipY, final ItemStack itemStack) {
        new TooltipXPBar(itemStack).renderXPBar(tooltipX, tooltipY, itemStack);
    }
}
