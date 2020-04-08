package transfarmer.soulboundarmory.capability.tool;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import transfarmer.soulboundarmory.capability.ISoulCapability;

public class SoulToolStorage implements IStorage<ISoulCapability> {
    @Override
    public NBTBase writeNBT(final Capability<ISoulCapability> capability, final ISoulCapability instance, final EnumFacing facing) {
        return instance.writeNBT();
    }

    @Override
    public void readNBT(final Capability<ISoulCapability> capability, final ISoulCapability instance, final EnumFacing facing, final NBTBase nbt) {
        instance.readNBT((NBTTagCompound) nbt);
    }
}
