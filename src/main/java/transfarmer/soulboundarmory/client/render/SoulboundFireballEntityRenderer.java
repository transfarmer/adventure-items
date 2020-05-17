package transfarmer.soulboundarmory.client.render;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry.Context;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.util.Identifier;
import transfarmer.soulboundarmory.entity.SoulboundFireballEntity;

public class SoulboundFireballEntityRenderer extends EntityRenderer<SoulboundFireballEntity> {
    public SoulboundFireballEntityRenderer(final EntityRenderDispatcher dispatcher, final Context context) {
        super(dispatcher);
    }

    @Override
    public Identifier getTexture(final SoulboundFireballEntity entity) {
        return new Identifier("minecraft:item/fire_charge.png");
    }
}
