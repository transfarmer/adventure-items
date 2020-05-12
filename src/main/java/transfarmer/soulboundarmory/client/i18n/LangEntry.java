package transfarmer.soulboundarmory.client.i18n;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class LangEntry {
    protected final String key;
    protected final String value;

    public LangEntry(final String key) {
        this.key = key;
        this.value = I18n.translate(key);
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
