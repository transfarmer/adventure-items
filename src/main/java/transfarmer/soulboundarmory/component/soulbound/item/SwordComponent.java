package transfarmer.soulboundarmory.component.soulbound.item;

import nerdhub.cardinal.components.api.ComponentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import transfarmer.farmerlib.collection.CollectionUtil;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.skill.common.SkillLeeching;
import transfarmer.soulboundarmory.skill.sword.SkillSummonLightning;
import transfarmer.soulboundarmory.statistics.EnchantmentStorage;
import transfarmer.soulboundarmory.statistics.SkillStorage;
import transfarmer.soulboundarmory.statistics.Statistics;
import transfarmer.soulboundarmory.statistics.StatisticType;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static transfarmer.soulboundarmory.Main.IMPACT;
import static transfarmer.soulboundarmory.Main.SWORD_COMPONENT;
import static transfarmer.soulboundarmory.statistics.Category.ATTRIBUTE;
import static transfarmer.soulboundarmory.statistics.Category.DATUM;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_DAMAGE;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_SPEED;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.CRITICAL_STRIKE_PROBABILITY;
import static transfarmer.soulboundarmory.statistics.StatisticType.EFFICIENCY;
import static transfarmer.soulboundarmory.statistics.StatisticType.ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.KNOCKBACK;
import static transfarmer.soulboundarmory.statistics.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.StatisticType.REACH;
import static transfarmer.soulboundarmory.statistics.StatisticType.SKILL_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ATTRIBUTE_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.SPENT_ENCHANTMENT_POINTS;
import static transfarmer.soulboundarmory.statistics.StatisticType.XP;

public class SwordComponent extends SoulboundItemComponent<SwordComponent> implements ISwordComponent {
    protected int lightningCooldown;

    public SwordComponent(final ItemStack itemStack) {
        super(itemStack);

        this.statistics = Statistics.builder()
                .category(DATUM, XP, LEVEL, SKILL_POINTS, ATTRIBUTE_POINTS, ENCHANTMENT_POINTS, SPENT_ATTRIBUTE_POINTS, SPENT_ENCHANTMENT_POINTS)
                .category(ATTRIBUTE, ATTACK_SPEED, ATTACK_DAMAGE, CRITICAL_STRIKE_PROBABILITY, KNOCKBACK, EFFICIENCY, REACH)
                .min(1.6, ATTACK_SPEED).min(4, ATTACK_DAMAGE).min(3, REACH).build();
        this.enchantments = new EnchantmentStorage((final Enchantment enchantment) -> {
            final String name = enchantment.getName(1).asString().toLowerCase();

            return !CollectionUtil.hashSet(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                    && (enchantment == IMPACT || !name.contains("soulbound")) && !name.contains("holding")
                    && !name.contains("mending");
        });
        this.skillStorage = new SkillStorage(new SkillLeeching(), new SkillSummonLightning());
);
    }

    @Nonnull
    @Override
    public ComponentType<SwordComponent> getComponentType() {
        return SWORD_COMPONENT;
    }

    @Override
    public int getLightningCooldown() {
        return this.lightningCooldown;
    }

    @Override
    public void setLightningCooldown(final int ticks) {
        this.lightningCooldown = ticks;
    }

    @Override
    public void resetLightningCooldown() {
        if (!this.getPlayer().isCreative()) {
            this.lightningCooldown = (int) Math.round(96 / this.getAttribute(this.item, ATTACK_SPEED));
        }
    }

    @Override
    public double getIncrease(final StatisticType statistic) {
        return statistic == ATTACK_SPEED
                ? 0.03
                : statistic == ATTACK_DAMAGE
                ? 0.075
                : statistic == CRITICAL_STRIKE_PROBABILITY
                ? 0.015
                : statistic == KNOCKBACK
                ? 0.35
                : statistic == EFFICIENCY
                ? 0.2
                : 0;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public List<String> getTooltip() {
        final NumberFormat format = DecimalFormat.getInstance();
        final List<String> tooltip = new ArrayList<>();

        tooltip.add(String.format(" %s%s %s", Mappings.ATTACK_SPEED_FORMAT, format.format(this.getAttribute(ATTACK_SPEED)), Mappings.ATTACK_SPEED_NAME));
        tooltip.add(String.format(" %s%s %s", Mappings.ATTACK_DAMAGE_FORMAT, format.format(this.getAttributeTotal(ATTACK_DAMAGE)), Mappings.ATTACK_DAMAGE_NAME));

        tooltip.add("");
        tooltip.add("");

        if (this.getAttribute(CRITICAL_STRIKE_PROBABILITY) > 0) {
            tooltip.add(String.format(" %s%s%% %s", Mappings.CRITICAL_FORMAT, format.format(this.getAttribute(CRITICAL_STRIKE_PROBABILITY) * 100), Mappings.CRITICAL_NAME));
        }
        if (this.getAttribute(KNOCKBACK) > 0) {
            tooltip.add(String.format(" %s%s %s", Mappings.KNOCKBACK_ATTRIBUTE_FORMAT, format.format(this.getAttribute(KNOCKBACK)), Mappings.KNOCKBACK_ATTRIBUTE_NAME));
        }
        if (this.getAttribute(EFFICIENCY) > 0) {
            tooltip.add(String.format(" %s%s %s", Mappings.WEAPON_EFFICIENCY_FORMAT, format.format(this.getAttribute(EFFICIENCY)), Mappings.EFFICIENCY_NAME));
        }

        return tooltip;
    }

    @Override
    public void fromTag(@Nonnull final CompoundTag tag) {
        super.fromTag(tag);

        this.setLightningCooldown(tag.getInt("lightningCooldown"));
    }

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull final CompoundTag tag) {
        return super.toTag(tag);
    }
}
