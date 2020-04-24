package transfarmer.soulboundarmory.skill;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import transfarmer.soulboundarmory.capability.soulbound.common.ISoulbound;

import java.util.List;
import java.util.Queue;

public interface ISkillContext {
    int readInt();

    ISkillContext writeInt(int value);

    double readDouble();

    ISkillContext writeDouble(double value);

    String readString();

    ISkillContext writeString(String string);

    ISkillContext writeEntities(List<Entity> entities);

    EntityPlayer readPlayer();

    ISkillContext writePlayer(EntityPlayer player);

    ISoulbound readCapability();

    ISkillContext writeCapability(ISoulbound capability);

    Entity readEntity();

    Queue<Entity> readEntities();

    ISkillContext writeEntity(Entity entity);
}
