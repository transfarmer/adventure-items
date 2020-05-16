package transfarmer.soulboundarmory.mixin.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.component.soulbound.common.SoulboundItemUtil;

import static transfarmer.soulboundarmory.MainClient.CLIENT;
import static transfarmer.soulboundarmory.statistics.StatisticType.REACH;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
    @Inject(method = "getReachDistance", at = @At("RETURN"), cancellable = true)
    public void getReachDistance(final CallbackInfoReturnable<Float> info) {
        final PlayerEntity player = CLIENT.player;
        final ISoulboundComponent component = SoulboundItemUtil.getCurrentComponent(player);

        if (component != null) {
            info.setReturnValue((float) component.getAttribute(component.getItemType(), REACH));
        }
    }
}
