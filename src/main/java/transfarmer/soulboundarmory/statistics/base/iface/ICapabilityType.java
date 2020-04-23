package transfarmer.soulboundarmory.statistics.base.iface;

import net.minecraftforge.common.capabilities.Capability;
import transfarmer.soulboundarmory.capability.soulbound.common.ISoulbound;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public interface ICapabilityType {
    List<ICapabilityType> CAPABILITIES = new ArrayList<>();

    @Nonnull
    Capability<? extends ISoulbound> getCapability();

    @Override
    String toString();

    static ICapabilityType get(final String string) {
        for (final ICapabilityType capability : CAPABILITIES) {
            if (capability.toString().equals(string)) {
                return capability;
            }
        }

        return null;
    }
}
