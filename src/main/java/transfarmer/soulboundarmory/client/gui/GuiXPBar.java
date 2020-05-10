package transfarmer.soulboundarmory.client.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundItemUtil;
import transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiScreenExtended;
import transfarmer.soulboundarmory.client.renderer.texture.ExperienceBarTexture;
import transfarmer.soulboundarmory.config.ClientConfig;
import transfarmer.soulboundarmory.item.ItemSoulbound;
import transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;

@SideOnly(CLIENT)
public class GuiXPBar extends GuiScreenExtended {
    protected static final ResourceLocation ICONS = new ResourceLocation(Main.MOD_ID, "textures/gui/icons");

    protected ItemStack itemStack;
    protected SoulboundCapability capability;
    protected IItem itemType;
    protected int row;
    protected int length;

    static {
        TEXTURE_MANAGER.loadTexture(ICONS, new ExperienceBarTexture(getTexture()));
    }

    public GuiXPBar() {
    }

    public GuiXPBar(final ItemStack itemStack) {
        this();

        this.update(itemStack);
    }

    public GuiXPBar(final SoulboundCapability capability) {
        this();

        this.update(capability);

        this.itemType = this.capability.getItemType();
    }

    public void setData(final int row, final int length) {
        this.row = row;
        this.length = length;
    }

    public boolean drawXPBar(final ScaledResolution resolution) {
        final EntityPlayer player = MINECRAFT.player;
        final ItemStack itemStack = player.getHeldItemMainhand();

        if (this.update(SoulboundItemUtil.getFirstCapability(player, itemStack))) {
            this.itemType = this.capability.getItemType(itemStack);

            if (this.itemType == null) {
                final int slot = player.inventory.currentItem;
                SoulboundCapability capability;

                if ((capability = WeaponProvider.get(player)).getBoundSlot() == slot
                        || (capability = ToolProvider.get(player)).getBoundSlot() == slot) {
                    this.capability = capability;
                    this.itemType = capability.getItemType();
                }
            }

            if (this.itemType != null) {
                this.drawXPBar((resolution.getScaledWidth() - 182) / 2, resolution.getScaledHeight() - 29, 182);

                return true;
            }
        }

        return false;
    }

    public void drawTooltip(final int tooltipX, final int tooltipY, final ItemStack itemStack) {
        if (this.update(itemStack)) {
            final int x = tooltipX + 4;
            final int y = tooltipY + this.row * 10;

            this.drawXPBar(x, y, this.length);
        }
    }

    public boolean update(final ItemStack itemStack) {
        if (this.update(SoulboundItemUtil.getFirstCapability(MINECRAFT.player, itemStack))) {
            if (itemStack.getItem() instanceof ItemSoulbound && this.itemStack != itemStack) {
                this.itemStack = itemStack;
                this.itemType = this.capability.getItemType(itemStack);
            }

            return this.itemType != null;
        }

        return false;
    }

    public boolean update(final SoulboundCapability capability) {
        if (capability != null) {
            this.capability = capability;
        } else {
            this.itemType = null;
        }

        return this.capability != null;
    }

