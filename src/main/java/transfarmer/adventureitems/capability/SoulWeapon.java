package transfarmer.adventureitems.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import transfarmer.adventureitems.Main;
import transfarmer.adventureitems.SoulWeapons;
import transfarmer.adventureitems.SoulWeapons.WeaponType;

import java.util.Random;

@SuppressWarnings("DuplicateBranchesInSwitch")
public class SoulWeapon implements ISoulWeapon {
    private WeaponType weaponType;
    private int level = 0;
    private int points;
    private int special;
    private int maxSpecial;
    private int hardness;
    private int knockback;
    private int attackDamage;
    private int critical;

    @Override
    public void setData(WeaponType weaponType, int level, int points, int special, int maxSpecial,
                        int hardness, int knockback, int attackDamage, int critical) {
        this.weaponType = weaponType;
        this.level = level;
        this.points = points;
        this.special = special;
        this.maxSpecial = maxSpecial;
        this.hardness = hardness;
        this.knockback = knockback;
        this.attackDamage = attackDamage;
        this.critical = critical;
    }

    @Override
    public WeaponType getWeaponType() {
        return this.weaponType;
    }

    @Override
    public void setWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void addLevel() {
        increaseAttribute(this.level++ % 10);
    }

    @Override
    public int getPoints() {
        return this.points;
    }

    @Override
    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public void addPoint() {
        this.points++;
    }

    @Override
    public int getMaxSpecial() {
        return this.maxSpecial;
    }

    @Override
    public int getSpecial() {
        return this.special;
    }

    @Override
    public void setSpecial(int special) {
        this.special = special;
    }

    @Override
    public void addSpecial() {
        this.special++;
    }

    @Override
    public int getHardness() {
        return this.hardness;
    }

    @Override
    public void setHardness(int hardness) {
        this.hardness = hardness;
    }

    @Override
    public void addHardness(int amount) {
        this.hardness += amount;
    }

    @Override
    public int getKnockback() {
        return this.knockback;
    }

    @Override
    public void setKnockback(int knockback) {
        this.knockback = knockback;
    }

    @Override
    public void addKnockback(int amount) {
        this.knockback += amount;
    }

    @Override
    public int getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    @Override
    public void addAttackDamage(int amount) {
        this.attackDamage += amount;
    }

    @Override
    public int getCritical() {
        return this.critical;
    }

    @Override
    public void setCritical(int critical) {
        this.critical = critical;
    }

    @Override
    public void addCritical(int amount) {
        this.critical += amount;
    }

    @Override
    public void increaseAttribute(int attributeNumber) {
        switch (this.weaponType) {
            case BIGSWORD:
                switch (attributeNumber) {
                    case 0:
                        addPoint();
                        break;
                    case 1:
                        if (this.special > this.maxSpecial) {
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
                        if (this.special > this.maxSpecial) {
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
                        increaseAttribute(new Random().nextInt(10));
                }
            case SWORD:
                switch (attributeNumber) {
                    case 0:
                        addPoint();
                        break;
                    case 1:
                        if (this.special > this.maxSpecial) {
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
                        if (this.special > this.maxSpecial) {
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
                        increaseAttribute(new Random().nextInt(10));
                }
            case DAGGER:
                switch (attributeNumber) {
                    case 0:
                        addPoint();
                        break;
                    case 1:
                        if (this.special > this.maxSpecial) {
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
                        if (this.special > this.maxSpecial) {
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
                        increaseAttribute(new Random().nextInt(10));
                }
        }
    }

    @Override
    public boolean hasSoulWeapon(PlayerEntity player) {
        return player.inventory.hasAny(SoulWeapons.getSoulWeapons());
    }

    @Override
    public boolean isSoulWeaponEquipped(PlayerEntity player) {
        for (final Item WEAPON : SoulWeapons.getSoulWeapons()) {
            if (player.inventory.getCurrentItem().isItemEqual(new ItemStack(WEAPON))) {
                return true;
            }
        }

        return false;
    }
}
