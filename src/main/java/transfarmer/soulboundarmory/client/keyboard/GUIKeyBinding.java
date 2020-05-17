package transfarmer.soulboundarmory.client.keyboard;

import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.util.Identifier;
import transfarmer.soulboundarmory.MainClient;
import transfarmer.soulboundarmory.component.soulbound.common.IPlayerSoulboundComponent;
import transfarmer.soulboundarmory.component.soulbound.item.ISoulboundItemComponent;

public class GUIKeyBinding extends ExtendedKeyBinding {
    public GUIKeyBinding(final Identifier identifier, final Type type, final int code, final String category) {
        super(identifier, type, code, category);
    }

    @Override
    protected void onPress() {
        final ISoulboundItemComponent<? extends Component> component = IPlayerSoulboundComponent.get(MainClient.CLIENT.player).getHeldItemComponent();

        if (component != null) {
            component.openGUI();
        }
    }
}
