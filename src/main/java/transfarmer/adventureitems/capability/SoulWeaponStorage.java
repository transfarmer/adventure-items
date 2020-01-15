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
        String weaponType = instance.getCurrentType() == null ? "null" : instance.getCurrentType().toString();
        tag.putString("adventureitems.weaponType", weaponType);
        return tag;
    }

    @Override
    public void readNBT(Capability<ISoulWeapon> capability, ISoulWeapon instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        instance.setCurrentType(SoulWeapon.WeaponType.get(tag.getString("adventureitems.weaponType")));
    }
}
