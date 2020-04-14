package transfarmer.soulboundarmory.capability.soulbound.tool;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import transfarmer.soulboundarmory.capability.soulbound.ISoulCapability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class SoulToolProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(ISoulCapability.class)
    private static final Capability<ISoulCapability> CAPABILITY = null;
    private final ISoulCapability instance = CAPABILITY.getDefaultInstance();

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

    public static ISoulCapability get(final Entity entity) {
        return entity.getCapability(CAPABILITY, null);
    }
}
