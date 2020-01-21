package transfarmer.adventureitems.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class SoulWeaponStorage implements IStorage<ISoulWeapon> {
    @Override
    public INBT writeNBT(Capability<ISoulWeapon> capability, ISoulWeapon instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("adventureitems.soulweapon.index", instance.getCurrentTypeIndex());
        tag.putIntArray("adventureitems.soulweapon.bigsword", instance.getBigswordAttributes());
        tag.putIntArray("adventureitems.soulweapon.sword", instance.getSwordAttributes());
        tag.putIntArray("adventureitems.soulweapon.dagger", instance.getDaggerAttributes());
        return tag;
    }

    @Override
    public void readNBT(Capability<ISoulWeapon> capability, ISoulWeapon instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        instance.setCurrentTypeIndex(tag.getInt("adventureitems.soulweapon.index"));
        int[] bigsword = tag.getIntArray("adventureitems.soulweapon.bigsword");
        int[] sword = tag.getIntArray("adventureitems.soulweapon.sword");
        int[] dagger = tag.getIntArray("adventureitems.soulweapon.dagger");

        if (bigsword.length == 0 || sword.length == 0 || dagger.length == 0) {
            instance.setAttributes(new int[8], new int[8], new int[8]);
            return;
        }

        instance.setAttributes(bigsword, sword, dagger);
    }
}
