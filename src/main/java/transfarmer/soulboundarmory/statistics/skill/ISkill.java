package transfarmer.soulboundarmory.statistics.skill;

import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public interface ISkill {
    @SideOnly(CLIENT)
    String getName();

    void apply(ExtendedPacketBuffer context);
}
