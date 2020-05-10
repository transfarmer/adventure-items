package transfarmer.soulboundarmory.config;

import net.minecraftforge.common.config.Property;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public abstract class Config {
    protected final ExtendedConfiguration configFile;

    public Config(final ExtendedConfiguration configFile) {
        this.configFile = configFile;
    }

    public abstract void load();

    public abstract void save();

    public void cleanUp() {
        try {
            final FileInputStream inputStream = new FileInputStream(this.configFile.getConfigFile());
            final StringBuilder builder = new StringBuilder();

            while (inputStream.available() > 0) {
                builder.append((char) inputStream.read());
            }

            inputStream.close();

            final List<String> lines = CollectionUtil.arrayList(builder.toString().split("\n"));

            for (final String category : this.configFile.getCategoryNames()) {
                for (final Property property : this.configFile.getCategory(category).getOrderedValues()) {
                    if (!this.configFile.getProperties().contains(property)) {
                        lines.removeIf(line -> line != null && line.contains(property.getName()));
                    }
                }
            }

            final FileOutputStream outputStream = new FileOutputStream(this.configFile.getConfigFile());

            for (final String line : lines) {
                for (final char character : line.toCharArray()) {
                    outputStream.write(character);
                }

                outputStream.write('\n');
            }

            outputStream.flush();
            outputStream.close();
        } catch (final IOException exception) {
            Main.LOGGER.error(exception);
        }
    }

    public void update() {
        final String loadedVersion = this.configFile.getLoadedConfigVersion();

        if (loadedVersion == null || !loadedVersion.equals(this.configFile.getDefinedConfigVersion())) {
            if (this.configFile.getConfigFile().delete()) {
                this.load();

                Main.LOGGER.warn("Deleted old configuration file.");
            }
        } else {
            this.cleanUp();
        }
    }
}
