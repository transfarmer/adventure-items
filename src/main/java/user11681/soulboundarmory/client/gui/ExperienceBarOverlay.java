package user11681.soulboundarmory.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import user11681.soulboundarmory.MainClient;
import user11681.soulboundarmory.client.i18n.Mappings;
import user11681.soulboundarmory.client.texture.ExperienceBarTexture;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.usersmanual.client.gui.screen.ExtendedScreen;

import static user11681.soulboundarmory.MainClient.CLIENT;
import static user11681.soulboundarmory.component.statistics.StatisticType.EXPERIENCE;
import static user11681.soulboundarmory.component.statistics.StatisticType.LEVEL;

@Environment(EnvType.CLIENT)
public class ExperienceBarOverlay extends ExtendedScreen {
    public static NativeImageBackedTexture TEXTURE = new ExperienceBarTexture(256, 256);

    protected ItemStack itemStack;

    protected ItemStorage<?> component;
    protected int row;
    protected int length;

    public ExperienceBarOverlay(final ItemStack itemStack) {
        this();

        this.update(itemStack);
    }

    public ExperienceBarOverlay(final ItemStorage<?> component) {
        this();

        this.update(component);

        this.component = component;
    }

    public ExperienceBarOverlay() {
        super(null);
    }

    public void setData(final int row, final int length) {
        this.row = row;
        this.length = length;
    }

    public void drawTooltip(final int tooltipX, final int tooltipY, final ItemStack itemStack) {
        if (this.update(itemStack)) {
            final int x = tooltipX + 4;
            final int y = tooltipY + this.row * 10;

            this.draw(x, y, this.length);
        }
    }

    public boolean update(final ItemStack itemStack) {
        if (this.update(ItemStorage.get(MainClient.getPlayer(), itemStack.getItem()))) {
            if (itemStack.getItem() instanceof SoulboundItem && this.itemStack != itemStack) {
                this.itemStack = itemStack;
            }
        }

        return false;
    }

    public boolean update(final ItemStorage<?> component) {
        if (component != null) {
            this.component = component;
        }

        return this.component != null;
    }

    public boolean draw() {
        final PlayerEntity player = MainClient.getPlayer();
        final Window window = CLIENT.getWindow();

        for (final ItemStack itemStack : player.getItemsHand()) {
            if (this.update(ItemStorage.get(player, itemStack.getItem()))) {
                this.draw((window.getScaledWidth() - 182) / 2, window.getScaledHeight() - 29, 182);

                return true;
            }
        }

        return false;
    }

    public void draw(final int x, final int y, final int width) {
        final Configuration.Client configuration = Configuration.instance().client;

        if (configuration.colors.alpha >= 26) {
            final Color color = new Color(configuration.colors.red, configuration.colors.green, configuration.colors.blue, configuration.colors.alpha);
            final float[] components = color.getComponents(null);
            Style style = configuration.style;

            if (style == null) {
                style = Style.EXPERIENCE;
            }

            final float ratio = (float) this.component.getDatum(EXPERIENCE) / this.component.getNextLevelXP();
            final float effectiveWidth = ratio * width;
            final int middleU = (int) Math.min(4, effectiveWidth);

            TEXTURE.bindTexture();
            RenderSystem.color4f(components[0], components[1], components[2], components[3]);

            this.blitHorizontallyInterpolated(x, y, 0, style.v, 4, 177, 182, width, 5);
            this.blitHorizontallyInterpolated(x, y, 0, style.v + 5, middleU, effectiveWidth < 4 ? middleU : (int) (ratio * 177), (int) (ratio * 182), this.component.canLevelUp()
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

    public enum Style {
        EXPERIENCE(64, Mappings.EXPERIENCE_STYLE),
        BOSS(74, Mappings.BOSS_STYLE),
        HORSE(84, Mappings.HORSE_STYLE);

        public static final List<Style> STYLES = new ArrayList<>(Arrays.asList(Style.values()));
        public static final int AMOUNT = STYLES.size();

        public final int v;
        protected final Text text;

        Style(final int v, final Text text) {
            this.v = v;
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text.asFormattedString();
        }
    }
}
