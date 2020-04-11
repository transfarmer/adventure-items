package transfarmer.soulboundarmory.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

public class MainConfig {
    private static final MainConfig INSTANCE = new MainConfig();
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_MULTIPLIERS = "experience multipliers";
    private final Configuration configFile = new Configuration(new File(String.format("%s/soulboundarmory", Loader.instance().getConfigDir()), "main.cfg"));
    private int initialWeaponXP;
    private int initialToolXP;
    private int levelsPerEnchantment;
    private int levelsPerSkill;
    private int maxLevel;
    private int preservationLevel;
    private boolean addToOffhand;
    private boolean levelupNotifications;

    private float armorMultiplier;
    private float attackDamageMultiplier;
    private float difficultyMultiplier;
    private float babyMultiplier;
    private float bossMultiplier;
    private float hardcoreMultiplier;
    private float passiveMultiplier;

    public static MainConfig instance() {
        return INSTANCE;
    }

    public void load() {
        this.configFile.load();

        this.initialToolXP = this.configFile.get(CATEGORY_GENERAL, "initialToolXP", 16).getInt();
        this.initialWeaponXP = this.configFile.get(CATEGORY_GENERAL, "initialWeaponXP", 64).getInt();
        this.levelsPerEnchantment = this.configFile.get(CATEGORY_GENERAL, "levelsPerEnchantment", 5, "the number of levels per enchantment point").getInt();
        this.levelsPerSkill = this.configFile.get(CATEGORY_GENERAL, "levelsPerSkill", 5, "the number of levels per additional skill").getInt();
        this.maxLevel = this.configFile.get(CATEGORY_GENERAL, "maxLevel", -1, "the maximum soulbound item level. Set to -1 for no limit.").getInt();
        this.preservationLevel = this.configFile.get(CATEGORY_GENERAL, "preservationLevel", 0, "the minimum level for soul weapons to be preserved after death").getInt();

        this.addToOffhand = this.configFile.get(CATEGORY_GENERAL, "addToOffhand", true, "places picked up items with full inventory in offhand").getBoolean();
        this.levelupNotifications = this.configFile.get(CATEGORY_GENERAL, "levelupNotifications", true, "whether levelup notifications should be sent to players or not").getBoolean();

        this.armorMultiplier = (float) this.configFile.get(CATEGORY_MULTIPLIERS, "armorMultiplier", 0.2, "armor XP multiplier = 1 + (armorMultiplier * armor)").getDouble();
        this.attackDamageMultiplier = (float) this.configFile.get(CATEGORY_MULTIPLIERS, "attackDamageMultiplier", 1D / 3, "attack damage XP multiplier = 1 + (attackDamageMultiplier * damage)").getDouble();
        this.difficultyMultiplier = (float) this.configFile.get(CATEGORY_MULTIPLIERS, "difficultyMultiplier", 0.5, "the XP multiplier for each level of game difficulty. (Difficulty ranges from 0 to 3.)").getDouble();
        this.babyMultiplier = (float) this.configFile.get(CATEGORY_MULTIPLIERS, "babyMultiplier", 2, "baby entity XP multiplier").getDouble();
        this.bossMultiplier = (float) this.configFile.get(CATEGORY_MULTIPLIERS, "bossMultiplier", 3, "boss XP multiplier").getDouble();
        this.hardcoreMultiplier = (float) this.configFile.get(CATEGORY_MULTIPLIERS, "hardcoreMultiplier", 2, "hardcore mode XP multiplier").getDouble();
        this.passiveMultiplier = (float) this.configFile.get(CATEGORY_MULTIPLIERS, "passiveMultiplier", 0, "passive entity multiplier").getDouble();

        this.configFile.save();
    }

