package transfarmer.soulboundarmory.capability.soulbound.tool;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class ToolProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(ITool.class)
    @Nonnull
    public static final Capability<ITool> TOOL_CAPABILITY = null;
    private final ITool instance = TOOL_CAPABILITY.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == TOOL_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        return capability == TOOL_CAPABILITY ? TOOL_CAPABILITY.cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return TOOL_CAPABILITY.getStorage().writeNBT(TOOL_CAPABILITY, this.instance, null);
    }

    @Override
    public void deserializeNBT(final NBTBase nbt) {
        TOOL_CAPABILITY.getStorage().readNBT(TOOL_CAPABILITY, this.instance, null, nbt);
    }

    public static ITool get(final Entity entity) {
        return entity.getCapability(TOOL_CAPABILITY, null);
    }
}
