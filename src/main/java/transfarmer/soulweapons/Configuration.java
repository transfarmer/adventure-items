package transfarmer.soulweapons;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

@Config(modid = Main.MODID)
public class Configuration {
    public static Multipliers multipliers = new Multipliers();

    @Comment("whether levelup notifications should be sent to players or not")
    public static boolean levelupNotifications = true;

    @Comment("whether attribute point notifications should be sent to players on levelup or not")
    public static boolean levelupPointNotifications = true;

    @Comment("whether every level should award only an attribute point or not")
    public static boolean onlyPoints = false;

    @Comment("max level")
    public static int maxLevel = 100;

    @Comment("minimum level for soul weapons to be preserved after death")
    public static int preservationLevel = 10;

    public static class Multipliers {
        @Comment("armor XP multiplier = 1 + (armorMultiplier * armor)")
        public float armorMultiplier = 0.05F;

        @Comment("attack damage XP multiplier = 1 + (attackDamageMultiplier * damage)")
        public float attackDamageMultiplier = 0.333F;

        @Comment({
            "the XP multiplier for game difficulty. (Difficulty ranges from 0 to 3.)",
            "The default setting of 0.5 on hard mode will multiply XP gain by 0.5 * 3 = 1.5"
        })
        public float difficultyMultiplier = 0.5F;

    }
}
