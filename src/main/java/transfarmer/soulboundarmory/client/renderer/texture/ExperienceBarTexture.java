package transfarmer.soulboundarmory.client.renderer.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

@Environment(EnvType.CLIENT)
public class ExperienceBarTexture extends NativeImageBackedTexture {
    public ExperienceBarTexture(final NativeImage image) {
        super(image);
    }
}
