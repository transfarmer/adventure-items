package transfarmer.soulboundarmory.skill;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import transfarmer.soulboundarmory.capability.soulbound.common.ISoulbound;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SkillContextBuffer implements ISkillContext {
    private final Queue<Integer> ints;
    private final Queue<Double> doubles;
    private final Queue<String> strings;
    private final Queue<Entity> entities;
    private final Queue<EntityPlayer> players;
    private final Queue<ISoulbound> capabilities;

    public SkillContextBuffer() {
        this.ints = new LinkedList<>();
        this.strings = new LinkedList<>();
        this.doubles = new LinkedList<>();
        this.entities = new LinkedList<>();
        this.capabilities = new LinkedList<>();
        this.players = new LinkedList<>();
    }

    @Override
    public int readInt() {
        return this.ints.poll();
    }

    @Override
    public ISkillContext writeInt(final int value) {
        this.ints.add(value);

        return this;
    }

    @Override
    public double readDouble() {
        return this.doubles.poll();
    }

    @Override
    public ISkillContext writeDouble(final double value) {
        this.doubles.add(value);

        return this;
    }

    @Override
    public String readString() {
        return this.strings.poll();
    }

    @Override
    public ISkillContext writeString(final String string) {
        this.strings.add(string);

        return this;
    }

    @Override
    public Entity readEntity() {
        return this.entities.poll();
    }

    @Override
    public ISkillContext writeEntity(final Entity entity) {
        this.entities.add(entity);

        return this;
    }

    @Override
    public Queue<Entity> readEntities() {
        return this.entities;
    }

    @Override
    public ISkillContext writeEntities(final List<Entity> entities) {
        this.entities.addAll(entities);

        return this;
    }

    @Override
    public EntityPlayer readPlayer() {
        return this.players.poll();
    }

    @Override
    public ISkillContext writePlayer(final EntityPlayer player) {
        this.players.add(player);

        return this;
    }

    @Override
    public ISoulbound readCapability() {
        return this.capabilities.poll();
    }

    @Override
    public ISkillContext writeCapability(final ISoulbound capability) {
        this.capabilities.add(capability);

        return this;
    }
}
