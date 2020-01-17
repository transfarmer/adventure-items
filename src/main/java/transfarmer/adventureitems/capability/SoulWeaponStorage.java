package transfarmer.adventureitems.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import transfarmer.adventureitems.Main;


public class SoulWeaponStorage implements IStorage<ISoulWeapon> {
    @Override
    public INBT writeNBT(Capability<ISoulWeapon> capability, ISoulWeapon instance, Direction side) {
        Main.LOGGER.info("write");
        CompoundNBT tag = new CompoundNBT();
        String weaponType = instance.getCurrentType() == null ? "null" : instance.getCurrentType().toString();
        tag.putString("adventureitems.weaponType", weaponType);
        return tag;
    }

    @Override
    public void readNBT(Capability<ISoulWeapon> capability, ISoulWeapon instance, Direction side, INBT nbt) {
        Main.LOGGER.info("read");
        CompoundNBT tag = (CompoundNBT) nbt;
        instance.setCurrentType(SoulWeapon.WeaponType.get(tag.getString("adventureitems.weaponType")));
    }
}
