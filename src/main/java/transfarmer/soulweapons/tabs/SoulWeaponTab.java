package transfarmer.soulweapons.tabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.init.ModItems;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;


public class SoulWeaponTab extends CreativeTabs {
    public SoulWeaponTab() {
        super(Main.MODID);
    }

    @SideOnly(CLIENT)
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.SOUL_BIGSWORD);
    }
}