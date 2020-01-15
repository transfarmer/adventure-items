package transfarmer.adventureitems.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;


public class Provider implements ICapabilitySerializable<INBT> {
    @CapabilityInject(ISoulWeapon.class)
    public static Capability<ISoulWeapon> TYPE;
    private LazyOptional<ISoulWeapon> instance = LazyOptional.of(TYPE::getDefaultInstance);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
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
