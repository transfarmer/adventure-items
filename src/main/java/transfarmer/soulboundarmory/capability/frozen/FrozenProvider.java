package transfarmer.soulboundarmory.capability.frozen;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import transfarmer.soulboundarmory.capability.Storage;
import transfarmer.soulboundarmory.util.ReflectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FrozenProvider implements ICapabilitySerializable<NBTTagCompound> {
    public static final Capability<IFrozen> FROZEN = ReflectUtil.createCapability(IFrozen.class, new Storage<>(), Frozen::new);
    private final IFrozen instance = FROZEN.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == FROZEN;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        return capability == FROZEN ? FROZEN.cast(this.instance) : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) FROZEN.getStorage().writeNBT(FROZEN, this.instance, null);
    }

    @Override
    public void deserializeNBT(final NBTTagCompound nbt) {
        FROZEN.getStorage().readNBT(FROZEN, this.instance, null, nbt);
    }

    public static IFrozen get(final Entity entity) {
        return entity.getCapability(FROZEN, null);
    }
}
