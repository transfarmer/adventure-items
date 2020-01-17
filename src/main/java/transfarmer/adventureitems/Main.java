package transfarmer.adventureitems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

import static transfarmer.adventureitems.init.ModItems.SOUL_BIGSWORD;
import static transfarmer.adventureitems.init.ModItems.SOUL_SWORD;
import static transfarmer.adventureitems.init.ModItems.SOUL_DAGGER;


@Mod(Main.MODID)
public class Main {
    public static final String MODID = "adventureitems";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final HashSet<Item> items = new HashSet(3, 1);
    static {
        items.add(SOUL_BIGSWORD);
        items.add(SOUL_SWORD);
        items.add(SOUL_DAGGER);
    }

    public Main() {
    }

    public boolean isSoulWeaponPresent(PlayerEntity player) {
        return !player.inventory.hasAny(items);
    }
}
