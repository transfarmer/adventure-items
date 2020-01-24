package transfarmer.soulweapons.capability;

import net.minecraft.item.Item;
import transfarmer.soulweapons.WeaponType;

import java.util.Random;

import static transfarmer.soulweapons.WeaponType.NONE;

@SuppressWarnings("DuplicateBranchesInSwitch")
public class SoulWeapon implements ISoulWeapon {
    private static final String[] weaponNames = {"bigsword", "sword", "dagger", null};
    private static final String[] attributeNames = {"level", "points", "special", "maxSpecial",
            "hardness", "knockback", "attackDamage", "critical"};
    private WeaponType currentType = NONE;
    private int[][] attributes = new int[3][8];

    private final int bigswordIndex = 0;
    private final int swordIndex = 1;
    private final int daggerIndex = 2;
    private int currentTypeIndex = -1;

    private final int levelIndex = 0;
    private final int pointIndex = 1;
    private final int specialIndex = 2;
    private final int maxSpecialIndex = 3;
    private final int hardnessIndex = 4;
    private final int knockbackIndex = 5;
    private final int attackDamageIndex = 6;
    private final int criticalIndex = 7;

    @Override
    public void setAttributes(int[][] attributes) {
        this.attributes = attributes;
    }

    @Override
    public int[][] getAttributes() {
        return this.attributes;
    }

    @Override
    public void addAttribute(int attributeNumber) {
        switch (currentTypeIndex) {
            case bigswordIndex:
                switch (attributeNumber) {
                    case 0:
                        addPoint();
                        break;
                    case 1:
                        if (this.attributes[currentTypeIndex][specialIndex] > this.attributes[currentTypeIndex][maxSpecialIndex]) {
                            addSpecial();
                            break;
                        }
                    case 2:
                        addHardness(5);
                        break;
                    case 3:
                        addKnockback(1);
                        break;
                    case 4:
                        addAttackDamage(1);
                        break;
                    case 5:
                        if (this.attributes[currentTypeIndex][specialIndex] > this.attributes[currentTypeIndex][maxSpecialIndex]) {
                            addSpecial();
                            break;
                        }
                    case 6:
                        addHardness(4);
                        break;
                    case 7:
                        addKnockback(1);
                        break;
                    case 8:
                        addCritical(2);
                        break;
                    case 9:
                        addAttackDamage(1);
                        break;
                    default:
                        this.addAttribute(new Random().nextInt(10));
                }

                break;
            case swordIndex:
                switch (attributeNumber) {
                    case 0:
                        addPoint();
                        break;
                    case 1:
                        if (this.attributes[currentTypeIndex][specialIndex] > this.attributes[currentTypeIndex][maxSpecialIndex]) {
                            addSpecial();
                            break;
                        }
                    case 2:
                        addHardness(4);
                        break;
                    case 3:
                        addKnockback(1);
                        break;
                    case 4:
                        addAttackDamage(1);
                        break;
                    case 5:
                        if (this.attributes[currentTypeIndex][specialIndex] > this.attributes[currentTypeIndex][maxSpecialIndex]) {
                            addSpecial();
                            break;
                        }
                    case 6:
                        addHardness(3);
                        break;
                    case 7:
                        addCritical(2);
                        break;
                    case 8:
                        addCritical(2);
                        break;
                    case 9:
                        addAttackDamage(1);
                        break;
                    default:
                        this.addAttribute(new Random().nextInt(10));
                }

                break;
            case daggerIndex:
                switch (attributeNumber) {
                    case 0:
                        addPoint();
                        break;
                    case 1:
                        if (this.attributes[currentTypeIndex][specialIndex] > this.attributes[currentTypeIndex][maxSpecialIndex]) {
                            addSpecial();
                            break;
                        }
                    case 2:
                        addHardness(4);
                        break;
                    case 3:
                        addCritical(2);
                        break;
                    case 4:
                        addAttackDamage(1);
                        break;
                    case 5:
                        if (this.attributes[currentTypeIndex][specialIndex] > this.attributes[currentTypeIndex][maxSpecialIndex]) {
                            addSpecial();
                            break;
                        }
                    case 6:
                        addHardness(3);
                        break;
                    case 7:
                        addCritical(4);
                        break;
                    case 8:
                        addKnockback(1);
                        break;
                    case 9:
                        addAttackDamage(1);
                        break;
                    default:
                        this.addAttribute(new Random().nextInt(10));
                }
        }
    }

