package transfarmer.soulboundarmory.skill;

import net.minecraft.entity.player.EntityPlayer;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;

public class SkillContext implements ISkillContext {
    private final EntityPlayer player;
    private final ExtendedPacketBuffer tag;

    public SkillContext(final EntityPlayer player, final ExtendedPacketBuffer tag) {
        this.player = player;
        this.tag = tag;
    }

    @Override
    public EntityPlayer getPlayer() {
        return this.player;
    }

    @Override
    public ExtendedPacketBuffer getData() {
        return this.tag;
    }
}