    public void save() {
        this.configFile.get(CATEGORY_GENERAL, "initialToolXP", 16).set(this.initialToolXP);
        this.configFile.get(CATEGORY_GENERAL, "initialWeaponXP", 64).set(this.initialWeaponXP);
        this.configFile.get(CATEGORY_GENERAL, "levelsPerEnchantment", 5, "the number of levels per enchantment point").set(this.levelsPerEnchantment);
        this.configFile.get(CATEGORY_GENERAL, "levelsPerSkill", 5, "the number of levels required in order to gain a skill").set(this.levelsPerSkill);
        this.configFile.get(CATEGORY_GENERAL, "maxLevel", -1, "the maximum soulbound item level. Set to -1 for no limit.").set(this.maxLevel);
        this.configFile.get(CATEGORY_GENERAL, "preservationLevel", 0, "the minimum level for soul weapons to be preserved after death").set(this.preservationLevel);

        this.configFile.get(CATEGORY_GENERAL, "addToOffhand", true, "places picked up items with full inventory in offhand").set(this.addToOffhand);
        this.configFile.get(CATEGORY_GENERAL, "levelupNotifications", true, "whether levelup notifications should be sent to players or not").set(this.levelupNotifications);

        this.configFile.get(CATEGORY_MULTIPLIERS, "armorMultiplier", 0.2, "armor XP multiplier = 1 + (armorMultiplier * armor)").set(this.armorMultiplier);
        this.configFile.get(CATEGORY_MULTIPLIERS, "attackDamageMultiplier", 1D / 3, "attack damage XP multiplier = 1 + (attackDamageMultiplier * damage)").set(this.attackDamageMultiplier);
        this.configFile.get(CATEGORY_MULTIPLIERS, "difficultyMultiplier", 0.5, "the XP multiplier for game difficulty. (Difficulty ranges from 0 to 3.)").set(this.difficultyMultiplier);
        this.configFile.get(CATEGORY_MULTIPLIERS, "babyMultiplier", 2, "baby entity XP multiplier").set(this.babyMultiplier);
        this.configFile.get(CATEGORY_MULTIPLIERS, "bossMultiplier", 3, "boss XP multiplier").set(this.bossMultiplier);
        this.configFile.get(CATEGORY_MULTIPLIERS, "hardcoreMultiplier", 2, "hardcore mode XP multiplier").set(this.hardcoreMultiplier);
        this.configFile.get(CATEGORY_MULTIPLIERS, "passiveMultiplier", 0, "passive entity multiplier").set(this.passiveMultiplier);

        this.configFile.save();
    }

    public Configuration getConfigFile() {
        return this.configFile;
    }

    public int getInitialToolXP() {
        return this.initialToolXP;
    }

    public void setInitialToolXP(final int initialToolXP) {
        this.initialToolXP = initialToolXP;
    }

    public int getInitialWeaponXP() {
        return this.initialWeaponXP;
    }

    public void setInitialWeaponXP(final int initialWeaponXP) {
        this.initialWeaponXP = initialWeaponXP;
    }

    public int getLevelsPerEnchantment() {
        return this.levelsPerEnchantment;
    }

    public void setLevelsPerEnchantment(final int levelsPerEnchantment) {
        this.levelsPerEnchantment = levelsPerEnchantment;
    }

    public int getLevelsPerSkill() {
        return this.levelsPerSkill;
    }

    public void setLevelsPerSkill(final int levelsPerSkill) {
        this.levelsPerSkill = levelsPerSkill;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public void setMaxLevel(final int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getPreservationLevel() {
        return this.preservationLevel;
    }

    public void setPreservationLevel(final int preservationLevel) {
        this.preservationLevel = preservationLevel;
    }

    public boolean getAddToOffhand() {
        return this.addToOffhand;
    }

    public void setAddToOffhand(final boolean addToOffhand) {
        this.addToOffhand = addToOffhand;
    }

    public boolean getLevelupNotifications() {
        return this.levelupNotifications;
    }

    public void setLevelupNotifications(final boolean levelupNotifications) {
        this.levelupNotifications = levelupNotifications;
    }

    public float getArmorMultiplier() {
        return this.armorMultiplier;
    }

    public void setArmorMultiplier(final float armorMultiplier) {
        this.armorMultiplier = armorMultiplier;
    }

    public float getAttackDamageMultiplier() {
        return this.attackDamageMultiplier;
    }

    public void setAttackDamageMultiplier(final float attackDamageMultiplier) {
        this.attackDamageMultiplier = attackDamageMultiplier;
    }

    public float getDifficultyMultiplier() {
        return this.difficultyMultiplier;
    }

    public void setDifficultyMultiplier(final float difficultyMultiplier) {
        this.difficultyMultiplier = difficultyMultiplier;
    }

    public float getBabyMultiplier() {
        return this.babyMultiplier;
    }

    public void setBabyMultiplier(final float babyMultiplier) {
        this.babyMultiplier = babyMultiplier;
    }

    public float getBossMultiplier() {
        return this.bossMultiplier;
    }

    public void setBossMultiplier(final float bossMultiplier) {
        this.bossMultiplier = bossMultiplier;
    }

    public float getHardcoreMultiplier() {
        return this.hardcoreMultiplier;
    }

    public void setHardcoreMultiplier(final float hardcoreMultiplier) {
        this.hardcoreMultiplier = hardcoreMultiplier;
    }

    public float getPassiveMultiplier() {
        return this.passiveMultiplier;
    }

    public void setPassiveMultiplier(final float passiveMultiplier) {
        this.passiveMultiplier = passiveMultiplier;
    }
}
