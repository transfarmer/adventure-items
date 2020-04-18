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
    protected static TooltipXPBar instance = new TooltipXPBar();

    protected static final Minecraft minecraft = Minecraft.getMinecraft();
    protected static final FontRenderer fontRenderer = minecraft.fontRenderer;

    protected ItemStack itemStack;
    protected ISoulCapability capability;
    protected SoulType type;

    protected void init(final ItemStack itemStack) {
        this.itemStack = itemStack;
        this.capability = SoulItemHelper.getCapability(minecraft.player, itemStack.getItem());
        this.type = this.capability.getType(itemStack);
    }

    protected void renderXPBar(final int tooltipX, final int tooltipY) {
        final int originalEnchantments = this.itemStack.getEnchantmentTagList().tagCount();
        final int level = this.capability.getDatum(DATA.level, this.type);
        final int barLeftX = tooltipX + 44;
        final int barTopY = tooltipY + (originalEnchantments + this.capability.getTooltip(this.type).indexOf("") + 4) * 10;
        final int length = 62;

        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.color(ColorConfig.getRed(), ColorConfig.getGreen(), ColorConfig.getBlue(), ColorConfig.getAlpha());
        minecraft.getTextureManager().bindTexture(XP_BAR);

        this.drawTexturedModalRect(barLeftX - length / 2, barTopY, 0, 10, length, 5);
        this.drawTexturedModalRect(barLeftX - length / 2, barTopY, 0, 15,
                Math.min(length, Math.round((float) this.capability.getDatum(DATA.xp, this.type) / this.capability.getNextLevelXP(this.type) * length)), 5);

        minecraft.getTextureManager().deleteTexture(XP_BAR);

        final String levelString = String.format("%d", level);
        final int x1 = barLeftX - fontRenderer.getStringWidth(levelString) / 2;
        final int y1 = barTopY - 6;

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
        if (instance.itemStack == null || !instance.itemStack.getItem().equals(itemStack.getItem())) {
            instance.init(itemStack);
        }

        instance.renderXPBar(tooltipX, tooltipY);
    }
}
