package user11681.soulboundarmory.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry.Context;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.entity.SoulboundDaggerEntity;

import javax.annotation.Nullable;

import static user11681.soulboundarmory.Main.MOD_ID;

@Environment(EnvType.CLIENT)
public class SoulboundDaggerEntityRenderer extends ProjectileEntityRenderer<SoulboundDaggerEntity> {
    public static final Identifier THROWN_SOULBOUND_DAGGER = new Identifier(MOD_ID, "textures/item/soulbound_dagger.png");

    public SoulboundDaggerEntityRenderer(final EntityRenderDispatcher dispatcher, final Context context) {
        super(dispatcher);
    }

    @Nullable
    @Override
    public Identifier getTexture(final SoulboundDaggerEntity entity) {
        return THROWN_SOULBOUND_DAGGER;
    }
}
