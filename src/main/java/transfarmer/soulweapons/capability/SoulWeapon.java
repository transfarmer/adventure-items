package transfarmer.soulweapons.capability;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulweapons.SoulAttributeModifier;
import transfarmer.soulweapons.SoulWeaponType;

import java.util.Random;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraft.item.ItemStack.DECIMALFORMAT;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soulweapons.SoulWeaponAttribute.ATTACK_DAMAGE;
import static transfarmer.soulweapons.SoulWeaponAttribute.ATTACK_SPEED;
import static transfarmer.soulweapons.SoulWeaponAttribute.CRITICAL;
import static transfarmer.soulweapons.SoulWeaponAttribute.EFFICIENCY;
import static transfarmer.soulweapons.SoulWeaponAttribute.KNOCKBACK;
import static transfarmer.soulweapons.SoulWeaponAttribute.LEVEL;
import static transfarmer.soulweapons.SoulWeaponAttribute.POINTS;
import static transfarmer.soulweapons.SoulWeaponAttribute.SPECIAL;
import static transfarmer.soulweapons.SoulWeaponType.NONE;

@SuppressWarnings("DuplicateBranchesInSwitch")
public class SoulWeapon implements ISoulWeapon {
    private static final String[] weaponNames = {"bigsword", "sword", "dagger", null};
    private static final String[] attributeNames = {"level", "points", "special", "maxSpecial",
            "efficiency", "knockback", "attackDamage", "critical", "attackSpeed"};
    public static final String[][] specialNames = {
        {"charge"},
        {"blocking", "blocking master"},
        {"throwing", "perforation", "return", "sneak return"}};
    private static final int[] maxSpecials = {1, 2, 4};
    private SoulWeaponType currentType = NONE;
    private int[][] attributes = new int[3][8];

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
            case BIGSWORD:
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
    public String getWeaponName() {
        return getWeaponName(this.currentType.getIndex());
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
        final AttributeModifier[] attributeModifiers = getAttributeModifiers(SoulWeaponType.getType(itemStack));
        final String[] tooltip = new String[attributeModifiers.length];

        tooltip[0] = " " + I18n.format("attribute.modifier.equals.0",
            DECIMALFORMAT.format(attributeModifiers[0].getAmount() + 4), I18n.format("attribute.name.generic.attackSpeed"));

        tooltip[1] = " " + I18n.format("attribute.modifier.equals.0",
            DECIMALFORMAT.format(attributeModifiers[1].getAmount() + 1), I18n.format("attribute.name.generic.attackDamage"));

        return tooltip;
    }

    @Override
    public int getLevel() {
        return this.attributes[currentType.getIndex()][LEVEL.index];
    }

    @Override
    public int getLevel(int index) {
        return this.attributes[index][LEVEL.index];
    }

    @Override
    public void setLevel(int level) {
        this.attributes[currentType.getIndex()][LEVEL.index] = level;
    }

    @Override
    public void addLevel() {
        addLevel(currentType.getIndex());
    }

    @Override
    public void addLevel(int index) {
        addAttribute(this.attributes[index][LEVEL.index]++ % 10);
    }

    @Override
    public int getPoints() {
        return this.attributes[currentType.getIndex()][POINTS.index];
    }

    @Override
    public void setPoints(int points) {
        this.attributes[currentType.getIndex()][POINTS.index] = points;
    }

    @Override
    public void addPoint() {
        this.attributes[currentType.getIndex()][POINTS.index]++;
    }

    @Override
    public int getMaxSpecials() {
        return maxSpecials[currentType.getIndex()];
    }

    @Override
    public int getSpecial() {
        return this.attributes[currentType.getIndex()][SPECIAL.index];
    }

    @Override
    public void setSpecial(int special) {
        this.attributes[currentType.getIndex()][SPECIAL.index] = special;
    }

    @Override
    public void addSpecial() {
        this.attributes[currentType.getIndex()][SPECIAL.index]++;
    }

    @Override
    public int getEfficiency() {
        return this.attributes[currentType.getIndex()][EFFICIENCY.index];
    }

    @Override
    public void setEfficiency(int efficiency) {
        this.attributes[currentType.getIndex()][EFFICIENCY.index] = efficiency;
    }

    @Override
    public void addEfficiency(int amount) {
        this.attributes[currentType.getIndex()][EFFICIENCY.index] += amount;
    }

    @Override
    public int getKnockback() {
        return this.attributes[currentType.getIndex()][KNOCKBACK.index];
    }

    @Override
    public void setKnockback(int knockback) {
        this.attributes[currentType.getIndex()][KNOCKBACK.index] = knockback;
    }

    @Override
    public void addKnockback(int amount) {
        this.attributes[currentType.getIndex()][KNOCKBACK.index] += amount;
    }

    @Override
    public int getAttackDamage() {
        return this.getAttackDamage(currentType);
    }

    @Override
    public int getAttackDamage(SoulWeaponType weaponType) {
        return this.attributes[weaponType.getIndex()][ATTACK_DAMAGE.index] + (int) weaponType.getItem().getAttackDamage();
    }

    @Override
    public void setAttackDamage(int attackDamage) {
        this.attributes[currentType.getIndex()][ATTACK_DAMAGE.index] = attackDamage;
    }

    @Override
    public void addAttackDamage(int amount) {
        this.attributes[currentType.getIndex()][ATTACK_DAMAGE.index] += amount;
    }

    @Override
    public int getCritical() {
        return this.attributes[currentType.getIndex()][CRITICAL.index];
    }

    @Override
    public void setCritical(int critical) {
        this.attributes[currentType.getIndex()][CRITICAL.index] = critical;
    }

    @Override
    public void addCritical(int amount) {
        this.attributes[currentType.getIndex()][CRITICAL.index] += amount;
    }

    @Override
    public float getAttackSpeed() {
        return this.attributes[currentType.getIndex()][ATTACK_SPEED.index] / 10F;
    }

    @Override
    public float getAttackSpeed(SoulWeaponType weaponType) {
        return this.attributes[weaponType.getIndex()][ATTACK_SPEED.index] + weaponType.getItem().getAttackSpeed();
    }

    @Override
    public void setAttackSpeed(float attackSpeed) {
        this.attributes[currentType.getIndex()][ATTACK_SPEED.index] = (int) attackSpeed * 10;
    }

    @Override
    public void addAttackSpeed(float amount) {
        this.attributes[currentType.getIndex()][ATTACK_SPEED.index] += amount * 10;
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
    public boolean hasAttributes() {
        return this.attributes[0].length != 0 && this.attributes[1].length != 0 && this.attributes[2].length != 0;
    }

    @Override
    public SoulWeaponType getCurrentType() {
        return this.currentType;
    }
}
