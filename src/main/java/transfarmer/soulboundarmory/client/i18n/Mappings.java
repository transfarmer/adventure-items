package transfarmer.soulboundarmory.client.i18n;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SideOnly(CLIENT)
public class Mappings {
    public static final String SOULBOUND_GREATSWORD_NAME = I18n.format("item.soulboundarmory.soulboundGreatsword");
    public static final String SOULBOUND_SWORD_NAME = I18n.format("item.soulboundarmory.soulboundSword");
    public static final String SOULBOUND_DAGGER_NAME = I18n.format("item.soulboundarmory.soulboundDagger");
    public static final String SOULBOUND_STAFF_NAME = I18n.format("item.soulboundarmory.soulboundStaff");
    public static final String SOULBOUND_PICK_NAME = I18n.format("item.soulboundarmory.soulboundPick");

    public static final String KEY_CATEGORY = I18n.format("key.categories.soulboundarmory");
    public static final String MENU_KEY = I18n.format("key.soulboundarmory.menu");
    public static final String TOGGLE_XP_BAR = I18n.format("key.soulboundarmory.bar");

    public static final String MENU_SELECTION = I18n.format("menu.soulboundarmory.selection");
    public static final String MENU_CONFIRMATION = I18n.format("menu.soulboundarmory.confirmation");
    public static final String MENU_BUTTON_ATTRIBUTES = I18n.format("menu.soulboundarmory.attributes");
    public static final String MENU_BUTTON_ENCHANTMENTS = I18n.format("menu.soulboundarmory.enchantments");
    public static final String MENU_BUTTON_SKILLS = I18n.format("menu.soulboundarmory.skills");
    public static final String MENU_BUTTON_RESET = I18n.format("menu.soulboundarmory.reset");
    public static final String MENU_BUTTON_BIND = I18n.format("menu.soulboundarmory.bind");
    public static final String MENU_BUTTON_UNBIND = I18n.format("menu.soulboundarmory.unbind");
    public static final String MENU_UNSPENT_POINTS = I18n.format("menu.soulboundarmory.unspentPoints");
    public static final String MENU_UNSPENT_POINT = I18n.format("menu.soulboundarmory.unspentPoint");
    public static final String MENU_POINTS = I18n.format("menu.soulboundarmory.points");
    public static final String MENU_POINT = I18n.format("menu.soulboundarmory.point");
    public static final String MENU_SKILL_LEARN_COST = I18n.format("menu.soulboundarmory.learnCost");
    public static final String MENU_SKILL_UPGRADE_COST = I18n.format("menu.soulboundarmory.upgradeCost");
    public static final String MENU_LEVEL = I18n.format("menu.soulboundarmory.level");

    public static final String ATTACK_SPEED_FORMAT = I18n.format("format.soulboundarmory.attackSpeed");
    public static final String ATTACK_DAMAGE_FORMAT = I18n.format("format.soulboundarmory.attackDamage");
    public static final String CRITICAL_FORMAT = I18n.format("format.soulboundarmory.critical");
    public static final String KNOCKBACK_ATTRIBUTE_FORMAT = I18n.format("format.soulboundarmory.knockback");
    public static final String WEAPON_EFFICIENCY_FORMAT = I18n.format("format.soulboundarmory.weaponEfficiency");
    public static final String TOOL_EFFICIENCY_FORMAT = I18n.format("format.soulboundarmory.toolEfficiency");
    public static final String HARVEST_LEVEL_FORMAT = I18n.format("format.soulboundarmory.harvestLevel");
    public static final String REACH_DISTANCE_FORMAT = I18n.format("format.soulboundarmory.reachDistance");

    public static final String ATTACK_SPEED_NAME = I18n.format("attribute.soulboundarmory.attackSpeed");
    public static final String ATTACK_DAMAGE_NAME = I18n.format("attribute.soulboundarmory.attackDamage");
    public static final String CRITICAL_NAME = I18n.format("attribute.soulboundarmory.critical");
    public static final String KNOCKBACK_ATTRIBUTE_NAME = I18n.format("attribute.soulboundarmory.knockback");
    public static final String EFFICIENCY_NAME = I18n.format("attribute.soulboundarmory.efficiency");
    public static final String HARVEST_LEVEL_NAME = I18n.format("attribute.soulboundarmory.harvestLevel");
    public static final String REACH_DISTANCE_NAME = I18n.format("attribute.soulboundarmory.reachDistance");

    public static final String MESSAGE_LEVEL_UP = I18n.format("message.soulboundarmory.levelup");

    public static final String MINING_LEVEL_COAL = I18n.format("miningLevel.soulboundarmory.coal");
    public static final String MINING_LEVEL_IRON = I18n.format("miningLevel.soulboundarmory.iron");

    public static final String MINING_LEVEL_DIAMOND = I18n.format("miningLevel.soulboundarmory.diamond");
    public static final String MINING_LEVEL_OBSIDIAN = I18n.format("miningLevel.soulboundarmory.obsidian");

    public static final String COMMAND_USAGE_0 = I18n.format("command.soulboundarmory.clientUsage0");
    public static final String COMMAND_USAGE_1 = I18n.format("command.soulboundarmory.clientUsage1");
    public static final String COMMAND_NO_ITEM = I18n.format("command.soulboundarmory.noItem");

    public static final String RED = I18n.format("color.soulboundarmory.red");
    public static final String GREEN = I18n.format("color.soulboundarmory.green");
    public static final String BLUE = I18n.format("color.soulboundarmory.blue");
    public static final String ALPHA = I18n.format("color.soulboundarmory.alpha");

    private static final String[] miningLevels = {MINING_LEVEL_COAL, MINING_LEVEL_IRON, MINING_LEVEL_DIAMOND, MINING_LEVEL_OBSIDIAN};

    public static String[] getMiningLevels() {
        return miningLevels;
    }
}
