package transfarmer.soulboundarmory.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

public class MainConfig {
    private static final MainConfig INSTANCE = new MainConfig();
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_MULTIPLIERS = "experience multipliers";
    private final MainConfiguration configFile = new MainConfiguration(new File(String.format("%s/soulboundarmory", Loader.instance().getConfigDir()), "main.cfg"));
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

        this.initialToolXP = this.configFile.getInitialToolXP().getInt();
        this.initialWeaponXP = this.configFile.getInitialWeaponXP().getInt();
        this.levelsPerEnchantment = this.configFile.getLevelsPerEnchantment().getInt();
        this.levelsPerSkill = this.configFile.getLevelsPerSkill().getInt();
        this.maxLevel = this.configFile.getMaxLevel().getInt();
        this.preservationLevel = this.configFile.getPreservationLevel().getInt();

        this.addToOffhand = this.configFile.getAddToOffhand().getBoolean();
        this.levelupNotifications = this.configFile.getLevelupNotifications().getBoolean();

        this.armorMultiplier = (float) this.configFile.getArmorMultiplier().getDouble();
        this.attackDamageMultiplier = (float) this.configFile.getAttackDamageMultiplier().getDouble();
        this.difficultyMultiplier = (float) this.configFile.getDifficultyMultiplier().getDouble();
        this.babyMultiplier = (float) this.configFile.getBabyMultiplier().getDouble();
        this.bossMultiplier = (float) this.configFile.getBossMultiplier().getDouble();
        this.hardcoreMultiplier = (float) this.configFile.getHardcoreMultiplier().getDouble();
        this.passiveMultiplier = (float) this.configFile.getPassiveMultiplier().getDouble();

