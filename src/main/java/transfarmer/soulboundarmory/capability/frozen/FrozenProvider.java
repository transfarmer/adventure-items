package transfarmer.soulboundarmory.capability.frozen;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class FrozenProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(IFrozen.class)
    private static final Capability<IFrozen> CAPABILITY = null;
    private final IFrozen instance = CAPABILITY.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        return capability == CAPABILITY ? CAPABILITY.cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return CAPABILITY.getStorage().writeNBT(CAPABILITY, this.instance, null);
    }

    @Override
    public void deserializeNBT(final NBTBase nbt) {
        CAPABILITY.getStorage().readNBT(CAPABILITY, this.instance, null, nbt);
    }

    public static IFrozen get(final Entity entity) {
        return entity.getCapability(CAPABILITY, null);
    }
}
