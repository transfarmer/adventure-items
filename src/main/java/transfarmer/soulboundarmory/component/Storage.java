package transfarmer.soulboundarmory.component;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Component;
import net.minecraftforge.common.capabilities.Component.IStorage;
import net.minecraftforge.common.util.INBTSerializable;

public class Storage<T extends INBTSerializable<CompoundTag>> implements IStorage<T> {
    @Override
    public NBTBase writeNBT(final Component<T> component, final T instance, final EnumFacing facing) {
        return instance.toTag();
    }

    @Override
    public void readNBT(final Component<T> component, final T instance, final EnumFacing facing, final NBTBase nbt) {
        instance.fromTag((CompoundTag) nbt);
    }
}
