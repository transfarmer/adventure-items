package transfarmer.soulboundarmory.component.soulbound.item;

import nerdhub.cardinal.components.api.component.Component;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import transfarmer.farmerlib.collection.CollectionUtil;
import transfarmer.soulboundarmory.client.gui.screen.common.EnchantmentTab;
import transfarmer.soulboundarmory.client.gui.screen.common.ScreenTab;
import transfarmer.soulboundarmory.client.gui.screen.common.SkillTab;
import transfarmer.soulboundarmory.client.gui.screen.weapon.WeaponAttributesTab;
import transfarmer.soulboundarmory.client.gui.screen.weapon.WeaponSelectionTab;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.statistics.Statistic;
import transfarmer.soulboundarmory.statistics.StatisticType;

import java.util.ArrayList;
import java.util.List;

import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_DAMAGE;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.CRITICAL_STRIKE_PROBABILITY;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;

public abstract class SoulboundWeaponComponent<C extends Component> extends SoulboundItemComponent<C> {
    public SoulboundWeaponComponent(final ItemStack itemStack, final PlayerEntity player) {
        super(itemStack, player);
    }

    @Override
    public int getLevelXP(final int level) {
        return this.canLevelUp()
                ? MainConfig.instance().getInitialWeaponXP() + 3 * (int) Math.round(Math.pow(level, 1.65))
                : -1;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public List<ScreenTab> getTabs() {
        List<ScreenTab> tabs = new ArrayList<>();
        tabs = CollectionUtil.arrayList(new WeaponSelectionTab(tabs), new WeaponAttributesTab(tabs), new EnchantmentTab(this, tabs), new SkillTab(this, tabs));

        return tabs;
    }

    @Override
    public double getAttributeRelative(final StatisticType statistic) {
        if (statistic == ATTACK_SPEED) {
            return this.getAttribute(ATTACK_SPEED) - 4;
        }

        if (statistic == ATTACK_DAMAGE) {
            return this.getAttribute(ATTACK_DAMAGE) - 1;
        }

        return super.getAttributeRelative(statistic);
    }

    @Override
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

    @Override
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
}
