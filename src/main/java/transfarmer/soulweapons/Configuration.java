package transfarmer.soulweapons;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

@Config(modid = Main.MODID)
public class Configuration {
    @Comment("experience point multipliers")
    public static Multipliers multipliers = new Multipliers();

    @Comment("the number of levels per enchantment point")
    public static int enchantmentLevels = 5;

    @Comment("the number of levels per additional skill")
    public static int skillLevels = 5;

    @Comment("initial experience points required in order to level up")
    public static int initialXP = 48;

    @Comment("whether levelup notifications should be sent to players or not")
    public static boolean levelupNotifications = true;

    @Comment("maximum soul weapon level")
    public static int maxLevel = 100;

    @Comment("minimum level for soul weapons to be preserved after death")
    public static int preservationLevel = 10;

    @Comment("the offset for soul weapon menu entries from the top multiplied by (height of Minecraft window / 16)")
    public static int menuOffset = 1;

    public static class Multipliers {
        @Comment("armor XP multiplier = 1 + (armorMultiplier * armor)")
        public float armorMultiplier = 0.2F;

        @Comment("attack damage XP multiplier = 1 + (attackDamageMultiplier * damage)")
        public float attackDamageMultiplier = 1F / 3;

        @Comment("boss XP multiplier")
        public float bossMultiplier = 3;

        @Comment({
            "the XP multiplier for game difficulty. (Difficulty ranges from 0 to 3.)",
            "The default setting of 0.5 on hard mode will multiply XP gain by 0.5 * 3 = 1.5"
        })
        public float difficultyMultiplier = 0.5F;

        @Comment("hardcore mode XP multiplier")
        public float hardcoreMultiplier = 2;
    }
}
