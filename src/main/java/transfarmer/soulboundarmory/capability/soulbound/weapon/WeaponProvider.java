package transfarmer.soulboundarmory.capability.soulbound.weapon;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class WeaponProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(IWeapon.class)
    @Nonnull
    public static final Capability<IWeapon> WEAPON_CAPABILITY = null;
    private final IWeapon instance = WEAPON_CAPABILITY.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == WEAPON_CAPABILITY;
    }

    @Override
    @Nullable
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        return capability == WEAPON_CAPABILITY ? WEAPON_CAPABILITY.cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return WEAPON_CAPABILITY.getStorage().writeNBT(WEAPON_CAPABILITY, instance, null);
    }

    @Override
    public void deserializeNBT(final NBTBase nbt) {
        WEAPON_CAPABILITY.getStorage().readNBT(WEAPON_CAPABILITY, instance, null, nbt);
    }

    public static IWeapon get(final Entity entity) {
        return entity.getCapability(WEAPON_CAPABILITY, null);
    }
}
