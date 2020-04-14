package transfarmer.soulboundarmory.capability.config;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

import javax.annotation.Nullable;

public class PlayerConfigStorage implements IStorage<IPlayerConfig> {
    @Nullable
    @Override
    public NBTBase writeNBT(final Capability<IPlayerConfig> capability, final IPlayerConfig instance, final EnumFacing side) {
        return null;
    }

    @Override
    public void readNBT(final Capability<IPlayerConfig> capability, final IPlayerConfig instance, final EnumFacing side, final NBTBase nbt) {
    }
}
