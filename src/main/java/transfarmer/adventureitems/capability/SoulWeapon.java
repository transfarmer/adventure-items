package transfarmer.adventureitems.capability;

import net.minecraft.item.Item;

import java.util.HashSet;
import java.util.Random;

import static transfarmer.adventureitems.init.ModItems.SOUL_BIGSWORD;
import static transfarmer.adventureitems.init.ModItems.SOUL_DAGGER;
import static transfarmer.adventureitems.init.ModItems.SOUL_SWORD;

public class SoulWeapon implements ISoulWeapon {
    private final String[] names = {"bigsword", "sword", "dagger"};
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

    public enum WeaponType {
        BIGSWORD(SOUL_BIGSWORD, 0),
        SWORD(SOUL_SWORD, 1),
        DAGGER(SOUL_DAGGER, 2);

        private static final HashSet<Item> SOUL_WEAPONS = new HashSet<>(3, 1);
        private Item item;
        private int index;

        WeaponType(Item item, int index) {
            this.item = item;
            this.index = index;
        }

        public Item getItem() {
            return this.item;
        }

        public int getIndex() {
            return this.index;
        }

        public static HashSet<Item> getItems() {
            return SOUL_WEAPONS;
        }

        static {
            SOUL_WEAPONS.add(SOUL_BIGSWORD);
            SOUL_WEAPONS.add(SOUL_SWORD);
            SOUL_WEAPONS.add(SOUL_DAGGER);
        }
    }

    @Override
    public void setAttributes(int[] bigsword, int[] sword, int[] dagger) {
        this.attributes[bigswordIndex] = bigsword;
        this.attributes[swordIndex] = sword;
        this.attributes[daggerIndex] = dagger;
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
    public String getName() {
        return this.names[currentTypeIndex];
    }

    @Override
    public Item getItem() {
        switch (currentTypeIndex) {
            case bigswordIndex:
                return SOUL_BIGSWORD;
            case swordIndex:
                return SOUL_SWORD;
            case daggerIndex:
                return SOUL_DAGGER;
            default:
                return null;
        }
    }

    @Override
    public int[] getBigswordAttributes() {
        return this.attributes[bigswordIndex];
    }

    @Override
    public void setBigswordAttributes(int[] bigsword) {
        this.attributes[bigswordIndex] = bigsword;
    }

    @Override
    public int[] getSwordAttributes() {
        return this.attributes[swordIndex];
    }

    @Override
    public void setSwordAttributes(int[] sword) {
        this.attributes[swordIndex] = sword;
    }

    @Override
    public int[] getDaggerAttributes() {
        return this.attributes[daggerIndex];
    }

    @Override
    public void setDaggerAttributes(int[] dagger) {
        this.attributes[daggerIndex] = dagger;
    }

    @Override
    public int getLevel() {
        for (int[] thing : attributes) {
            for (int thingg : thing) {
                System.out.printf("%d ", thingg);
            }

            System.out.println();
        }
        return this.attributes[currentTypeIndex][levelIndex];
    }

    @Override
    public void setLevel(int level) {
        this.attributes[currentTypeIndex][levelIndex] = level;
    }

    @Override
    public void addLevel() {
        addAttribute(this.attributes[currentTypeIndex][levelIndex]++ % 10);
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

    public int getCurrentTypeIndex() {
        return this.currentTypeIndex;
    }

    public void setCurrentTypeIndex(int index) {
        this.currentTypeIndex = index;
    }
}
