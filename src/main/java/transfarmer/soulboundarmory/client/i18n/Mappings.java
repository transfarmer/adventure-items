package transfarmer.soulboundarmory.client.i18n;

import net.minecraft.text.TranslatableText;

public class Mappings {
    public static final TranslatableText SOULBOUND_GREATSWORD_NAME = new TranslatableText("item.soulboundarmory.soulboundGreatsword");
    public static final TranslatableText SOULBOUND_SWORD_NAME = new TranslatableText("item.soulboundarmory.soulboundSword");
    public static final TranslatableText SOULBOUND_DAGGER_NAME = new TranslatableText("item.soulboundarmory.soulboundDagger");
    public static final TranslatableText SOULBOUND_STAFF_NAME = new TranslatableText("item.soulboundarmory.soulboundStaff");
    public static final TranslatableText SOULBOUND_PICK_NAME = new TranslatableText("item.soulboundarmory.soulboundPick");

    public static final TranslatableText KEY_CATEGORY = new TranslatableText("key.categories.soulboundarmory");
    public static final TranslatableText MENU_KEY = new TranslatableText("key.soulboundarmory.gui");
    public static final TranslatableText TOGGLE_XP_BAR = new TranslatableText("key.soulboundarmory.bar");

    public static final TranslatableText MENU_SELECTION = new TranslatableText("gui.soulboundarmory.selection");
    public static final TranslatableText MENU_CONFIRMATION = new TranslatableText("gui.soulboundarmory.confirmation");
    public static final TranslatableText MENU_BUTTON_ATTRIBUTES = new TranslatableText("gui.soulboundarmory.attributes");
    public static final TranslatableText MENU_BUTTON_ENCHANTMENTS = new TranslatableText("gui.soulboundarmory.enchantments");
    public static final TranslatableText MENU_SKILLS = new TranslatableText("gui.soulboundarmory.skills");
    public static final TranslatableText MENU_BUTTON_RESET = new TranslatableText("gui.soulboundarmory.reset");
    public static final TranslatableText MENU_BUTTON_BIND = new TranslatableText("gui.soulboundarmory.bind");
    public static final TranslatableText MENU_BUTTON_UNBIND = new TranslatableText("gui.soulboundarmory.unbind");
    public static final TranslatableText MENU_UNSPENT_POINTS = new TranslatableText("gui.soulboundarmory.unspentPoints");
    public static final TranslatableText MENU_UNSPENT_POINT = new TranslatableText("gui.soulboundarmory.unspentPoint");
    public static final TranslatableText MENU_POINTS = new TranslatableText("gui.soulboundarmory.points");
    public static final TranslatableText MENU_POINT = new TranslatableText("gui.soulboundarmory.point");
    public static final TranslatableText MENU_SKILL_LEARN_COST = new TranslatableText("gui.soulboundarmory.learnCost");
    public static final TranslatableText MENU_SKILL_UPGRADE_COST = new TranslatableText("gui.soulboundarmory.upgradeCost");
    public static final TranslatableText MENU_LEVEL = new TranslatableText("gui.soulboundarmory.level");
    public static final TranslatableText RED = new TranslatableText("gui.soulboundarmory.red");
    public static final TranslatableText GREEN = new TranslatableText("gui.soulboundarmory.green");
    public static final TranslatableText BLUE = new TranslatableText("gui.soulboundarmory.blue");
    public static final TranslatableText ALPHA = new TranslatableText("gui.soulboundarmory.alpha");
    public static final TranslatableText XP_BAR_STYLE = new TranslatableText("gui.soulboundarmory.style");
    public static final TranslatableText EXPERIENCE = new TranslatableText("gui.soulboundarmory.experience");
    public static final TranslatableText BOSS = new TranslatableText("gui.soulboundarmory.boss");
    public static final TranslatableText HORSE = new TranslatableText("entity.Horse.name");

    public static final TranslatableText ATTACK_SPEED_FORMAT = new TranslatableText("format.soulboundarmory.attackSpeed");
    public static final TranslatableText ATTACK_DAMAGE_FORMAT = new TranslatableText("format.soulboundarmory.attackDamage");
    public static final TranslatableText CRITICAL_FORMAT = new TranslatableText("format.soulboundarmory.critical");
    public static final TranslatableText KNOCKBACK_ATTRIBUTE_FORMAT = new TranslatableText("format.soulboundarmory.knockback");
    public static final TranslatableText WEAPON_EFFICIENCY_FORMAT = new TranslatableText("format.soulboundarmory.weaponEfficiency");
    public static final TranslatableText TOOL_EFFICIENCY_FORMAT = new TranslatableText("format.soulboundarmory.toolEfficiency");
    public static final TranslatableText HARVEST_LEVEL_FORMAT = new TranslatableText("format.soulboundarmory.harvestLevel");
    public static final TranslatableText REACH_DISTANCE_FORMAT = new TranslatableText("format.soulboundarmory.reachDistance");

    public static final TranslatableText ATTACK_SPEED_NAME = new TranslatableText("attribute.soulboundarmory.attackSpeed");
    public static final TranslatableText ATTACK_DAMAGE_NAME = new TranslatableText("attribute.soulboundarmory.attackDamage");
    public static final TranslatableText CRITICAL_NAME = new TranslatableText("attribute.soulboundarmory.critical");
    public static final TranslatableText KNOCKBACK_ATTRIBUTE_NAME = new TranslatableText("attribute.soulboundarmory.knockback");
    public static final TranslatableText EFFICIENCY_NAME = new TranslatableText("attribute.soulboundarmory.efficiency");
    public static final TranslatableText HARVEST_LEVEL_NAME = new TranslatableText("attribute.soulboundarmory.harvestLevel");
    public static final TranslatableText REACH_DISTANCE_NAME = new TranslatableText("attribute.soulboundarmory.reachDistance");

    public static final TranslatableText MESSAGE_LEVEL_UP = new TranslatableText("message.soulboundarmory.levelup");

    public static final TranslatableText MINING_LEVEL_COAL = new TranslatableText("miningLevel.soulboundarmory.coal");
    public static final TranslatableText MINING_LEVEL_IRON = new TranslatableText("miningLevel.soulboundarmory.iron");
    public static final TranslatableText MINING_LEVEL_DIAMOND = new TranslatableText("miningLevel.soulboundarmory.diamond");
    public static final TranslatableText MINING_LEVEL_OBSIDIAN = new TranslatableText("miningLevel.soulboundarmory.obsidian");

    public static final TranslatableText COMMAND_USAGE_0 = new TranslatableText("command.soulboundarmory.clientUsage0");
    public static final TranslatableText COMMAND_USAGE_1 = new TranslatableText("command.soulboundarmory.clientUsage1");
    public static final TranslatableText COMMAND_NO_ITEM = new TranslatableText("command.soulboundarmory.noItem");

    private static final TranslatableText[] miningLevels = {MINING_LEVEL_COAL, MINING_LEVEL_IRON, MINING_LEVEL_DIAMOND, MINING_LEVEL_OBSIDIAN};

    public static TranslatableText[] getMiningLevels() {
        return miningLevels;
    }
}
