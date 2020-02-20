package transfarmer.soulboundarmory.capability.tool;

import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.statistics.IType;

public interface ISoulTool extends ISoulCapability {
    float getEffectiveEfficiency(IType type);

    float getEffectiveReachDistance(IType type);
}
