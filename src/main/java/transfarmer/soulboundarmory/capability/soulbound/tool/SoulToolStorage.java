package transfarmer.soulboundarmory.capability.soulbound.tool;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import transfarmer.soulboundarmory.capability.soulbound.ISoulCapability;

public class SoulToolStorage implements IStorage<ISoulCapability> {
    @Override
    public NBTBase writeNBT(final Capability<ISoulCapability> capability, final ISoulCapability instance, final EnumFacing facing) {
        return instance.writeToNBT();
    }

    @Override
    public void readNBT(final Capability<ISoulCapability> capability, final ISoulCapability instance, final EnumFacing facing, final NBTBase nbt) {
        instance.readFromNBT((NBTTagCompound) nbt);
    }
}
