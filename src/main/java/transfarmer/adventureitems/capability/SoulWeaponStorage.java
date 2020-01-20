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
        instance.setBigswordAttributes(tag.getIntArray("adventureitems.soulweapon.bigsword"));
        instance.setSwordAttributes(tag.getIntArray("adventureitems.soulweapon.sword"));
        instance.setDaggerAttributes(tag.getIntArray("adventureitems.soulweapon.dagger"));
    }
}
