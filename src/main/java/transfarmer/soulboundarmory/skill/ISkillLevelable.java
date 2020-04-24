package transfarmer.soulboundarmory.skill;

public interface ISkillLevelable extends ISkill, Comparable<ISkillLevelable> {
    int getLevel();

    void setLevel(int level);
}
