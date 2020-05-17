package transfarmer.soulboundarmory.mixin.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
//    @Inject(method = "getReachDistance", at = @At("RETURN"), cancellable = true)
//    public void getReachDistance(final CallbackInfoReturnable<Float> info) {
//        final PlayerEntity player = CLIENT.player;
//        final ISoulboundItemComponent component = SoulboundItemUtil.getCurrentComponent(player);
//
//        if (component != null) {
//            info.setReturnValue((float) component.getAttribute(component.getItemType(), REACH));
//        }
//    }
}
