package transfarmer.soulboundarmory.statistics.base.iface;

import net.minecraftforge.common.capabilities.Capability;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public interface ICapabilityType {
    List<ICapabilityType> CAPABILITIES = new ArrayList<>();

    @Nonnull
    Capability<? extends SoulboundCapability> getCapability();

    @Override
    String toString();

    @SuppressWarnings("ConstantConditions")
    @Nonnull()
    static ICapabilityType get(@Nonnull final String string) {
        for (final ICapabilityType capability : CAPABILITIES) {
            if (capability.toString().equals(string)) {
                return capability;
            }
        }

        return null;
    }
}
