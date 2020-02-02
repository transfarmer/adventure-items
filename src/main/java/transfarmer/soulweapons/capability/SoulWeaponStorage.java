package transfarmer.soulweapons.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import transfarmer.soulweapons.weapon.SoulWeaponAttribute;
import transfarmer.soulweapons.weapon.SoulWeaponDatum;

public class SoulWeaponStorage implements IStorage<ISoulWeapon> {
    @Override
    public NBTBase writeNBT(Capability<ISoulWeapon> capability, ISoulWeapon instance, EnumFacing facing) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("soulweapons.soulweapon.index", instance.getIndex());
        int[][] data = instance.getData();
        float[][] attributes = instance.getAttributes();

        SoulWeaponHelper.forEachDatumAndAttribute((Integer weaponIndex, Integer valueIndex) -> {
            tag.setInteger(String.format("soulweapons.soulweapon.%s.%s",
                instance.getWeaponName(weaponIndex), SoulWeaponDatum.getName(valueIndex)),
                data[weaponIndex][valueIndex]);
        }, (Integer weaponIndex, Integer valueIndex) -> {
            tag.setFloat(String.format("soulweapons.soulweapon.%s.%s",
                instance.getWeaponName(weaponIndex), SoulWeaponAttribute.getName(valueIndex)),
                attributes[weaponIndex][valueIndex]);
        });

        return tag;
    }

    @Override
    public void readNBT(Capability<ISoulWeapon> capability, ISoulWeapon instance, EnumFacing facing, NBTBase nbt) {
        NBTTagCompound tag = (NBTTagCompound) nbt;
        instance.setCurrentType(tag.getInteger("soulweapons.soulweapon.index"));
        int[][] data = new int[3][4];
        float[][] attributes = new float[3][5];

        SoulWeaponHelper.forEachDatumAndAttribute((Integer weaponIndex, Integer valueIndex) -> {
            data[weaponIndex][valueIndex] = tag.getInteger(String.format("soulweapons.soulweapon.%s.%s",
                instance.getWeaponName(weaponIndex), SoulWeaponDatum.getName(valueIndex)));
        }, (Integer weaponIndex, Integer valueIndex) -> {
            attributes[weaponIndex][valueIndex] = tag.getFloat(String.format("soulweapons.soulweapon.%s.%s",
                instance.getWeaponName(weaponIndex), SoulWeaponAttribute.getName(valueIndex)));
        });

        instance.setData(data);
        instance.setAttributes(attributes);
    }
}