        this.configFile.save();
    }

    public void save() {
        this.configFile.getInitialToolXP().set(this.initialToolXP);
        this.configFile.getInitialWeaponXP().set(this.initialWeaponXP);
        this.configFile.getLevelsPerEnchantment().set(this.levelsPerEnchantment);
        this.configFile.getLevelsPerSkill().set(this.levelsPerSkill);
        this.configFile.getMaxLevel().set(this.maxLevel);
        this.configFile.getPreservationLevel().set(this.preservationLevel);

        this.configFile.getAddToOffhand().set(this.addToOffhand);
        this.configFile.getLevelupNotifications().set(this.levelupNotifications);

        this.configFile.getArmorMultiplier().set(this.armorMultiplier);
        this.configFile.getAttackDamageMultiplier().set(this.attackDamageMultiplier);
        this.configFile.getDifficultyMultiplier().set(this.difficultyMultiplier);
        this.configFile.getBabyMultiplier().set(this.babyMultiplier);
        this.configFile.getBossMultiplier().set(this.bossMultiplier);
        this.configFile.getHardcoreMultiplier().set(this.hardcoreMultiplier);
        this.configFile.getPassiveMultiplier().set(this.passiveMultiplier);

        this.configFile.save();
    }

    public Configuration getConfigFile() {
        return this.configFile;
    }

    public int getInitialToolXP() {
        return this.initialToolXP;
    }

    public int getInitialWeaponXP() {
        return this.initialWeaponXP;
    }

    public int getLevelsPerEnchantment() {
        return this.levelsPerEnchantment;
    }

    public int getLevelsPerSkill() {
        return this.levelsPerSkill;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public int getPreservationLevel() {
        return this.preservationLevel;
    }

    public boolean getAddToOffhand() {
        return this.addToOffhand;
    }

    public boolean getLevelupNotifications() {
        return this.levelupNotifications;
    }

    public float getArmorMultiplier() {
        return this.armorMultiplier;
    }

    public float getAttackDamageMultiplier() {
        return this.attackDamageMultiplier;
    }

    public float getDifficultyMultiplier() {
        return this.difficultyMultiplier;
    }

    public float getBabyMultiplier() {
        return this.babyMultiplier;
    }

    public float getBossMultiplier() {
        return this.bossMultiplier;
    }

    public float getHardcoreMultiplier() {
        return this.hardcoreMultiplier;
    }

    public float getPassiveMultiplier() {
        return this.passiveMultiplier;
    }

    public void setInitialToolXP(final int initialToolXP) {
        this.initialToolXP = initialToolXP;
    }

    public void setInitialWeaponXP(final int initialWeaponXP) {
        this.initialWeaponXP = initialWeaponXP;
    }

    public void setLevelsPerEnchantment(final int levelsPerEnchantment) {
        this.levelsPerEnchantment = levelsPerEnchantment;
    }

    public void setLevelsPerSkill(final int levelsPerSkill) {
        this.levelsPerSkill = levelsPerSkill;
    }

    public void setMaxLevel(final int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setPreservationLevel(final int preservationLevel) {
        this.preservationLevel = preservationLevel;
    }

    public void setAddToOffhand(final boolean addToOffhand) {
        this.addToOffhand = addToOffhand;
    }

    public void setLevelupNotifications(final boolean levelupNotifications) {
        this.levelupNotifications = levelupNotifications;
    }

    public void setArmorMultiplier(final float armorMultiplier) {
        this.armorMultiplier = armorMultiplier;
    }

    public void setAttackDamageMultiplier(final float attackDamageMultiplier) {
        this.attackDamageMultiplier = attackDamageMultiplier;
    }

    public void setDifficultyMultiplier(final float difficultyMultiplier) {
        this.difficultyMultiplier = difficultyMultiplier;
    }

    public void setBabyMultiplier(final float babyMultiplier) {
        this.babyMultiplier = babyMultiplier;
    }

    public void setBossMultiplier(final float bossMultiplier) {
        this.bossMultiplier = bossMultiplier;
    }

    public void setHardcoreMultiplier(final float hardcoreMultiplier) {
        this.hardcoreMultiplier = hardcoreMultiplier;
    }

    public void setPassiveMultiplier(final float passiveMultiplier) {
        this.passiveMultiplier = passiveMultiplier;
    }

    private static class MainConfiguration extends Configuration {
        public MainConfiguration(final File file) {
            super(file);
        }

        public Property getInitialToolXP() {
            return this.get(CATEGORY_GENERAL, "initialToolXP", 16);
        }

        public Property getInitialWeaponXP() {
            return this.get(CATEGORY_GENERAL, "initialWeaponXP", 64);
        }

        public Property getLevelsPerEnchantment() {
            return this.get(CATEGORY_GENERAL, "levelsPerEnchantment", 5, "the number of levels per enchantment point");
        }

        public Property getLevelsPerSkill() {
            return this.get(CATEGORY_GENERAL, "levelsPerSkill", 5, "the number of levels required in order to gain a skill");
        }

        public Property getMaxLevel() {
            return this.get(CATEGORY_GENERAL, "maxLevel", -1, "the maximum soulbound item level. Set to -1 for no limit.");
        }

        public Property getPreservationLevel() {
            return this.get(CATEGORY_GENERAL, "preservationLevel", 0, "the minimum level for soul weapons to be preserved after death");
        }

        public Property getAddToOffhand() {
            return this.get(CATEGORY_GENERAL, "addToOffhand", true, "places picked up items with full inventory in offhand");
        }

        public Property getLevelupNotifications() {
            return this.get(CATEGORY_GENERAL, "levelupNotifications", true, "whether levelup notifications should be sent to players or not");
        }

        public Property getArmorMultiplier() {
            return this.get(CATEGORY_MULTIPLIERS, "armorMultiplier", 0.2, "armor XP multiplier = 1 + (armorMultiplier * armor)");
        }

        public Property getAttackDamageMultiplier() {
            return this.get(CATEGORY_MULTIPLIERS, "attackDamageMultiplier", 1D / 3, "attack damage XP multiplier = 1 + (attackDamageMultiplier * damage)");
        }

        public Property getDifficultyMultiplier() {
            return this.get(CATEGORY_MULTIPLIERS, "difficultyMultiplier", 0.5, "the XP multiplier for game difficulty. (Difficulty ranges from 0 to 3.)");
        }

        public Property getBabyMultiplier() {
            return this.get(CATEGORY_MULTIPLIERS, "babyMultiplier", 2, "baby entity XP multiplier");
        }

        public Property getBossMultiplier() {
            return this.get(CATEGORY_MULTIPLIERS, "bossMultiplier", 3, "boss XP multiplier");
        }

        public Property getHardcoreMultiplier() {
            return this.get(CATEGORY_MULTIPLIERS, "hardcoreMultiplier", 2, "hardcore mode XP multiplier");
        }

        public Property getPassiveMultiplier() {
            return this.get(CATEGORY_MULTIPLIERS, "passiveMultiplier", 0, "passive entity multiplier");
        }
    }
}
