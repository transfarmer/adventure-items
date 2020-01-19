package transfarmer.adventureitems.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;


public class SoulWeaponProvider implements ICapabilitySerializable<INBT> {
    @CapabilityInject(ISoulWeapon.class)
    public static Capability<ISoulWeapon> SOUL_WEAPON;
    private LazyOptional<ISoulWeapon> instance = LazyOptional.of(SOUL_WEAPON::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction side) {
        return capability == SOUL_WEAPON ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return SOUL_WEAPON.getStorage().writeNBT(SOUL_WEAPON, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional is empty")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        SOUL_WEAPON.getStorage().readNBT(SOUL_WEAPON, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional is empty")), null, nbt);
    }
}
