package transfarmer.adventureitems;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transfarmer.adventureitems.network.ApplyWeaponType;
import transfarmer.adventureitems.network.RequestWeaponType;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "adventureitems";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Main.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    public static int id;

    public Main() {
        CHANNEL.registerMessage(
                id++,
                RequestWeaponType.class,
                RequestWeaponType::encode,
                RequestWeaponType::new,
                RequestWeaponType::handle
        );

        CHANNEL.registerMessage(
                id++,
                ApplyWeaponType.class,
                ApplyWeaponType::encode,
                ApplyWeaponType::new,
                ApplyWeaponType::handle
        );
    }
}
