package transfarmer.soulboundarmory.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.List;

public abstract class ExtendedConfiguration extends Configuration {
    public ExtendedConfiguration(final File file) {
        super(file);
    }

    public ExtendedConfiguration(final File file, final String version) {
        super(file, version);
    }

    abstract List<Property> getProperties();
}
