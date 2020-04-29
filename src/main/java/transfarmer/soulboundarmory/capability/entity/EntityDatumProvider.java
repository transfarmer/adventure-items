package transfarmer.soulboundarmory.capability.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import transfarmer.soulboundarmory.capability.Storage;
import transfarmer.soulboundarmory.util.ReflectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityDatumProvider implements ICapabilitySerializable<NBTTagCompound> {
    public static final Capability<IEntityData> FROZEN = ReflectUtil.createCapability(IEntityData.class, new Storage<>(), EntityData::new);
    private final IEntityData instance = FROZEN.getDefaultInstance();

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

    public static IEntityData get(final Entity entity) {
        return entity.getCapability(FROZEN, null);
    }
}
