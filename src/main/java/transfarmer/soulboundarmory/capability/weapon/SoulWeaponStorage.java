package transfarmer.soulboundarmory.capability.weapon;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class SoulWeaponStorage implements IStorage<ISoulWeapon> {
    @Override
    public NBTBase writeNBT(final Capability<ISoulWeapon> capability, final ISoulWeapon instance, final EnumFacing facing) {
        return instance.writeNBT();
    }

    @Override
    public void readNBT(final Capability<ISoulWeapon> capability, final ISoulWeapon instance, final EnumFacing facing, final NBTBase nbt) {
        instance.readNBT((NBTTagCompound) nbt);
    }
}
