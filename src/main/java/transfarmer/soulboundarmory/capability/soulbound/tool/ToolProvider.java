package transfarmer.soulboundarmory.capability.soulbound.tool;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import transfarmer.soulboundarmory.capability.Storage;
import transfarmer.soulboundarmory.util.ReflectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ToolProvider implements ICapabilitySerializable<NBTBase> {
    public static final Capability<ITool> TOOLS = ReflectUtil.createCapability(ITool.class, new Storage<>(), Tool::new);
    private final ITool instance = TOOLS.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == TOOLS;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        return capability == TOOLS ? TOOLS.cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return TOOLS.getStorage().writeNBT(TOOLS, this.instance, null);
    }

    @Override
    public void deserializeNBT(final NBTBase nbt) {
        TOOLS.getStorage().readNBT(TOOLS, this.instance, null, nbt);
    }

    public static ITool get(final Entity entity) {
        return entity.getCapability(TOOLS, null);
    }
}
