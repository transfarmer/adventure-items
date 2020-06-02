package user11681.soulboundarmory.component.statistics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nonnull;
import nerdhub.cardinal.components.api.util.NbtSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.registry.Registries;
import user11681.soulboundarmory.skill.Skill;
import user11681.soulboundarmory.skill.SkillContainer;

public class SkillStorage implements Iterable<Skill>, NbtSerializable {
    private final Map<Skill, SkillContainer> skills;

    public SkillStorage(final Skill... skills) {
        this.skills = new HashMap<>();

        for (final Skill skill : skills) {
            this.skills.put(skill, new SkillContainer(skill, this));
        }
    }

    public Map<Skill, SkillContainer> get() {
        return this.skills;
    }

    public SkillContainer get(final Skill skill) {
        return this.skills.get(skill);
    }

    public void put(SkillContainer skill) {
        this.skills.put(skill.getSkill(), skill);
    }

    public boolean contains(final Skill skill) {
        final SkillContainer container = this.get(skill);

        return container != null && container.isLearned();
    }

    public boolean contains(final Skill skill, final int level) {
        final SkillContainer container = this.get(skill);

        return container != null && container.isLearned() && container.getLevel() >= level;
    }

    public void reset() {
        for (final SkillContainer container : this.skills.values()) {
            if (container.hasDependencies())
                this.skills.clear();
        }
    }

    public Collection<SkillContainer> values() {
        return this.skills.values();
    }

    @Nonnull
    public CompoundTag toTag(@Nonnull final CompoundTag tag) {
        for (final SkillContainer skill : this.values()) {
            if (skill != null) {
                tag.put(skill.getSkill().getIdentifier().toString(), skill.toTag(new CompoundTag()));
            }
        }

        return tag;
    }

    @Override
    public void fromTag(final CompoundTag tag) {
        for (final String identifier : tag.getKeys()) {
            final SkillContainer skill = this.get(Registries.SKILL.get(new Identifier(identifier)));

            if (skill != null) {
                skill.fromTag(tag.getCompound(identifier));
            }
        }
    }

    @Nonnull
    @Override
    public Iterator<Skill> iterator() {
        return this.skills.keySet().iterator();
    }
}
