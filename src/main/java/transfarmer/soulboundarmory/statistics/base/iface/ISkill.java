package transfarmer.soulboundarmory.statistics.base.iface;

import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public interface ISkill {
    @Override
    String toString();

    @SideOnly(CLIENT)
    String getName();
}
