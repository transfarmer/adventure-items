package transfarmer.soulboundarmory.statistics.base.enumeration;

import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.capability.soulbound.common.ISoulbound;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;
import transfarmer.soulboundarmory.util.CollectionUtil;
import transfarmer.soulboundarmory.util.StringUtil;

import javax.annotation.Nonnull;

import static transfarmer.soulboundarmory.capability.soulbound.tool.ToolProvider.TOOLS;
import static transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider.WEAPONS;

public enum CapabilityType implements ICapabilityType {
    TOOL(TOOLS),
    WEAPON(WEAPONS);

    static {
        CollectionUtil.addAll(CAPABILITIES, TOOL, WEAPON);
    }

    private final Capability<? extends ISoulbound> capability;

    CapabilityType(final @NotNull Capability<? extends ISoulbound> capability) {
        this.capability = capability;
    }

    @Override
    @Nonnull
    public Capability<? extends ISoulbound> getCapability() {
        return this.capability;
    }

    @Override
    public String toString() {
        return StringUtil.macroCaseToCamelCase(this.name());
    }
}
