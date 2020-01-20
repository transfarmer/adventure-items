package transfarmer.adventureitems;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transfarmer.adventureitems.network.ClientWeaponData;
import transfarmer.adventureitems.network.ClientWeaponLevelup;
import transfarmer.adventureitems.network.ClientWeaponType;
import transfarmer.adventureitems.network.ServerWeaponLevelup;
import transfarmer.adventureitems.network.ServerWeaponType;

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
        CHANNEL.registerMessage(id++,
                ServerWeaponType.class,
                ServerWeaponType::encode,
                ServerWeaponType::new,
                ServerWeaponType::handle
        );

        CHANNEL.registerMessage(id++,
                ClientWeaponType.class,
                ClientWeaponType::encode,
                ClientWeaponType::new,
                ClientWeaponType::handle
        );

        CHANNEL.registerMessage(id++,
                ServerWeaponLevelup.class,
                ServerWeaponLevelup::encode,
                ServerWeaponLevelup::new,
                ServerWeaponLevelup::handle
        );

        CHANNEL.registerMessage(id++,
                ClientWeaponLevelup.class,
                ClientWeaponLevelup::encode,
                ClientWeaponLevelup::new,
                ClientWeaponLevelup::handle
        );

        CHANNEL.registerMessage(id++,
                ClientWeaponData.class,
                ClientWeaponData::encode,
                ClientWeaponData::new,
                ClientWeaponData::handle
        );
    }
}
