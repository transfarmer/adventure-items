package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulItemHelper;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiExtended;
import transfarmer.soulboundarmory.config.ClientConfig;
import transfarmer.soulboundarmory.item.ISoulboundItem;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import java.awt.*;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.Main.MOD_ID;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

@SideOnly(CLIENT)
public class GuiXPBar extends Gui implements GuiExtended {
    public static final ResourceLocation XP_BAR = new ResourceLocation(MOD_ID, "textures/gui/xp_bar.png");

    protected static GuiXPBar instance;
    protected static int[] data = new int[2];

    protected ItemStack itemStack;
    protected SoulboundCapability capability;
    protected IItem item;

    public GuiXPBar(final ItemStack itemStack) {
        this(SoulItemHelper.getFirstCapability(MINECRAFT.player, itemStack.getItem()));

        this.itemStack = itemStack;

        if (this.capability != null) {
            this.item = this.capability.getItemType(itemStack);
        }
    }

    public GuiXPBar(final SoulboundCapability capability) {
        if (capability != null) {
            this.capability = capability;
            this.item = capability.getItemType();
        }
    }

    public GuiXPBar() {

    }

    public static void setData(final int row, final int length) {
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
        final float effectiveLength = ratio * length;
        final int middleU = (int) Math.min(4, effectiveLength);
        final Color color = new Color(ClientConfig.getRed(), ClientConfig.getGreen(), ClientConfig.getBlue(), ClientConfig.getAlpha());

        GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);

        // debug
        final long time = System.nanoTime();
        TEXTURE_MANAGER.bindTexture(XP_BAR);
        Main.LOGGER.warn((System.nanoTime() - time) / 1000000F); // 4 ms

        GuiExtended.drawHorizontalInterpolatedTexturedRect(x, y, 0, 0, 4, 177, 182, length, 5);
        GuiExtended.drawHorizontalInterpolatedTexturedRect(x, y, 0, 5, middleU, effectiveLength >= 4 ? (int) (ratio * 177) : middleU, (int) (ratio * 182), this.capability.canLevelUp(this.item)
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

    protected void drawEmpty(final int x, final int y, final int length) {
        final int endX = x + length - 1;
        final int endY = y + 4;

        this.drawHorizontalLine(x + 1, endX - 1, y, 0xFF202020);
        this.drawHorizontalLine(x + 3, endX - 3, y + 1, 0xFF484848);
        this.drawHorizontalLine(x + 3, endX - 3, y + 2, 0xFF343434);
        this.drawHorizontalLine(x + 3, endX - 3, endY - 1, 0xFF484848);
        this.drawHorizontalLine(x + 1, endX - 1, endY, 0xFF202020);
        this.drawVerticalLine(x, y, endY, 0xFF202020);
        this.drawVerticalLine(endX, y, endY, 0xFF202020);
        this.drawPixel(x + 1, y + 1, 0xFF585858);
        this.drawPixel(x + 1, y + 2, 0xFF484848);
        this.drawPixel(x + 1, endY - 1, 0xFF585858);
        this.drawPixel(x + 2, y + 1, 0xFF4C4C4C);
        this.drawPixel(x + 2, y + 2, 0xFF3E3E3E);
        this.drawPixel(x + 2, endY - 1, 0xFF4C4C4C);
        this.drawPixel(endX - 1, y + 1, 0xFF585858);
        this.drawPixel(endX - 1, y + 2, 0xFF484848);
        this.drawPixel(endX - 1, endY - 1, 0xFF585858);
        this.drawPixel(endX - 2, y + 1, 0xFF4C4C4C);
        this.drawPixel(endX - 2, y + 2, 0xFF3E3E3E);
        this.drawPixel(endX - 2, endY - 1, 0xFF4C4C4C);
    }

    protected void drawFull(final int x, final int y, final int length, final float proportion) {
        final int endX = x + length - 1;
        final int endY = y + 4;

        this.drawHorizontalLine(x + 2, x + 4, y, 0xFF383838);
        this.drawHorizontalLine(x + 2, x + 4, endY, 0xFF383838);
        this.drawHorizontalLine(x + 2, endX - 2, y + 1, 0xFFFFFFFF);
        this.drawHorizontalLine(x + 2, endX - 5, y + 2, 0xFFC6C6C6);
        this.drawHorizontalLine(x + 2, endX - 2, y + 3, 0xFF949494);
        this.drawHorizontalLine(x + 5, endX - 5, endY, 0xFF484848);
        this.drawHorizontalLine(x + 5, endX - 5, y, 0xFF484848);
        this.drawHorizontalLine(endX - 4, endX - 2, y + 2, 0xFFB8B8B8);
        this.drawHorizontalLine(endX - 5, endX - 3, endY, 0xFF383838);
        this.drawHorizontalLine(endX - 5, endX - 3, y, 0xFF383838);
        this.drawHorizontalLine(endX - 3, endX - 1, endY, 0xFF282828);
        this.drawHorizontalLine(endX - 3, endX - 1, y, 0xFF282828);
        this.drawVerticalLine(x, y, endY, 0xFF282828);
        this.drawVerticalLine(endX, y, endY, 0xFF282828);
        this.drawPixel(x + 1, y, 0xFF282828);
        this.drawPixel(x + 1, endY, 0xFF282828);
        this.drawPixel(x + 1, y + 1, 0xFF727272);
        this.drawPixel(x + 1, y + 2, 0xFF909090);
        this.drawPixel(x + 1, endY - 1, 0xFF727272);
        this.drawPixel(endX - 5, y + 2, 0xFFCACACA);
        this.drawPixel(endX - 1, y + 1, 0xFF727272);
        this.drawPixel(endX - 1, y + 2, 0xFF909090);
        this.drawPixel(endX - 1, endY - 1, 0xFF727272);
    }

    protected void drawPixel(final int x, final int y, final int color) {
        drawRect(x, y, x + 1, y + 1, color);
    }

    public static boolean drawXPBar(final int x, final int y) {
        if (instance.capability != null) {
            instance.drawXPBar(x, y, 182);

            return true;
        }

        return false;
    }

    public static boolean update(final ItemStack itemStack) {
        final Item item = itemStack.getItem();

        if (!(item instanceof ISoulboundItem)) {
            return false;
        }

        if (instance == null || instance.itemStack != itemStack) {
            instance = new GuiXPBar(itemStack);
        }

        return true;
    }
}
