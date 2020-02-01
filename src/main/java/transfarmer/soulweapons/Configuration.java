package transfarmer.soulweapons;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

@Config(modid = Main.MODID)
public class Configuration {
    @Comment({
        "the XP multiplier for game difficulty. (Difficulty ranges from 0 to 3.)",
        "The default setting of 0.5 on hard mode will multiply XP gain by 1.5"
    })
    public static float difficultyMultiplier = 0.5F;

    @Comment("the XP multiplier for enemy attack damage.")
    public static float attackDamageMultiplier = 0.333F;
}
