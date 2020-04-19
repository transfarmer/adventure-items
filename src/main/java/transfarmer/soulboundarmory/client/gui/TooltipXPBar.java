package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.IItemCapability;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.config.ColorConfig;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import java.awt.*;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.Main.ResourceLocations.XP_BAR;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

@SideOnly(CLIENT)
public class TooltipXPBar extends Gui {
    protected static final TooltipXPBar instance = new TooltipXPBar();

    protected final Minecraft minecraft = Minecraft.getMinecraft();
    protected final FontRenderer fontRenderer = minecraft.fontRenderer;

    protected ItemStack itemStack;
    protected IItemCapability capability;
    protected IItem item;
    protected int row;

    public static void setRow(final int row) {
        if (instance.row != row) {
            instance.row = row;
        }
    }

    protected void setItemStack(final ItemStack itemStack) {
        this.itemStack = itemStack;
        this.capability = SoulItemHelper.getCapability(this.minecraft.player, itemStack.getItem());
        this.item = this.capability.getItemType(itemStack);
    }

    protected void renderXPBar(final int tooltipX, final int tooltipY) {
        final int level = this.capability.getDatum(this.item, LEVEL);
        final int barLeftX = tooltipX + 44;
        final int barTopY = tooltipY + row * 10;
        final int length = 62;

        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.color(ColorConfig.getRed(), ColorConfig.getGreen(), ColorConfig.getBlue(), ColorConfig.getAlpha());
        minecraft.getTextureManager().bindTexture(XP_BAR);

        this.drawTexturedModalRect(barLeftX - length / 2, barTopY, 0, 10, length, 5);
        this.drawTexturedModalRect(barLeftX - length / 2, barTopY, 0, 15,
                Math.min(length, Math.round((float) this.capability.getDatum(this.item, XP) / this.capability.getNextLevelXP(this.item) * length)), 5);

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
        if (instance.itemStack != itemStack) {
            instance.setItemStack(itemStack);
        }

        instance.renderXPBar(tooltipX, tooltipY);
    }
}
