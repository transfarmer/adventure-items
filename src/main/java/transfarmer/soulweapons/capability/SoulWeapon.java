package transfarmer.soulweapons.capability;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulweapons.weapon.SoulAttributeModifier;
import transfarmer.soulweapons.weapon.SoulWeaponAttribute;
import transfarmer.soulweapons.weapon.SoulWeaponType;

import java.util.Random;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.ATTACK_DAMAGE;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.ATTACK_SPEED;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.CRITICAL;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.EFFICIENCY;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.KNOCKBACK;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.LEVEL;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.POINTS;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.SPECIAL;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.XP;
import static transfarmer.soulweapons.weapon.SoulWeaponType.NONE;

@SuppressWarnings("DuplicateBranchesInSwitch")
public class SoulWeapon implements ISoulWeapon {
    private static final String[] weaponNames = {"bigsword", "sword", "dagger"};
    public static final String[][] specialNames = {
        {"charge"},
        {"blocking", "blocking master"},
        {"throwing", "perforation", "return", "sneak return"}
    };
    private SoulWeaponType currentType = NONE;
    private int[][] attributes = new int[3][9];

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
        switch (currentType) {
            case GREATSWORD:
                switch (attributeNumber) {
                    case 0:
                        addPoint();
                        break;
                    case 1:
                        if (this.getSpecial() < this.getMaxSpecials()) {
                            addSpecial();
                            break;
                        }
                    case 2:
                        addEfficiency(5);
                        break;
                    case 3:
                        addKnockback(1);
                        break;
                    case 4:
                        addAttackDamage(1);
                        break;
                    case 5:
                        if (this.getSpecial() < this.getMaxSpecials()) {
                            addSpecial();
                            break;
                        }
                    case 6:
                        addEfficiency(4);
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
            case SWORD:
                switch (attributeNumber) {
                    case 0:
                        addPoint();
                        break;
                    case 1:
                        if (this.getSpecial() < this.getMaxSpecials()) {
                            addSpecial();
                            break;
                        }
                    case 2:
                        addEfficiency(4);
                        break;
                    case 3:
                        addKnockback(1);
                        break;
                    case 4:
                        addAttackDamage(1);
                        break;
                    case 5:
                        addPoint();
                    case 6:
                        addEfficiency(3);
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
            case DAGGER:
                switch (attributeNumber) {
                    case 0:
                        addPoint();
                        break;
                    case 1:
                        if (this.getSpecial() < this.getMaxSpecials()) {
                            addSpecial();
                            break;
                        }
                    case 2:
                        addEfficiency(4);
                        break;
                    case 3:
                        addCritical(2);
                        break;
                    case 4:
                        addAttackDamage(1);
                        break;
                    case 5:
                        if (this.getSpecial() < this.getMaxSpecials()) {
                            addSpecial();
                            break;
                        }
                    case 6:
                        addEfficiency(3);
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
    public void addAttribute(SoulWeaponAttribute attribute) {
        switch (attribute) {
            case ATTACK_SPEED:
                this.addAttackSpeed(0.1F);
                break;
            case ATTACK_DAMAGE:
                this.addAttackDamage(1);
                break;
            case CRITICAL:
                this.addCritical(1);
                break;
            case KNOCKBACK:
                this.addKnockback(1);
                break;
            case EFFICIENCY:
                this.addEfficiency(5);
        }

        this.attributes[currentType.index][POINTS.index] -= 1;
    }

    @Override
    public String getWeaponName() {
        return getWeaponName(this.getIndex());
    }

    @Override
    public String getWeaponName(int index) {
        return weaponNames[index];
    }

    @Override
    public Item getItem() {
        return currentType.item;
    }

    @Override
    public ItemStack getItemStack() {
        return getItemStack(currentType);
    }

    public ItemStack getItemStack(ItemStack itemStack) {
        return getItemStack(SoulWeaponType.getType(itemStack));
    }

    @Override
    public ItemStack getItemStack(SoulWeaponType weaponType) {
        final ItemStack itemStack = weaponType.getItemStack();
        final AttributeModifier[] ATTRIBUTE_MODIFIERS = getAttributeModifiers(weaponType);

        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED.getName(), ATTRIBUTE_MODIFIERS[0], MAINHAND);
        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), ATTRIBUTE_MODIFIERS[1], MAINHAND);

        return itemStack;
    }

    @Override
    public AttributeModifier[] getAttributeModifiers(SoulWeaponType weaponType) {
        return new AttributeModifier[]{
            new AttributeModifier(SoulAttributeModifier.ATTACK_SPEED_UUID, "generic.attackSpeed", getAttackSpeed(weaponType), ADD),
            new AttributeModifier(SoulAttributeModifier.ATTACK_DAMAGE_UUID, "generic.attackDamage", getAttackDamage(weaponType), ADD)
        };
    }

    @Override
    public String[] getTooltip(ItemStack itemStack) {
        SoulWeaponType weaponType = SoulWeaponType.getType(itemStack);

        return new String[]{
            String.format(" %.1f attack speed", this.getAttackSpeed(weaponType) + 4),
            String.format(" %d attack damage", this.getAttackDamage(weaponType) + 1),
            "",
            ""
        };
    }

    @Override
    public int getNextLevelXP() {
        return getNextLevelXP(this.currentType);
    }

    @Override
    public int getNextLevelXP(SoulWeaponType weaponType) {
        return 32 + 4 * (int) Math.round(Math.pow(this.getLevel(weaponType), 1.2));
    }

    @Override
    public int getXP() {
        return getXP(this.getIndex());
    }

    @Override
    public int getXP(SoulWeaponType weaponType) {
        return getXP(weaponType.index);
    }

    @Override
    public int getXP(int index) {
        return this.attributes[index][XP.index];
    }

    @Override
    public void setXP(int xp) {
        this.attributes[this.getIndex()][XP.index] = xp;
    }

    @Override
    public boolean addXP(int xp) {
        this.attributes[this.getIndex()][XP.index] += xp;

        if (this.getXP() >= this.getNextLevelXP()) {
            this.addXP(-this.getNextLevelXP());
            this.addLevel();
            return true;
        }

        return false;
    }

    @Override
    public boolean addXP(float xp) {
        return addXP(Math.round(xp));
    }

    @Override
    public int getLevel() {
        return this.getLevel(this.getIndex());
    }

    @Override
    public int getLevel(SoulWeaponType weaponType) {
        return this.getLevel(weaponType.index);
    }

    @Override
    public int getLevel(int index) {
        return this.attributes[index][LEVEL.index];
    }

    @Override
    public void setLevel(int level) {
        this.attributes[getIndex()][LEVEL.index] = level;
    }

    @Override
    public void addLevel() {
        this.addLevel(getIndex());
    }

    @Override
    public void addLevel(int index) {
        this.addAttribute(this.attributes[index][LEVEL.index]++ % 10);
    }

    @Override
    public int getPoints() {
        return this.attributes[getIndex()][POINTS.index];
    }

    @Override
    public void setPoints(int points) {
        this.attributes[getIndex()][POINTS.index] = points;
    }

    @Override
    public void addPoint() {
        this.attributes[getIndex()][POINTS.index]++;
    }

    @Override
    public int getMaxSpecials() {
        return specialNames[getIndex()].length;
    }

    @Override
    public int getSpecial() {
        return this.attributes[getIndex()][SPECIAL.index];
    }

    @Override
    public void setSpecial(int special) {
        this.attributes[getIndex()][SPECIAL.index] = special;
    }

    @Override
    public void addSpecial() {
        this.attributes[getIndex()][SPECIAL.index]++;
    }

    @Override
    public int getEfficiency() {
        return this.attributes[getIndex()][EFFICIENCY.index];
    }

    @Override
    public void setEfficiency(int efficiency) {
        this.attributes[getIndex()][EFFICIENCY.index] = efficiency;
    }

    @Override
    public void addEfficiency(int amount) {
        this.attributes[getIndex()][EFFICIENCY.index] += amount;
    }

    @Override
    public int getKnockback() {
        return this.attributes[getIndex()][KNOCKBACK.index];
    }

    @Override
    public void setKnockback(int knockback) {
        this.attributes[getIndex()][KNOCKBACK.index] = knockback;
    }

    @Override
    public void addKnockback(int amount) {
        this.attributes[getIndex()][KNOCKBACK.index] += amount;
    }

    @Override
    public int getAttackDamage() {
        return this.getAttackDamage(currentType);
    }

    @Override
    public int getAttackDamage(SoulWeaponType weaponType) {
        return this.attributes[weaponType.index][ATTACK_DAMAGE.index] + (int) weaponType.item.getAttackDamage();
    }

    @Override
    public void setAttackDamage(int attackDamage) {
        this.attributes[getIndex()][ATTACK_DAMAGE.index] = attackDamage;
    }

    @Override
    public void addAttackDamage(int amount) {
        this.attributes[getIndex()][ATTACK_DAMAGE.index] += amount;
    }

    @Override
    public int getCritical() {
        return this.attributes[getIndex()][CRITICAL.index];
    }

    @Override
    public void setCritical(int critical) {
        this.attributes[getIndex()][CRITICAL.index] = critical;
    }

    @Override
    public void addCritical(int amount) {
        this.attributes[getIndex()][CRITICAL.index] += amount;
    }

    @Override
    public float getAttackSpeed() {
        return getAttackSpeed(this.currentType);
    }

    @Override
    public float getAttackSpeed(SoulWeaponType weaponType) {
        return this.attributes[weaponType.index][ATTACK_SPEED.index] / 10F + weaponType.item.getAttackSpeed();
    }

    @Override
    public void setAttackSpeed(float attackSpeed) {
        this.attributes[getIndex()][ATTACK_SPEED.index] = (int) attackSpeed * 10;
    }

    @Override
    public void addAttackSpeed(float amount) {
        this.attributes[getIndex()][ATTACK_SPEED.index] += amount * 10;
    }

    @Override
    public void setCurrentType(SoulWeaponType type) {
        this.currentType = type;
    }

    @Override
    public void setCurrentType(int index) {
        this.currentType = SoulWeaponType.getType(index);
    }

    @Override
    public int getIndex() {
        return this.currentType.index;
    }

    @Override
    public boolean hasAttributes() {
        return this.attributes[0].length != 0 && this.attributes[1].length != 0 && this.attributes[2].length != 0;
    }

    @Override
    public SoulWeaponType getCurrentType() {
        return this.currentType;
    }
}
