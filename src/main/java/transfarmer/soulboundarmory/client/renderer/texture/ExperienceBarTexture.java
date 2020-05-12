package transfarmer.soulboundarmory.client.renderer.texture;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.client.gui.GuiXPBar;

import java.awt.image.BufferedImage;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@Environment(CLIENT)
public class ExperienceBarTexture extends AbstractTexture {
    protected int[] dynamicTextureData;
    protected int width;
    protected int height;

    public ExperienceBarTexture(final BufferedImage image) {
        this.updateDynamicTexture(image);
    }

    @Override
    public void loadTexture(@NotNull final IResourceManager resourceManager) {
        this.updateDynamicTexture(GuiXPBar.getTexture());
    }

    public void updateDynamicTexture(final BufferedImage image) {
        final int id = this.getGlTextureId();

        this.width = image.getWidth();
        this.height = image.getHeight();

        TextureUtil.allocateTexture(id, this.width, this.height);

        this.dynamicTextureData = image.getRGB(0, 0, this.width, this.height, null, 0, this.width);

        TextureUtil.uploadTexture(id, this.dynamicTextureData, this.width, this.height);
    }
}
