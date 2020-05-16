package transfarmer.soulboundarmory.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import transfarmer.soulboundarmory.entity.ReachModifierEntity;

import static transfarmer.soulboundarmory.Main.MOD_ID;

@Environment(EnvType.CLIENT)
public class RenderReachModifier extends ProjectileEntityRenderer<ReachModifierEntity> {
    public static final Identifier REACH_MODIFIER = new Identifier(MOD_ID, "textures/entity/reach_modifier.png");

    public RenderReachModifier(final EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public Identifier getTexture(final ReachModifierEntity entity) {
        return REACH_MODIFIER;
    }
}
