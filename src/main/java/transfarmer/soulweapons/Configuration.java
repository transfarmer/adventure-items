package transfarmer.soulweapons;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

@Config(modid = Main.MODID)
public class Configuration {
    @Comment("the XP multiplier for enemy attack damage.")
    public static float attackDamageMultiplier = 0.333F;

    @Comment({
        "the XP multiplier for game difficulty. (Difficulty ranges from 0 to 3.)",
        "The default setting of 0.5 on hard mode will multiply XP gain by 1.5"
    })
    public static float difficultyMultiplier = 0.5F;

    @Comment("whether levelup notifications should be sent to players or not")
    public static boolean levelupNotifications = true;

    @Comment("whether attribute point notifications should be sent to players on levelup or not")
    public static boolean levelupPointNotifications = true;

    @Comment("whether every level should award only an attribute point or not")
    public static boolean onlyPoints = false;

    @Comment("max level")
    public static float maxLevel = 100;
}
