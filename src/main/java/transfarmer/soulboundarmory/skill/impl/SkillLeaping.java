package transfarmer.soulboundarmory.skill.impl;

import net.minecraft.util.ResourceLocation;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.skill.SkillBase;

public class SkillLeaping extends SkillBase {
    public SkillLeaping() {
        super("leaping");
    }

    @Override
    public void apply(final ExtendedPacketBuffer buffer) {
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation("textures/items/feather.png");
    }
}
