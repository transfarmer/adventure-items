package transfarmer.adventureitems;

import net.minecraftforge.fml.common.Mod;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


@Mod(Main.MODID)
public class Main {
    public static final String MODID = "adventureitems";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public Main() {
        LOGGER.debug(String.format("%s is loaded", MODID));
    }
}
