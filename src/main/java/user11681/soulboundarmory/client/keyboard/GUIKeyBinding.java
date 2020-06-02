package user11681.soulboundarmory.client.keyboard;

import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.MainClient;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundItemUtil;

public class GUIKeyBinding extends ExtendedKeyBinding {
    public GUIKeyBinding(final Identifier identifier, final Type type, final int code, final String category) {
        super(identifier, type, code, category);
    }

    @Override
    protected void onPress() {
        final ItemStorage<?> component = SoulboundItemUtil.getFirstStorage(MainClient.getPlayer());

        if (component != null) {
            component.openGUI();
        }
    }
}
