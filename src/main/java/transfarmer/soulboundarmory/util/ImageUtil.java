package transfarmer.soulboundarmory.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.Main;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SuppressWarnings("ConstantConditions")
@SideOnly(CLIENT)
public class ImageUtil {
    public static final IResourceManager RESOURCE_MANAGER = Minecraft.getMinecraft().getResourceManager();

    @Nonnull
    public static BufferedImage readTexture(final ResourceLocation location) {
        try {
            return ImageIO.read(RESOURCE_MANAGER.getResource(location).getInputStream());
        } catch (final IOException exception) {
            Main.LOGGER.error(exception);
        }

        return null;
    }

    public static int[][][] getPixels(final BufferedImage image) {
        return getPixels(image, 0, 0, image.getWidth(), image.getHeight());
    }

    public static int[][][] getPixels(final BufferedImage image, final int u, final int v, final int width, final int height) {
        final int[][][] pixels = new int[image.getHeight()][image.getWidth()][4];
        final Raster raster = image.getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = raster.getPixel(u + x, v + y, (int[]) null);
            }
        }

        return pixels;
    }

    public static int[] getRGB(int color) {
        final int[] rgb = new int[3];

        rgb[0] = color / 0xFF0000;
        rgb[1] = (color %= 0xFF0000) / 0xFF00;
        rgb[2] = color % 0xFF00 / 0xFF;

        return rgb;
    }
}
