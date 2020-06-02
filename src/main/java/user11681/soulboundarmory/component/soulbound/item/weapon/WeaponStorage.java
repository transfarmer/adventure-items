package user11681.soulboundarmory.component.soulbound.item.weapon;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.item.Item;
import user11681.soulboundarmory.client.gui.screen.common.EnchantmentTab;
import user11681.soulboundarmory.client.gui.screen.common.ScreenTab;
import user11681.soulboundarmory.client.gui.screen.common.SkillTab;
import user11681.soulboundarmory.client.gui.screen.weapon.WeaponAttributeTab;
import user11681.soulboundarmory.client.gui.screen.weapon.WeaponSelectionTab;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.component.statistics.Statistic;
import user11681.soulboundarmory.component.statistics.StatisticType;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.item.SoulboundWeaponItem;
import user11681.usersmanual.collections.CollectionUtil;

import static user11681.soulboundarmory.component.statistics.StatisticType.ATTACK_DAMAGE;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTACK_SPEED;
import static user11681.soulboundarmory.component.statistics.StatisticType.ATTRIBUTE_POINTS;
import static user11681.soulboundarmory.component.statistics.StatisticType.CRITICAL_STRIKE_PROBABILITY;
import static user11681.soulboundarmory.component.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;

public abstract class WeaponStorage<T> extends ItemStorage<T> {
    public WeaponStorage(final SoulboundComponent component, final Item item) {
        super(component, item);
    }

    public double getAttributeRelative(final StatisticType statistic) {
        if (statistic == ATTACK_SPEED) {
            return this.getAttribute(ATTACK_SPEED) - 4;
        }

        if (statistic == ATTACK_DAMAGE) {
            return this.getAttribute(ATTACK_DAMAGE) - 1;
        }

        return super.getAttributeRelative(statistic);
    }

    public double getAttributeTotal(final StatisticType statistic) {
        if (statistic == ATTACK_DAMAGE) {
            double attackDamage = this.getAttribute(ATTACK_DAMAGE);

            for (final Enchantment enchantment : this.enchantments) {
                attackDamage += enchantment.getAttackDamage(this.getEnchantment(enchantment), EntityGroup.DEFAULT);
            }

            return attackDamage;
        }

        return this.getAttribute(statistic);
    }

    public void addAttribute(final StatisticType attribute, final int amount) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            if (sign > 0 && this.getDatum(ATTRIBUTE_POINTS) > 0 || sign < 0 && this.getDatum(SPENT_ATTRIBUTE_POINTS) > 0) {
                this.addDatum(ATTRIBUTE_POINTS, -sign);
                this.addDatum(SPENT_ATTRIBUTE_POINTS, sign);

                final double change = sign * this.getIncrease(attribute);

                if ((attribute.equals(CRITICAL_STRIKE_PROBABILITY) && this.getAttribute(CRITICAL_STRIKE_PROBABILITY) + change >= 1)) {
                    this.setAttribute(attribute, 1);

                    return;
                }

                final Statistic statistic = this.statistics.get(attribute);

                if (this.getAttribute(attribute) + change <= statistic.min()) {
                    this.setAttribute(attribute, statistic.min());

                    return;
                }

                this.statistics.add(attribute, change);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public List<ScreenTab> getTabs() {
        List<ScreenTab> tabs = new ArrayList<>();
        tabs = CollectionUtil.arrayList(new WeaponSelectionTab(this.component, tabs), new WeaponAttributeTab(this.component, tabs), new EnchantmentTab(this.component, tabs), new SkillTab(this.component, tabs));

        return tabs;
    }

    public int getLevelXP(final int level) {
        return this.canLevelUp()
                ? Configuration.instance().initialWeaponXP + 3 * (int) Math.round(Math.pow(level, 1.65))
                : -1;
    }

    public Class<? extends SoulboundItem> getBaseItemClass() {
        return SoulboundWeaponItem.class;
    }
}
