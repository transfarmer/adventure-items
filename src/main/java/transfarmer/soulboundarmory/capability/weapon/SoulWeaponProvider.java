package transfarmer.soulboundarmory.capability.weapon;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class SoulWeaponProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(ISoulWeapon.class)
    private static final Capability<ISoulWeapon> CAPABILITY = null;
    private final ISoulWeapon instance = CAPABILITY.getDefaultInstance();

    @Override
    @Nullable
    public <T> T getCapability(final @Nonnull Capability<T> capability, final @Nullable EnumFacing facing) {
        return capability == CAPABILITY ? CAPABILITY.cast(this.instance) : null;
    }

    @Override
    public boolean hasCapability(final @Nonnull Capability<?> capability, final @Nullable EnumFacing facing) {
        return capability == CAPABILITY;
    }

    @Override
    public NBTBase serializeNBT() {
        return CAPABILITY.getStorage().writeNBT(CAPABILITY, instance, null);
    }

    @Override
    public void deserializeNBT(final NBTBase nbt) {
        CAPABILITY.getStorage().readNBT(CAPABILITY, instance, null, nbt);
    }

    public static ISoulWeapon get(final Entity entity) {
        return entity.getCapability(CAPABILITY, null);
    }
}
