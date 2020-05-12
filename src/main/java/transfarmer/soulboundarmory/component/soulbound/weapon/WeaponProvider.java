package transfarmer.soulboundarmory.component.soulbound.weapon;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import transfarmer.soulboundarmory.component.Storage;
import transfarmer.farmerlib.util.ReflectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WeaponProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<IWeaponCapability> WEAPONS = ReflectUtil.createCapability(IWeaponCapability.class, new Storage<>(), Weapon::new);

    private final IWeaponCapability instance = WEAPONS.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == WEAPONS;
    }

    @Override
    @Nullable
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        return capability == WEAPONS ? WEAPONS.cast(this.instance) : null;
    }

    @Override
    public CompoundTag serializeNBT() {
        return (CompoundTag) WEAPONS.getStorage().writeNBT(WEAPONS, instance, null);
    }

    @Override
    public void deserializeNBT(final CompoundTag nbt) {
        WEAPONS.getStorage().readNBT(WEAPONS, instance, null, nbt);
    }

    public static IWeaponCapability get(final Entity entity) {
        return entity.getCapability(WEAPONS, null);
    }
}
