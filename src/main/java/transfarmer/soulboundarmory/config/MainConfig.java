package transfarmer.soulboundarmory.config;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.network.C2S.C2SConfig;
import transfarmer.soulboundarmory.network.S2C.S2CConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainConfig extends Config {
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_MULTIPLIERS = "experience multipliers";
    private static final MainConfig INSTANCE = new MainConfig(new MainConfiguration(new File(String.format("%s/soulboundarmory", Loader.instance().getConfigDir()), "main.cfg")));
    private final MainConfiguration configFile;

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

    public MainConfig(final MainConfiguration configFile) {
        super(configFile);

        this.configFile = configFile;
    }

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

    public void saveAndLoad() {
        this.configFile.save();
        this.load();
    }

    public void update() {
        final String loadedVersion = this.configFile.getLoadedConfigVersion();

        if (loadedVersion == null || !loadedVersion.equals(this.configFile.getDefinedConfigVersion())) {
            if (this.configFile.getConfigFile().delete()) {
                this.load();

                Main.LOGGER.warn("Deleted old configuration file.");
            }
        } else {
            this.cleanUp();
        }
    }

    public NBTTagCompound writeToNBT() {
        final NBTTagCompound tag = new NBTTagCompound();

        for (final Property property : this.configFile.getIntegers()) {
            tag.setInteger(property.getName(), property.getInt());
        }

        for (final Property property : this.configFile.getBooleans()) {
            tag.setBoolean(property.getName(), property.getBoolean());
        }

        for (final Property property : this.configFile.getFloats()) {
            tag.setFloat(property.getName(), (float) property.getDouble());
        }

        return tag;
    }

    public void readFromNBT(final NBTTagCompound tag) {
        this.initialToolXP = tag.getInteger(this.configFile.getInitialToolXP().getName());
        this.initialWeaponXP = tag.getInteger(this.configFile.getInitialWeaponXP().getName());
        this.levelsPerEnchantment = tag.getInteger(this.configFile.getLevelsPerEnchantment().getName());
        this.levelsPerSkill = tag.getInteger(this.configFile.getLevelsPerSkill().getName());
        this.maxLevel = tag.getInteger(this.configFile.getMaxLevel().getName());
        this.preservationLevel = tag.getInteger(this.configFile.getPreservationLevel().getName());

        this.addToOffhand = tag.getBoolean(this.configFile.getAddToOffhand().getName());
        this.levelupNotifications = tag.getBoolean(this.configFile.getLevelupNotifications().getName());

        this.armorMultiplier = tag.getFloat(this.configFile.getArmorMultiplier().getName());
        this.attackDamageMultiplier = tag.getFloat(this.configFile.getAttackDamageMultiplier().getName());
        this.babyMultiplier = tag.getFloat(this.configFile.getBabyMultiplier().getName());
        this.bossMultiplier = tag.getFloat(this.configFile.getBossMultiplier().getName());
        this.difficultyMultiplier = tag.getFloat(this.configFile.getDifficultyMultiplier().getName());
        this.hardcoreMultiplier = tag.getFloat(this.configFile.getHardcoreMultiplier().getName());
        this.passiveMultiplier = tag.getFloat(this.configFile.getPassiveMultiplier().getName());
    }

    public void sync() {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            Main.CHANNEL.sendToServer(new C2SConfig());
        } else {
            Main.CHANNEL.sendToAll(new S2CConfig());
        }
    }

    public MainConfiguration getConfigFile() {
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

    public static class MainConfiguration extends ExtendedConfiguration {
        public MainConfiguration(final File file) {
            super(file, "2.10.5");
        }

        public List<Property> getProperties() {
            final List<Property> properties = new ArrayList<>();

            properties.addAll(this.getIntegers());
            properties.addAll(this.getBooleans());
            properties.addAll(this.getFloats());

            return properties;
        }

        public List<Property> getIntegers() {
            final List<Property> properties = new ArrayList<>();

            properties.add(this.getInitialToolXP());
            properties.add(this.getInitialWeaponXP());
            properties.add(this.getLevelsPerEnchantment());
            properties.add(this.getLevelsPerSkill());
            properties.add(this.getMaxLevel());
            properties.add(this.getPreservationLevel());

            return properties;
        }

        public List<Property> getBooleans() {
            final List<Property> properties = new ArrayList<>();

            properties.add(this.getAddToOffhand());
            properties.add(this.getLevelupNotifications());

            return properties;
        }

        public List<Property> getFloats() {
            final List<Property> properties = new ArrayList<>();

            properties.add(this.getArmorMultiplier());
            properties.add(this.getAttackDamageMultiplier());
            properties.add(this.getDifficultyMultiplier());
            properties.add(this.getBabyMultiplier());
            properties.add(this.getBossMultiplier());
            properties.add(this.getHardcoreMultiplier());
            properties.add(this.getPassiveMultiplier());

            return properties;
        }

        public Property getInitialToolXP() {
            return this.get(CATEGORY_GENERAL, "initial pick XP", 16);
        }

        public Property getInitialWeaponXP() {
            return this.get(CATEGORY_GENERAL, "initial weapon XP", 64);
        }

        public Property getLevelsPerEnchantment() {
            return this.get(CATEGORY_GENERAL, "levels per enchantment point", 10, "the number of levels required in order to gain an enchantment point");
        }

        public Property getLevelsPerSkill() {
            return this.get(CATEGORY_GENERAL, "levels per skill point", 5, "the number of levels required in order to gain a skill point");
        }

        public Property getMaxLevel() {
            return this.get(CATEGORY_GENERAL, "max level", -1, "the maximum soulbound item level. Set to -1 for no limit.");
        }

        public Property getPreservationLevel() {
            return this.get(CATEGORY_GENERAL, "preservation level", 0, "the minimum level for soul weapons to be preserved after death");
        }

        public Property getAddToOffhand() {
            return this.get(CATEGORY_GENERAL, "add items to offhand", true, "places picked up items with full inventory in offhand");
        }

        public Property getLevelupNotifications() {
            return this.get(CATEGORY_GENERAL, "levelup notifications", false, "whether levelup notifications should be sent to players or not");
        }

        public Property getArmorMultiplier() {
            return this.get(CATEGORY_MULTIPLIERS, "armor multiplier", 0.2, "armor XP multiplier = 1 + (armorMultiplier * armor)");
        }

        public Property getAttackDamageMultiplier() {
            return this.get(CATEGORY_MULTIPLIERS, "attack damage XP multiplier", 1D / 3, "attack damage XP multiplier = 1 + (attackDamageMultiplier * damage)");
        }

        public Property getDifficultyMultiplier() {
            return this.get(CATEGORY_MULTIPLIERS, "difficulty XP multiplier", 0.5, "the XP multiplier for game difficulty. (Difficulty ranges from 0 to 3.)");
        }

        public Property getBabyMultiplier() {
            return this.get(CATEGORY_MULTIPLIERS, "baby entity XP multiplier", 2, "the factor by which XP gained by killing baby entities are multiplied");
        }

        public Property getBossMultiplier() {
            return this.get(CATEGORY_MULTIPLIERS, "boss XP multiplier", 3, "the factor by which XP gained by killing bosses are multiplied");
        }

        public Property getHardcoreMultiplier() {
            return this.get(CATEGORY_MULTIPLIERS, "hardcore mode XP multiplier", 2, "hardcore mode XP multiplier");
        }

        public Property getPassiveMultiplier() {
            return this.get(CATEGORY_MULTIPLIERS, "passive entity XP multiplier", 0, "the factor by which XP gained by killing passive entities are multiplied");
        }
    }
}
