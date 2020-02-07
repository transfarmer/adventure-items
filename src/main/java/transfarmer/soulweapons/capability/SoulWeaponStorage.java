package transfarmer.soulweapons.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import transfarmer.soulweapons.data.SoulWeaponAttribute;
import transfarmer.soulweapons.data.SoulWeaponDatum;
import transfarmer.soulweapons.data.SoulWeaponEnchantment;
import transfarmer.soulweapons.data.SoulWeaponType;

public class SoulWeaponStorage implements IStorage<transfarmer.soulweapons.capability.ISoulWeapon> {
    @Override
    public NBTBase writeNBT(Capability<transfarmer.soulweapons.capability.ISoulWeapon> capability, transfarmer.soulweapons.capability.ISoulWeapon instance, EnumFacing facing) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("soulweapons.capability.index", instance.getCurrentType() == null ? -1 : instance.getCurrentType().index);
        tag.setInteger("soulweapons.capability.tab", instance.getCurrentTab());
        final int[][] data = instance.getData();
        final float[][] attributes = instance.getAttributes();
        final int[][] enchantments = instance.getEnchantments();

        transfarmer.soulweapons.capability.SoulWeaponHelper.forEach(
            (Integer weaponIndex, Integer valueIndex) ->
                tag.setInteger(String.format("soulweapons.datum.%s.%s",
                    SoulWeaponType.getType(weaponIndex),
                    SoulWeaponDatum.getName(valueIndex)),
                    data[weaponIndex][valueIndex]),
            (Integer weaponIndex, Integer valueIndex) ->
                tag.setFloat(String.format("soulweapons.attribute.%s.%s",
                    SoulWeaponType.getType(weaponIndex),
                    SoulWeaponAttribute.getName(valueIndex)),
                    attributes[weaponIndex][valueIndex]),
            (Integer weaponIndex, Integer valueIndex) ->
                tag.setInteger(String.format("soulweapons.enchantment.%s.%s",
                    SoulWeaponType.getType(weaponIndex),
                    SoulWeaponEnchantment.getName(valueIndex)),
                    enchantments[weaponIndex][valueIndex])
        );

        return tag;
    }

    @Override
    public void readNBT(Capability<transfarmer.soulweapons.capability.ISoulWeapon> capability, ISoulWeapon instance, EnumFacing facing, NBTBase nbt) {
        NBTTagCompound tag = (NBTTagCompound) nbt;
        instance.setCurrentType(tag.getInteger("soulweapons.capability.index"));
        instance.setCurrentTab(tag.getInteger("soulweapons.capability.tab"));
        final int[][] data = new int[3][5];
        final float[][] attributes = new float[3][5];
        final int[][] enchantments = new int[3][7];

        SoulWeaponHelper.forEach(
            (Integer weaponIndex, Integer valueIndex) ->
                data[weaponIndex][valueIndex] = tag.getInteger(String.format("soulweapons.datum.%s.%s",
                    SoulWeaponType.getType(weaponIndex),
                    SoulWeaponDatum.getName(valueIndex)
                )),
            (Integer weaponIndex, Integer valueIndex) ->
                attributes[weaponIndex][valueIndex] = tag.getFloat(String.format("soulweapons.attribute.%s.%s",
                        SoulWeaponType.getType(weaponIndex),
                        SoulWeaponAttribute.getName(valueIndex)
                )),
            (Integer weaponIndex, Integer valueIndex) ->
                enchantments[weaponIndex][valueIndex] = tag.getInteger(String.format("soulweapons.enchantment.%s.%s",
                        SoulWeaponType.getType(weaponIndex),
                        SoulWeaponEnchantment.getName(valueIndex)
                ))
        );

        instance.set(data, attributes, enchantments);
    }
}
