package transfarmer.soulboundarmory.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import nerdhub.cardinal.components.api.component.Component;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import transfarmer.farmerlib.image.ImageUtil;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.MainClient;
import transfarmer.soulboundarmory.client.gui.screen.common.ExtendedScreen;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.client.renderer.texture.ExperienceBarTexture;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.SoulboundItem;
import transfarmer.soulboundarmory.statistics.StatisticType;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static transfarmer.soulboundarmory.MainClient.CLIENT;
import static transfarmer.soulboundarmory.statistics.StatisticType.LEVEL;

@Environment(EnvType.CLIENT)
public class XPBarGUI extends ExtendedScreen {
    protected static final Identifier ICONS = new Identifier(Main.MOD_ID, "textures/gui/icons");

    protected ItemStack itemStack;
    protected ISoulboundItemComponent<? extends Component> component;
    protected int row;
    protected int length;

    static {
        TEXTURE_MANAGER.registerTexture(ICONS, new ExperienceBarTexture(getTexture()));
    }

    public XPBarGUI() {
        super(null);
    }

    public XPBarGUI(final ItemStack itemStack) {
        this();

        this.update(itemStack);
    }

    public XPBarGUI(final ISoulboundItemComponent<? extends Component> component) {
        this();

        this.update(component);

        this.component = component;
    }

    public void setData(final int row, final int length) {
        this.row = row;
        this.length = length;
    }

    public boolean drawXPBar() {
        final PlayerEntity player = MainClient.PLAYER;
        final Window window = CLIENT.getWindow();

        for (final ItemStack itemStack : player.getItemsHand()) {
            if (this.update(ISoulboundItemComponent.get(itemStack))) {
                this.drawXPBar((window.getScaledWidth() - 182) / 2, window.getScaledHeight() - 29, 182);

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
        if (this.update(ISoulboundItemComponent.get(itemStack))) {
            if (itemStack.getItem() instanceof SoulboundItem && this.itemStack != itemStack) {
                this.itemStack = itemStack;
            }
        }

        return false;
    }

    public boolean update(final ISoulboundItemComponent<? extends Component> component) {
        if (component != null) {
            this.component = component;
        }

        return this.component != null;
    }

    public void drawXPBar(final int x, final int y, final int width) {
        if (MainConfig.instance().colors.alpha >= 26) {
            final Color color = new Color(MainConfig.instance().colors.red, MainConfig.instance().colors.green, MainConfig.instance().colors.blue, MainConfig.instance().colors.alpha);
            final float[] components = color.getComponents(null);
            final Style style = MainConfig.instance().style;
            final float ratio = (float) this.component.getDatum(StatisticType.XP) / this.component.getNextLevelXP();
            final float effectiveWidth = ratio * width;
            final int middleU = (int) Math.min(4, effectiveWidth);

            TEXTURE_MANAGER.bindTexture(ICONS);
            RenderSystem.color4f(components[0], components[1], components[2], components[3]);

            this.drawHorizontalInterpolatedTexturedRect(x, y, 0, style.v, 4, 177, 182, width, 5);
            this.drawHorizontalInterpolatedTexturedRect(x, y, 0, style.v + 5, middleU, effectiveWidth < 4 ? middleU : (int) (ratio * 177), (int) (ratio * 182), this.component.canLevelUp()
                    ? Math.min(width, (int) (ratio * width))
                    : width, 5);

            final int level = this.component.getDatum(LEVEL);

            if (level > 0) {
                final String levelString = String.format("%d", level);
                final int levelX = x + (width - TEXT_RENDERER.getStringWidth(levelString)) / 2;
                final int levelY = y - 6;

                TEXT_RENDERER.draw(levelString, levelX + 1, levelY, 0);
                TEXT_RENDERER.draw(levelString, levelX - 1, levelY, 0);
                TEXT_RENDERER.draw(levelString, levelX, levelY + 1, 0);
                TEXT_RENDERER.draw(levelString, levelX, levelY - 1, 0);
                TEXT_RENDERER.draw(levelString, levelX, levelY, color.getRGB());
            }

            RenderSystem.disableLighting();
        }
    }

    public static NativeImage getTexture() {
        final BufferedImage image = ImageUtil.readTexture(GUI_ICONS_LOCATION);
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

                        pixel[0] = pixel[1] = pixel[2] = (int) (multipliers[bar] * Math.round(Math.sqrt(0.299F * pixel[0] * pixel[0] + 0.587F * pixel[1] * pixel[1] + 0.114F * pixel[2] * pixel[2])));
                        raster.setPixel(u, v, pixel);
                    }
                }
            }
        }

        return ImageUtil.toNativeImage(raster);
    }

    @Environment(EnvType.CLIENT)
    public enum Style {
        EXPERIENCE(64, Mappings.EXPERIENCE),
        BOSS(74, Mappings.BOSS),
        HORSE(84, Mappings.HORSE);

        public static final List<Style> STYLES = new ArrayList<>(Arrays.asList(Style.values()));
        public static final int AMOUNT = STYLES.size();

        public final int v;
        private final Text text;

        Style(final int v, final Text text) {
            this.v = v;
            this.text = text;
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
            return this.text.asFormattedString();
        }
    }
}
