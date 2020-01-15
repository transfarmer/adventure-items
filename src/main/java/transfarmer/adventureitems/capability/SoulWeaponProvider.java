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
    public static Capability<ISoulWeapon> TYPE;
    private LazyOptional<ISoulWeapon> instance = LazyOptional.of(TYPE::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction side) {
        return capability == TYPE ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return TYPE.getStorage().writeNBT(TYPE, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional is empty")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        TYPE.getStorage().readNBT(TYPE, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional is empty")), null, nbt);
    }
}