    @Override
    public String getWeaponName() {
        return weaponNames[currentTypeIndex];
    }

    @Override
    public String getWeaponName(int index) {
        return weaponNames[index];
    }

    @Override
    public String getAttributeName(int index) {
        return attributeNames[index];
    }

    @Override
    public Item getItem() {
        return currentType.getItem();
    }

    @Override
    public int getLevel() {
        return this.attributes[currentTypeIndex][levelIndex];
    }

    @Override
    public int getLevel(int index) {
        return this.attributes[index][levelIndex];
    }

    @Override
    public void setLevel(int level) {
        this.attributes[currentTypeIndex][levelIndex] = level;
    }

    @Override
    public void addLevel() {
        addLevel(currentTypeIndex);
    }

    @Override
    public void addLevel(int index) {
        addAttribute(this.attributes[index][levelIndex]++ % 10);
    }

    @Override
    public int getPoints() {
        return this.attributes[currentTypeIndex][pointIndex];
    }

    @Override
    public void setPoints(int points) {
        this.attributes[currentTypeIndex][pointIndex] = points;
    }

    @Override
    public void addPoint() {
        this.attributes[currentTypeIndex][pointIndex]++;
    }

    @Override
    public int getMaxSpecial() {
        return this.attributes[currentTypeIndex][maxSpecialIndex];
    }

    @Override
    public int getSpecial() {
        return this.attributes[currentTypeIndex][specialIndex];
    }

    @Override
    public void setSpecial(int special) {
        this.attributes[currentTypeIndex][specialIndex] = special;
    }

    @Override
    public void addSpecial() {
        this.attributes[currentTypeIndex][specialIndex]++;
    }

    @Override
    public int getHardness() {
        return this.attributes[currentTypeIndex][hardnessIndex];
    }

    @Override
    public void setHardness(int hardness) {
        this.attributes[currentTypeIndex][hardnessIndex] = hardness;
    }

    @Override
    public void addHardness(int amount) {
        this.attributes[currentTypeIndex][hardnessIndex] += amount;
    }

    @Override
    public int getKnockback() {
        return this.attributes[currentTypeIndex][knockbackIndex];
    }

    @Override
    public void setKnockback(int knockback) {
        this.attributes[currentTypeIndex][knockbackIndex] = knockback;
    }

    @Override
    public void addKnockback(int amount) {
        this.attributes[currentTypeIndex][knockbackIndex] += amount;
    }

    @Override
    public int getAttackDamage() {
        return this.attributes[currentTypeIndex][attackDamageIndex];
    }

    @Override
    public void setAttackDamage(int attackDamage) {
        this.attributes[currentTypeIndex][attackDamageIndex] = attackDamage;
    }

    @Override
    public void addAttackDamage(int amount) {
        this.attributes[currentTypeIndex][attackDamageIndex] += amount;
    }

    @Override
    public int getCritical() {
        return this.attributes[currentTypeIndex][criticalIndex];
    }

    @Override
    public void setCritical(int critical) {
        this.attributes[currentTypeIndex][criticalIndex] = critical;
    }

    @Override
    public void addCritical(int amount) {
        this.attributes[currentTypeIndex][criticalIndex] += amount;
    }

    @Override
    public int getCurrentTypeIndex() {
        return this.currentTypeIndex;
    }

    @Override
    public void setCurrentType(int index) {
        this.currentTypeIndex = index;
        this.currentType = WeaponType.getType(index);
    }

    @Override
    public void setCurrentType(WeaponType type) {
        this.currentType = type;
        this.currentTypeIndex = type.getIndex();
    }

    @Override
    public boolean hasAttributes() {
        return this.attributes[0].length == 8 && this.attributes[1].length == 8 && this.attributes[2].length == 8;
    }

    @Override
    public WeaponType getCurrentType() {
        return this.currentType;
    }
}
