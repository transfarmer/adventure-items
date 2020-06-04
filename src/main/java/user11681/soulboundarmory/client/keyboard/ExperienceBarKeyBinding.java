package user11681.soulboundarmory.client.keyboard;

import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.config.Configuration;
import user11681.usersmanual.client.keyboard.ModKeyBinding;

public class ExperienceBarKeyBinding extends ModKeyBinding {
    public ExperienceBarKeyBinding(final Identifier identifier, final Type type, final int code, final String name) {
        super(identifier, type, code, name);
    }

    @Override
    protected void onPress() {
        final Configuration.Client client = Configuration.instance().client;

        client.toggleOverlayExperienceBar();
    }
}
