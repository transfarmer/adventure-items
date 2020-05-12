package transfarmer.soulboundarmory.statistics.base.enumeration;

import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;
import transfarmer.farmerlib.util.CollectionUtil;
import transfarmer.farmerlib.util.StringUtil;

import javax.annotation.Nonnull;

import static transfarmer.soulboundarmory.component.soulbound.tool.ToolProvider.TOOLS;
import static transfarmer.soulboundarmory.component.soulbound.weapon.WeaponProvider.WEAPONS;

public enum CapabilityType implements ICapabilityType {
    TOOL(TOOLS),
    WEAPON(WEAPONS);

    static {
        CollectionUtil.addAll(CAPABILITIES, TOOL, WEAPON);
    }

    private final Capability<? extends ISoulboundComponent> capability;

    CapabilityType(final @NotNull Capability<? extends ISoulboundComponent> capability) {
        this.capability = capability;
    }

    @Override
    @Nonnull
    public Capability<? extends ISoulboundComponent> getCapability() {
        return this.capability;
    }

    @Override
    public String toString() {
        return StringUtil.macroCaseToCamelCase(this.name());
    }
}
