package transfarmer.adventureitems.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import transfarmer.adventureitems.ModItemTier;
import transfarmer.adventureitems.init.ModItemGroups;


public class SoulWeapon extends SwordItem {
    public SoulWeapon(int attackDamage, float attackSpeed) {
        super(ModItemTier.SOUL, attackDamage, attackSpeed, new Item.Properties().group(ItemGroup.COMBAT).group(ModItemGroups.MOD_ITEM_GROUP));
    }

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent event) {
    }
}
