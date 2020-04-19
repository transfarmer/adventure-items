package transfarmer.soulboundarmory.statistics.base.enumeration;

import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.capability.soulbound.ICapability;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;
import transfarmer.soulboundarmory.util.CollectionUtil;
import transfarmer.soulboundarmory.util.StringUtil;

import javax.annotation.Nonnull;

import static transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider.TOOL_CAPABILITY;
import static transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider.WEAPON_CAPABILITY;

@SuppressWarnings("ConstantConditions")
public enum CapabilityType implements ICapabilityType {
    TOOL(TOOL_CAPABILITY),
    WEAPON(WEAPON_CAPABILITY);

    static {
        CollectionUtil.addAll(CAPABILITIES, TOOL, WEAPON);
    }

    private final Capability<? extends ICapability> capability;

    CapabilityType(final @NotNull Capability<? extends ICapability> capability) {
        this.capability = capability;
    }

    @Override
    @Nonnull
    public Capability<? extends ICapability> getCapability() {
        return this.capability;
    }

    @Override
    public String toString() {
        return StringUtil.macroCaseToCamelCase(this.name());
    }
}
