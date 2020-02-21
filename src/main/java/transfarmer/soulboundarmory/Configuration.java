package transfarmer.soulboundarmory;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

@Config(modid = Main.MOD_ID)
public class Configuration {
    @Comment("experience point multipliers")
    public static Multipliers multipliers = new Multipliers();

    public static int initialWeaponXP = 48;

    public static int initialToolXP = 16;

    @Comment("the number of levels per enchantment point")
    public static int levelsPerEnchantment = 5;

    @Comment("the number of levels per additional skill")
    public static int levelsPerSkill = 5;

    @Comment("whether levelup notifications should be sent to players or not")
    public static boolean levelupNotifications = true;

    @Comment("maximum soul weapon level")
    public static int maxLevel = 100;

    @Comment("whether passive entities yield XP or not")
    public static boolean passiveXP = false;

    @Comment("minimum level for soul weapons to be preserved after death")
    public static int preservationLevel = 0;

    @Comment("the offset for soul weapon menu entries from the top multiplied by ((height of Minecraft window) / 16)")
    public static int menuOffset = 1;

    @Comment("Set this to \"false\" in order to prevent items picked up with full inventory and empty offhand from appearing in the offhand.")
    public static boolean addToOffhand = true;

    public static class Multipliers {
        @Comment("armor XP multiplier = 1 + (armorMultiplier * armor)")
        public float armorMultiplier = 0.2F;

        @Comment("attack damage XP multiplier = 1 + (attackDamageMultiplier * damage)")
        public float attackDamageMultiplier = 1F / 3;

        @Comment({
            "the XP multiplier for game difficulty. (Difficulty ranges from 0 to 3.)",
            "The default setting of 0.5 on hard mode will multiply XP gain by 0.5 * 3 = 1.5"
        })
        public float difficultyMultiplier = 0.5F;

        @Comment("boss XP multiplier")
        public float bossMultiplier = 3;

        @Comment("hardcore mode XP multiplier")
        public float hardcoreMultiplier = 2;

        @Comment("baby zombie (including baby zombie pigmen) XP multiplier")
        public float babyZombieMultiplier = 2;
    }
}
