package transfarmer.soulboundarmory.world.loot;

import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import transfarmer.soulboundarmory.Main;

import static transfarmer.soulboundarmory.init.ModItems.SOULBOUND_STAFF;

@EventBusSubscriber(modid = Main.MOD_ID)
public class LootTables {
    @SubscribeEvent
    public static void onLootTableLoad(final LootTableLoadEvent event) {
        if (event.getName().toString().equals("minecraft:chests/nether_bridge")) {
            final LootCondition[] conditions = {new RandomChance(0.1F)};
            final RandomValueRange range = new RandomValueRange(1);
            final LootFunction[] functions = {new SetCount(conditions, range)};
            final LootEntry[] entries = new LootEntryItem[]{new LootEntryItem(SOULBOUND_STAFF, 1, 0, functions, conditions, "soulbound_staff")};
            final LootPool pool = new LootPool(entries, conditions, range, new RandomValueRange(0), "soulbound_staff");

            event.getTable().addPool(pool);
        }
    }
}
