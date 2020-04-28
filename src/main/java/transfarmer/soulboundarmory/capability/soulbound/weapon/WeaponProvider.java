package transfarmer.soulboundarmory.capability.soulbound.weapon;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import transfarmer.soulboundarmory.capability.Storage;
import transfarmer.soulboundarmory.util.ReflectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WeaponProvider implements ICapabilitySerializable<NBTTagCompound> {
    public static final Capability<WeaponCapability> WEAPONS = ReflectUtil.createCapability(WeaponCapability.class, new Storage<>(), Weapon::new);

    private final WeaponCapability instance = WEAPONS.getDefaultInstance();

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
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) WEAPONS.getStorage().writeNBT(WEAPONS, instance, null);
    }

    @Override
    public void deserializeNBT(final NBTTagCompound nbt) {
        WEAPONS.getStorage().readNBT(WEAPONS, instance, null, nbt);
    }

    public static WeaponCapability get(final Entity entity) {
        return entity.getCapability(WEAPONS, null);
    }
}
