package user11681.soulboundarmory.client.texture;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.ResourceManager;
import user11681.soulboundarmory.client.gui.ExperienceBarOverlay;
import user11681.usersmanual.image.ImageUtil;

import static net.minecraft.client.gui.DrawableHelper.GUI_ICONS_LOCATION;

public class ExperienceBarTexture extends NativeImageBackedTexture {
    public ExperienceBarTexture(final int width, final int height) {
        super(width, height, true);

        MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("soulboundarmory/gui/experience_bar", this);
    }

    @Override
    public void load(final ResourceManager manager) throws IOException {
        super.load(manager);

        final BufferedImage image = ImageUtil.readTexture(GUI_ICONS_LOCATION);
        final WritableRaster raster = image.getRaster();
        final List<ExperienceBarOverlay.Style> styles = ExperienceBarOverlay.Style.STYLES;
        final int amount = ExperienceBarOverlay.Style.AMOUNT;
        final float scale = image.getWidth() / 256F;
        final int scaledWidth = (int) (182 * scale);
        final int scaledHeight = (int) (5 * scale);
        float[] multipliers = new float[amount];

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

        final NativeImage nativeImage = this.getImage();

        for (int bar = 0; bar < amount; bar++) {
            int y = styles.get(bar).v;

            for (int state = 0; state < 2; state++) {
                y += state * 5;

                for (int row = 0; row < scaledHeight; row++) {
                    final int v = (int) (y * scale) + row;

                    for (int u = 0; u < scaledWidth; u++) {
                        final int[] pixel = raster.getPixel(u, v, (int[]) null);
                        final int color = (int) (multipliers[bar] * Math.round(Math.sqrt(0.299F * pixel[0] * pixel[0] + 0.587F * pixel[1] * pixel[1] + 0.114F * pixel[2] * pixel[2])));

                        assert nativeImage != null;
                        nativeImage.setPixelRgba(u, v, new Color(color, color, color).getRGB());
                    }
                }
            }
        }
    }
}
