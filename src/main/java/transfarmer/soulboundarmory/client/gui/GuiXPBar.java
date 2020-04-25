package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulItemHelper;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiExtended;
import transfarmer.soulboundarmory.config.ColorConfig;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import java.awt.*;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.Main.ResourceLocations.XP_BAR;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

@SideOnly(CLIENT)
public class GuiXPBar extends Gui implements GuiExtended {
    protected static GuiXPBar instance;
    protected static int[] data = new int[2];

    protected ItemStack itemStack;
    protected SoulboundCapability capability;
    protected IItem item;

    public GuiXPBar(final ItemStack itemStack) {
        this(SoulItemHelper.getFirstCapability(MINECRAFT.player, itemStack.getItem()));

        this.itemStack = itemStack;
        this.item = this.capability.getItemType(itemStack);
    }

    public GuiXPBar(final SoulboundCapability capability) {
        this.capability = capability;
        this.item = capability.getItemType();
    }

    public static void setRow(final int row, final int length) {
        data[0] = row;
        data[1] = length;
    }

    public static void drawTooltip(final int tooltipX, final int tooltipY, final ItemStack itemStack) {
        if (instance == null || instance.itemStack != itemStack) {
            instance = new GuiXPBar(itemStack);
        }

        final int x = tooltipX + 4;
        final int y = tooltipY + data[0] * 10;

        instance.drawXPBar(x, y, data[1]);
    }

    public void drawXPBar(final int x, final int y, final int length) {
        final float ratio = (float) this.capability.getDatum(this.item, XP) / this.capability.getNextLevelXP(this.item);
        final Color color = new Color(ColorConfig.getRed(), ColorConfig.getGreen(), ColorConfig.getBlue(), ColorConfig.getAlpha());

        GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);

        TEXTURE_MANAGER.bindTexture(XP_BAR);
        GuiExtended.drawHorizontalInterpolatedTexturedRect(x, y, 0, 0, 4, 177, 182, length, 5);
        GuiExtended.drawHorizontalInterpolatedTexturedRect(x, y, 0, 5, 4, (int) (ratio * 177), (int) (ratio * 182), this.capability.canLevelUp(this.item)
                ? Math.min(length, (int) (ratio * length))
                : length, 5
        );
        TEXTURE_MANAGER.deleteTexture(XP_BAR);

        final int level = this.capability.getDatum(this.item, LEVEL);
        final String levelString = String.format("%d", level);
        final int levelX = x + (length - FONT_RENDERER.getStringWidth(levelString)) / 2;
        final int levelY = y - 6;

        FONT_RENDERER.drawString(levelString, levelX + 1, levelY, 0);
        FONT_RENDERER.drawString(levelString, levelX - 1, levelY, 0);
        FONT_RENDERER.drawString(levelString, levelX, levelY + 1, 0);
        FONT_RENDERER.drawString(levelString, levelX, levelY - 1, 0);
        FONT_RENDERER.drawString(levelString, levelX, levelY, color.getRGB());

        GlStateManager.disableLighting();
    }
}
