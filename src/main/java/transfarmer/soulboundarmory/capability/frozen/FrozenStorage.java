package transfarmer.soulboundarmory.capability.frozen;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

import javax.annotation.Nullable;

public class FrozenStorage implements IStorage<IFrozen> {
    @Nullable
    @Override
    public NBTBase writeNBT(final Capability<IFrozen> capability, final IFrozen instance, final EnumFacing side) {
        return new NBTTagCompound();
    }

    @Override
    public void readNBT(final Capability<IFrozen> capability, final IFrozen instance, final EnumFacing side, final NBTBase nbt) {
    }
}
