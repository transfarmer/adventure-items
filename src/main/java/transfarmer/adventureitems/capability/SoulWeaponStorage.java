package transfarmer.adventureitems.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import transfarmer.adventureitems.Main;

import static transfarmer.adventureitems.SoulWeapons.WeaponType;


public class SoulWeaponStorage implements IStorage<ISoulWeapon> {
    @Override
    public INBT writeNBT(Capability<ISoulWeapon> capability, ISoulWeapon instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        String weaponType = instance.getWeaponType() == null ? "null" : instance.getWeaponType().toString();
        tag.putString("adventureitems.weaponType", weaponType);
        tag.putInt("adventureitems.level", instance.getLevel());
        tag.putInt("adventureitems.points", instance.getPoints());
        tag.putInt("adventureitems.special", instance.getSpecial());
        tag.putInt("adventureitems.hardness", instance.getHardness());
        tag.putInt("adventureitems.knockback", instance.getKnockback());
        tag.putInt("adventureitems.attackDamage", instance.getAttackDamage());
        tag.putInt("adventureitems.critical", instance.getCritical());
        return tag;
    }

    @Override
    public void readNBT(Capability<ISoulWeapon> capability, ISoulWeapon instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        instance.setWeaponType(WeaponType.getItem(tag.getString("adventureitems.weaponType")));
        instance.setLevel(tag.getInt("adventureitems.level"));
        instance.setPoints(tag.getInt("aventureitems.points"));
        instance.setSpecial(tag.getInt("adventureitems.special"));
        instance.setHardness(tag.getInt("adventureitems.hardness"));
        instance.setKnockback(tag.getInt("adventureitems.knockback"));
        instance.setAttackDamage(tag.getInt("adventureitems.attackDamage"));
        instance.setCritical(tag.getInt("adventureitems.critical"));
    }
}
