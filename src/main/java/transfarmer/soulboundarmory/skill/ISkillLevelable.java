package transfarmer.soulboundarmory.skill;

public interface ISkillLevelable extends ISkill, Comparable<ISkillLevelable> {
    int getLevel();

    void setLevel(int level);

    boolean canBeUpgraded();

    boolean canBeUpgraded(int points);

    void upgrade();
}
