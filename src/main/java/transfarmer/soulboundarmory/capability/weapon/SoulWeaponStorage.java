package transfarmer.soulboundarmory.capability.weapon;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponAttribute;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponDatum;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponEnchantment;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponType;

import static transfarmer.soulboundarmory.capability.weapon.SoulWeaponHelper.ATTRIBUTES;
import static transfarmer.soulboundarmory.capability.weapon.SoulWeaponHelper.DATA;
import static transfarmer.soulboundarmory.capability.weapon.SoulWeaponHelper.ENCHANTMENTS;

public class SoulWeaponStorage implements IStorage<ISoulWeapon> {
    @Override
    public NBTBase writeNBT(Capability<ISoulWeapon> capability, ISoulWeapon instance, EnumFacing facing) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("soulweapons.capability.index", instance.getCurrentType() == null ? -1 : instance.getCurrentType().index);
        tag.setInteger("soulweapons.capability.tab", instance.getCurrentTab());
        tag.setInteger("soulweapons.capability.cooldown", instance.getAttackCooldwn());
        tag.setInteger("soulweapons.capability.boundSlot", instance.getBoundSlot());
        final int[][] data = instance.getData();
        final float[][] attributes = instance.getAttributes();
        final int[][] enchantments = instance.getEnchantments();

        SoulWeaponHelper.forEach(
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
    public void readNBT(Capability<ISoulWeapon> capability, ISoulWeapon instance, EnumFacing facing, NBTBase nbt) {
        NBTTagCompound tag = (NBTTagCompound) nbt;
        instance.setCurrentType(tag.getInteger("soulweapons.capability.index"));
        instance.setCurrentTab(tag.getInteger("soulweapons.capability.tab"));
        instance.setAttackCooldwn(tag.getInteger("soulweapons.capability.cooldown"));
        instance.setBoundSlot(tag.getInteger("soulweapons.capability.boundSlot"));
        final int[][] data = new int[3][DATA];
        final float[][] attributes = new float[3][ATTRIBUTES];
        final int[][] enchantments = new int[3][ENCHANTMENTS];

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
