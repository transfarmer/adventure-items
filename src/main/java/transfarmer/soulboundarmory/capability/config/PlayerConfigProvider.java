package transfarmer.soulboundarmory.capability.config;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class PlayerConfigProvider implements ICapabilityProvider {
    @CapabilityInject(IPlayerConfig.class)
    private static final Capability<IPlayerConfig> CAPABILITY = null;
    private final IPlayerConfig instance = CAPABILITY.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        return capability == CAPABILITY ? CAPABILITY.cast(this.instance) : null;
    }

    public static IPlayerConfig get(final Entity entity) {
        return entity.getCapability(CAPABILITY, null);
    }
}
