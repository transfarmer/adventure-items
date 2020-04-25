package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulItemHelper;
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
    protected SoulboundCapability capability;
    protected IItem item;
    protected int row;

    public static void setRow(final int row) {
        if (instance.row != row) {
            instance.row = row;
        }
    }

    protected void setItemStack(final ItemStack itemStack) {
        this.itemStack = itemStack;
        this.capability = SoulItemHelper.getFirstCapability(this.minecraft.player, itemStack.getItem());
        this.item = this.capability.getItemType(itemStack);
    }

    protected void renderXPBar(final int tooltipX, final int tooltipY) {
        final int level = this.capability.getDatum(this.item, LEVEL);
        final int barX = tooltipX + 44;
        final int barY = tooltipY + row * 10;
        final int length = 62;

        GlStateManager.color(ColorConfig.getRed(), ColorConfig.getGreen(), ColorConfig.getBlue(), ColorConfig.getAlpha());
        minecraft.getTextureManager().bindTexture(XP_BAR);

        this.drawTexturedModalRect(barX - length / 2, barY, 0, 10, length, 5);
        this.drawTexturedModalRect(barX - length / 2, barY, 0, 15,
                Math.min(length, Math.round((float) this.capability.getDatum(this.item, XP) / this.capability.getNextLevelXP(this.item) * length)), 5);

        minecraft.getTextureManager().deleteTexture(XP_BAR);

        final String levelString = String.format("%d", level);
        final int levelX = barX - fontRenderer.getStringWidth(levelString) / 2;
        final int levelY = barY - 6;

        fontRenderer.drawString(levelString, levelX + 1, levelY, 0);
        fontRenderer.drawString(levelString, levelX - 1, levelY, 0);
        fontRenderer.drawString(levelString, levelX, levelY + 1, 0);
        fontRenderer.drawString(levelString, levelX, levelY - 1, 0);
        fontRenderer.drawString(levelString, levelX, levelY,
                new Color(ColorConfig.getRed(), ColorConfig.getGreen(), ColorConfig.getBlue(), ColorConfig.getAlpha()).getRGB());

        GlStateManager.disableLighting();
    }

    public static void render(final int tooltipX, final int tooltipY, final ItemStack itemStack) {
        if (instance.itemStack != itemStack) {
            instance.setItemStack(itemStack);
        }

        instance.renderXPBar(tooltipX, tooltipY);
    }
}
