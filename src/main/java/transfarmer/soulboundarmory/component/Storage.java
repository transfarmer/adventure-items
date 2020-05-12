package transfarmer.soulboundarmory.component;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.INBTSerializable;

public class Storage<T extends INBTSerializable<CompoundTag>> implements IStorage<T> {
    @Override
    public NBTBase writeNBT(final Capability<T> capability, final T instance, final EnumFacing facing) {
        return instance.serializeNBT();
    }

    @Override
    public void readNBT(final Capability<T> capability, final T instance, final EnumFacing facing, final NBTBase nbt) {
        instance.deserializeNBT((CompoundTag) nbt);
    }
}