    public void drawXPBar(final int x, final int y, final int length) {
        if (ClientConfig.getAlpha() >= 26F / 255F) {
//            final ColorSpace yCbCr = ColorSpace.getInstance(ColorSpace.CS_PYCC);
//            Color color = new Color(yCbCr, yCbCr.fromRGB(
//                    new float[]{ClientConfig.getRed(), ClientConfig.getGreen(), ClientConfig.getBlue(), ClientConfig.getAlpha()}
//            ), ClientConfig.getAlpha());
            final Color color = new Color(ClientConfig.getRed(), ClientConfig.getGreen(), ClientConfig.getBlue(), ClientConfig.getAlpha());
            final Style style = ClientConfig.getStyle();
            final float ratio = (float) this.capability.getDatum(this.itemType, StatisticType.XP) / this.capability.getNextLevelXP(this.itemType);
            final float effectiveLength = ratio * length;
            final int middleU = (int) Math.min(4, effectiveLength);

            TEXTURE_MANAGER.bindTexture(ICONS);
            GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);

            this.drawHorizontalInterpolatedTexturedRect(x, y, 0, style.v, 4, 177, 182, length, 5);
            this.drawHorizontalInterpolatedTexturedRect(x, y, 0, style.v + 5, middleU, effectiveLength < 4 ? middleU : (int) (ratio * 177), (int) (ratio * 182), this.capability.canLevelUp(this.itemType)
                    ? Math.min(length, (int) (ratio * length))
                    : length, 5
            );

            final int level = this.capability.getDatum(this.itemType, LEVEL);

            if (level > 0) {
                final String levelString = String.format("%d", level);
                final int levelX = x + (length - FONT_RENDERER.getStringWidth(levelString)) / 2;
                final int levelY = y - 6;

                FONT_RENDERER.drawString(levelString, levelX + 1, levelY, 0);
                FONT_RENDERER.drawString(levelString, levelX - 1, levelY, 0);
                FONT_RENDERER.drawString(levelString, levelX, levelY + 1, 0);
                FONT_RENDERER.drawString(levelString, levelX, levelY - 1, 0);
                FONT_RENDERER.drawString(levelString, levelX, levelY, color.getRGB());
            }

            GlStateManager.disableLighting();
        }
    }

    public static BufferedImage getTexture() {
        final BufferedImage image = ImageUtil.readTexture(Gui.ICONS);
        final WritableRaster raster = image.getRaster();
        final List<Style> styles = Style.STYLES;
        final int amount = Style.AMOUNT;
        float[] multipliers = new float[amount];
        final float scale = image.getWidth() / 256F;
        final int scaledWidth = (int) (182 * scale);
        final int scaledHeight = (int) (5 * scale);
        Arrays.fill(multipliers, Float.MAX_VALUE);

        for (int bar = 0; bar < amount; bar++) {
            int v = styles.get(bar).v;

            for (int state = 0; state < 2; state++) {
                v += state * 5;

                for (final int[][] row : ImageUtil.getPixels(image, 0, (int) (v * scale), scaledWidth, scaledHeight)) {
                    for (final int[] pixel : row) {
                        final float multiplier = (float) (255D / Math.sqrt(0.299F * pixel[0] * pixel[0] + 0.587F * pixel[1] * pixel[1] + 0.114F * pixel[2] * pixel[2]));

                        if (multiplier < multipliers[bar]) {
                            multipliers[bar] = multiplier;
                        }
                    }
                }
            }
        }

        for (int bar = 0; bar < amount; bar++) {
            int y = styles.get(bar).v;

            for (int state = 0; state < 2; state++) {
                y += state * 5;

                for (int row = 0; row < scaledHeight; row++) {
                    final int v = (int) (y * scale) + row;

                    for (int u = 0; u < scaledWidth; u++) {
                        final int[] pixel = raster.getPixel(u, v, (int[]) null);
                        final int mean = (int) (multipliers[bar] * Math.round(Math.sqrt(0.299F * pixel[0] * pixel[0] + 0.587F * pixel[1] * pixel[1] + 0.114F * pixel[2] * pixel[2])));

                        pixel[0] = mean;
                        pixel[1] = mean;
                        pixel[2] = mean;

                        raster.setPixel(u, v, pixel);
                    }
                }
            }
        }

        return new BufferedImage(image.getColorModel(), raster, image.isAlphaPremultiplied(), null);
    }

    @SideOnly(CLIENT)
    public enum Style {
        EXPERIENCE(64, "gui.soulboundarmory.experience"),
        BOSS(74, "gui.soulboundarmory.boss"),
        HORSE(84, "entity.Horse.name");

        public static final List<Style> STYLES = new ArrayList<>(Arrays.asList(Style.values()));
        public static final int AMOUNT = STYLES.size();

        public final int v;
        private final String name;

        Style(final int v, final String key) {
            this.v = v;
            this.name = I18n.format(key).toLowerCase();
        }

        public static Style get(final String name) {
            try {
                return valueOf(name);
            } catch (final IllegalArgumentException exception) {
                return EXPERIENCE;
            }
        }

        public static int indexOf(final String name) {
            return STYLES.indexOf(Style.get(name));
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
