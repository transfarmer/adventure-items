package transfarmer.soulboundarmory.skill;

public interface SkillLevelable extends Skill, Comparable<SkillLevelable> {
    int getLevel();

    void setLevel(int level);

    boolean canBeUpgraded();

    boolean canBeUpgraded(int points);

    void upgrade();
}
