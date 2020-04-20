package transfarmer.soulboundarmory.statistics.skill;

import net.minecraft.entity.player.EntityPlayer;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;

public interface ISkillContext {
    EntityPlayer getPlayer();

    ExtendedPacketBuffer getData();
}
