package transfarmer.soulboundarmory.capability;

import net.minecraft.nbt.NBTTagCompound;

public interface ICapabilitySerializable {
    NBTTagCompound writeToNBT();

    void readFromNBT(NBTTagCompound nbt);
}
