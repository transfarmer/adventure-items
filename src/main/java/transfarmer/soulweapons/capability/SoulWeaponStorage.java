package transfarmer.soulweapons.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import transfarmer.soulweapons.SoulWeaponAttribute;

public class SoulWeaponStorage implements IStorage<ISoulWeapon> {
    @Override
    public NBTBase writeNBT(Capability<ISoulWeapon> capability, ISoulWeapon instance, EnumFacing facing) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("soulweapons.soulweapon.index", instance.getIndex());
        int[][] attributes = instance.getAttributes();

        for (int weaponTypeIndex = 0; weaponTypeIndex <= 2; weaponTypeIndex++) {
            for (int valueIndex = 0; valueIndex <= 8; valueIndex++) {
                tag.setInteger(String.format("soulweapons.soulweapon.%s.%s",
                    instance.getWeaponName(weaponTypeIndex), SoulWeaponAttribute.getName(valueIndex)),
                    attributes[weaponTypeIndex][valueIndex]);
            }
        }

        return tag;
    }

    @Override
    public void readNBT(Capability<ISoulWeapon> capability, ISoulWeapon instance, EnumFacing facing, NBTBase nbt) {
        NBTTagCompound tag = (NBTTagCompound) nbt;
        instance.setCurrentType(tag.getInteger("soulweapons.soulweapon.index"));
        int[][] attributes = new int[3][9];

        for (int weaponTypeIndex = 0; weaponTypeIndex <= 2; weaponTypeIndex++) {
            for (int valueIndex = 0; valueIndex <= 8; valueIndex++) {
                attributes[weaponTypeIndex][valueIndex] = tag.getInteger(String.format("soulweapons.soulweapon.%s.%s",
                    instance.getWeaponName(weaponTypeIndex), SoulWeaponAttribute.getName(valueIndex)));
            }
        }

        instance.setAttributes(attributes);
    }
}
