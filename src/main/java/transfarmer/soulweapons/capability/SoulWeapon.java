package transfarmer.soulweapons.capability;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.soulweapons.Configuration;
import transfarmer.soulweapons.weapon.SoulAttributeModifier;
import transfarmer.soulweapons.weapon.SoulWeaponAttribute;
import transfarmer.soulweapons.weapon.SoulWeaponType;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.ATTACK_DAMAGE;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.ATTACK_SPEED;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.CRITICAL;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.EFFICIENCY;
import static transfarmer.soulweapons.weapon.SoulWeaponAttribute.KNOCKBACK;
import static transfarmer.soulweapons.weapon.SoulWeaponDatum.LEVEL;
import static transfarmer.soulweapons.weapon.SoulWeaponDatum.POINTS;
import static transfarmer.soulweapons.weapon.SoulWeaponDatum.SKILL;
import static transfarmer.soulweapons.weapon.SoulWeaponDatum.XP;
import static transfarmer.soulweapons.weapon.SoulWeaponType.NONE;

@SuppressWarnings("DuplicateBranchesInSwitch")
public class SoulWeapon implements ISoulWeapon {
    private static final String[] weaponNames = {"bigsword", "sword", "dagger"};
    public static final String[][] skillNames = {
        {"charge"},
        {}, // lifesteal?
        {"throwing", "perforation", "return", "sneak return"}
    };
    private SoulWeaponType currentType = NONE;
    private int[][] data = new int[3][4];
    private float[][] attributes = new float[3][5];

    @Override
    public float[][] getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(float[][] attributes) {
        this.attributes = attributes;
    }

    @Override
    public int[][] getData() {
        return this.data;
    }

    @Override
    public void setData(int[][] data) {
        this.data = data;
    }

    @Override
    public void addAttribute(int number) {
        if (Configuration.onlyPoints) {
            addPoint();
            return;
        }

        switch (currentType) {
            case GREATSWORD:
                switch (number) {
                    case 0:
                        addPoint();
                        break;
                    case 1:
                        if (this.getSkills() < this.getMaxSkills()) {
                            addSpecial();
                            break;
                        } else {
                            this.addAttribute(new Random().nextInt(10));
                        }
                    case 2:
                        addEfficiency(0.5F);
                        break;
                    case 3:
                        addKnockback(1);
                        break;
                    case 4:
                        addAttackDamage(1);
                        break;
                    case 5:
                        if (this.getSkills() < this.getMaxSkills()) {
                            addSpecial();
                            break;
                        } else {
                            this.addAttribute(new Random().nextInt(10));
                        }
                    case 6:
                        addEfficiency(0.4F);
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
                }

                break;
            case SWORD:
                switch (number) {
                    case 0:
                        addPoint();
                        break;
                    case 1:
                        if (this.getSkills() < this.getMaxSkills()) {
                            addSpecial();
                            break;
                        }
                    case 2:
                        addEfficiency(0.4F);
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
                        addEfficiency(0.3F);
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
                switch (number) {
                    case 0:
                        addPoint();
                        break;
                    case 1:
                        if (this.getSkills() < this.getMaxSkills()) {
                            addSpecial();
                            break;
                        }
                    case 2:
                        addEfficiency(0.4F);
                        break;
                    case 3:
                        addCritical(2);
                        break;
                    case 4:
                        addAttackDamage(1);
                        break;
                    case 5:
                        if (this.getSkills() < this.getMaxSkills()) {
                            addSpecial();
                            break;
                        }
                    case 6:
                        addEfficiency(0.3F);
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
                this.addAttackSpeed(0.2F);
                break;
            case ATTACK_DAMAGE:
                this.addAttackDamage(1);
                break;
            case CRITICAL:
                this.addCritical(3);
                break;
            case KNOCKBACK:
                this.addKnockback(1);
                break;
            case EFFICIENCY:
                this.addEfficiency(0.5F);
        }

        this.data[currentType.index][POINTS.index] -= 1;
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
        final SoulWeaponType WEAPON_TYPE = SoulWeaponType.getType(itemStack);
        final NumberFormat FORMAT = DecimalFormat.getInstance();

        return new String[]{
            String.format(" %s attack speed", FORMAT.format(this.getAttackSpeed(WEAPON_TYPE) + 4)),
            String.format(" %s attack damage", FORMAT.format(this.getAttackDamage(WEAPON_TYPE) + 1)),
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
        return 32 + 4 * (int) Math.round(Math.pow(this.getLevel(weaponType), 1.5));
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
        return this.data[index][XP.index];
    }

    @Override
    public boolean addXP(int xp) {
        this.data[this.getIndex()][XP.index] += xp;

        if (this.getXP() >= this.getNextLevelXP() && Configuration.maxLevel > this.getLevel()) {
            this.addXP(-this.getNextLevelXP());
            this.addLevel();
            return true;
        }

        return false;
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
        return this.data[index][LEVEL.index];
    }

    @Override
    public void addLevel() {
        this.addLevel(getIndex());
    }

    @Override
    public void addLevel(int index) {
        this.addAttribute(this.data[index][LEVEL.index]++ % 10);
    }

    @Override
    public int getPoints() {
        return this.data[getIndex()][POINTS.index];
    }

    @Override
    public void addPoint() {
        this.data[getIndex()][POINTS.index]++;
    }

    @Override
    public int getMaxSkills() {
        return skillNames[getIndex()].length;
    }

    @Override
    public int getSkills() {
        return this.data[getIndex()][SKILL.index];
    }

    @Override
    public void addSpecial() {
        this.data[getIndex()][SKILL.index]++;
    }

    @Override
    public float getEfficiency() {
        return this.attributes[getIndex()][EFFICIENCY.index];
    }

    @Override
    public void addEfficiency(float amount) {
        this.attributes[getIndex()][EFFICIENCY.index] += amount;
    }

    @Override
    public float getKnockback() {
        return this.attributes[getIndex()][KNOCKBACK.index];
    }

    @Override
    public void addKnockback(float amount) {
        this.attributes[getIndex()][KNOCKBACK.index] += amount;
    }

    @Override
    public float getAttackDamage() {
        return this.getAttackDamage(currentType);
    }

    @Override
    public float getAttackDamage(SoulWeaponType weaponType) {
        return this.attributes[weaponType.index][ATTACK_DAMAGE.index] + weaponType.item.getAttackDamage();
    }

    @Override
    public void addAttackDamage(float amount) {
        this.attributes[getIndex()][ATTACK_DAMAGE.index] += amount;
    }

    @Override
    public float getAttackSpeed() {
        return getAttackSpeed(this.currentType);
    }

    @Override
    public float getAttackSpeed(SoulWeaponType weaponType) {
        return this.attributes[weaponType.index][ATTACK_SPEED.index] + weaponType.item.getAttackSpeed();
    }

    @Override
    public void addAttackSpeed(float amount) {
        this.attributes[getIndex()][ATTACK_SPEED.index] += amount;
    }

    @Override
    public float getCritical() {
        return this.attributes[getIndex()][CRITICAL.index];
    }

    @Override
    public void addCritical(float amount) {
        this.attributes[getIndex()][CRITICAL.index] += amount;
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
    public boolean hasAttributesAndData() {
        return this.attributes[0].length != 0 && this.attributes[1].length != 0 && this.attributes[2].length != 0
            && this.data[0].length != 0 && this.data[1].length != 0 && this.data[2].length != 0;
    }

    @Override
    public SoulWeaponType getCurrentType() {
        return this.currentType;
    }
}
