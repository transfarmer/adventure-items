package transfarmer.soulboundarmory.client.i18n;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Mappings {
    public static final LangEntry SOULBOUND_GREATSWORD_NAME = new LangEntry("item.soulboundarmory.soulboundGreatsword");
    public static final LangEntry SOULBOUND_SWORD_NAME = new LangEntry("item.soulboundarmory.soulboundSword");
    public static final LangEntry SOULBOUND_DAGGER_NAME = new LangEntry("item.soulboundarmory.soulboundDagger");
    public static final LangEntry SOULBOUND_STAFF_NAME = new LangEntry("item.soulboundarmory.soulboundStaff");
    public static final LangEntry SOULBOUND_PICK_NAME = new LangEntry("item.soulboundarmory.soulboundPick");

    public static final LangEntry KEY_CATEGORY = new LangEntry("key.categories.soulboundarmory");
    public static final LangEntry MENU_KEY = new LangEntry("key.soulboundarmory.gui");
    public static final LangEntry TOGGLE_XP_BAR = new LangEntry("key.soulboundarmory.bar");

    public static final LangEntry MENU_SELECTION = new LangEntry("gui.soulboundarmory.selection");
    public static final LangEntry MENU_CONFIRMATION = new LangEntry("gui.soulboundarmory.confirmation");
    public static final LangEntry MENU_BUTTON_ATTRIBUTES = new LangEntry("gui.soulboundarmory.attributes");
    public static final LangEntry MENU_BUTTON_ENCHANTMENTS = new LangEntry("gui.soulboundarmory.enchantments");
    public static final LangEntry MENU_BUTTON_SKILLS = new LangEntry("gui.soulboundarmory.skills");
    public static final LangEntry MENU_BUTTON_RESET = new LangEntry("gui.soulboundarmory.reset");
    public static final LangEntry MENU_BUTTON_BIND = new LangEntry("gui.soulboundarmory.bind");
    public static final LangEntry MENU_BUTTON_UNBIND = new LangEntry("gui.soulboundarmory.unbind");
    public static final LangEntry MENU_UNSPENT_POINTS = new LangEntry("gui.soulboundarmory.unspentPoints");
    public static final LangEntry MENU_UNSPENT_POINT = new LangEntry("gui.soulboundarmory.unspentPoint");
    public static final LangEntry MENU_POINTS = new LangEntry("gui.soulboundarmory.points");
    public static final LangEntry MENU_POINT = new LangEntry("gui.soulboundarmory.point");
    public static final LangEntry MENU_SKILL_LEARN_COST = new LangEntry("gui.soulboundarmory.learnCost");
    public static final LangEntry MENU_SKILL_UPGRADE_COST = new LangEntry("gui.soulboundarmory.upgradeCost");
    public static final LangEntry MENU_LEVEL = new LangEntry("gui.soulboundarmory.level");
    public static final LangEntry RED = new LangEntry("gui.soulboundarmory.red");
    public static final LangEntry GREEN = new LangEntry("gui.soulboundarmory.green");
    public static final LangEntry BLUE = new LangEntry("gui.soulboundarmory.blue");
    public static final LangEntry ALPHA = new LangEntry("gui.soulboundarmory.alpha");
    public static final LangEntry XP_BAR_STYLE = new LangEntry("gui.soulboundarmory.style");
    public static final LangEntry EXPERIENCE = new LangEntry("gui.soulboundarmory.experience");
    public static final LangEntry BOSS = new LangEntry("gui.soulboundarmory.boss");
    public static final LangEntry HORSE = new LangEntry("entity.Horse.name");

    public static final LangEntry ATTACK_SPEED_FORMAT = new LangEntry("format.soulboundarmory.attackSpeed");
    public static final LangEntry ATTACK_DAMAGE_FORMAT = new LangEntry("format.soulboundarmory.attackDamage");
    public static final LangEntry CRITICAL_FORMAT = new LangEntry("format.soulboundarmory.critical");
    public static final LangEntry KNOCKBACK_ATTRIBUTE_FORMAT = new LangEntry("format.soulboundarmory.knockback");
    public static final LangEntry WEAPON_EFFICIENCY_FORMAT = new LangEntry("format.soulboundarmory.weaponEfficiency");
    public static final LangEntry TOOL_EFFICIENCY_FORMAT = new LangEntry("format.soulboundarmory.toolEfficiency");
    public static final LangEntry HARVEST_LEVEL_FORMAT = new LangEntry("format.soulboundarmory.harvestLevel");
    public static final LangEntry REACH_DISTANCE_FORMAT = new LangEntry("format.soulboundarmory.reachDistance");

    public static final LangEntry ATTACK_SPEED_NAME = new LangEntry("attribute.soulboundarmory.attackSpeed");
    public static final LangEntry ATTACK_DAMAGE_NAME = new LangEntry("attribute.soulboundarmory.attackDamage");
    public static final LangEntry CRITICAL_NAME = new LangEntry("attribute.soulboundarmory.critical");
    public static final LangEntry KNOCKBACK_ATTRIBUTE_NAME = new LangEntry("attribute.soulboundarmory.knockback");
    public static final LangEntry EFFICIENCY_NAME = new LangEntry("attribute.soulboundarmory.efficiency");
    public static final LangEntry HARVEST_LEVEL_NAME = new LangEntry("attribute.soulboundarmory.harvestLevel");
    public static final LangEntry REACH_DISTANCE_NAME = new LangEntry("attribute.soulboundarmory.reachDistance");

    public static final LangEntry MESSAGE_LEVEL_UP = new LangEntry("message.soulboundarmory.levelup");

    public static final LangEntry MINING_LEVEL_COAL = new LangEntry("miningLevel.soulboundarmory.coal");
    public static final LangEntry MINING_LEVEL_IRON = new LangEntry("miningLevel.soulboundarmory.iron");
    public static final LangEntry MINING_LEVEL_DIAMOND = new LangEntry("miningLevel.soulboundarmory.diamond");
    public static final LangEntry MINING_LEVEL_OBSIDIAN = new LangEntry("miningLevel.soulboundarmory.obsidian");

    public static final LangEntry COMMAND_USAGE_0 = new LangEntry("command.soulboundarmory.clientUsage0");
    public static final LangEntry COMMAND_USAGE_1 = new LangEntry("command.soulboundarmory.clientUsage1");
    public static final LangEntry COMMAND_NO_ITEM = new LangEntry("command.soulboundarmory.noItem");

    private static final LangEntry[] miningLevels = {MINING_LEVEL_COAL, MINING_LEVEL_IRON, MINING_LEVEL_DIAMOND, MINING_LEVEL_OBSIDIAN};

    public static LangEntry[] getMiningLevels() {
        return miningLevels;
    }
}
