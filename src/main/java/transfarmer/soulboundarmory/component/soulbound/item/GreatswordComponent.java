package transfarmer.soulboundarmory.component.soulbound.item;

import nerdhub.cardinal.components.api.ComponentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import transfarmer.farmerlib.collection.CollectionUtil;
import transfarmer.farmerlib.nbt.NBTUtil;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.component.entity.IEntityData;
import transfarmer.soulboundarmory.skill.common.SkillLeeching;
import transfarmer.soulboundarmory.skill.greatsword.SkillFreezing;
import transfarmer.soulboundarmory.skill.greatsword.SkillLeaping;
import transfarmer.soulboundarmory.statistics.EnchantmentStorage;
import transfarmer.soulboundarmory.statistics.SkillStorage;
import transfarmer.soulboundarmory.statistics.StatisticType;
import transfarmer.soulboundarmory.statistics.Statistics;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static transfarmer.soulboundarmory.Main.GREATSWORD_COMPONENT;
import static transfarmer.soulboundarmory.Main.IMPACT;
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

public class GreatswordComponent extends SoulboundItemComponent<GreatswordComponent> implements IGreatswordComponent {
    protected CompoundTag cannotFreeze;
    protected int leapDuration;
    protected double leapForce;

    public GreatswordComponent(final ItemStack itemStack) {
        super(itemStack);

        this.statistics = Statistics.builder()
                .category(DATUM, XP, LEVEL, SKILL_POINTS, ATTRIBUTE_POINTS, ENCHANTMENT_POINTS, SPENT_ATTRIBUTE_POINTS, SPENT_ENCHANTMENT_POINTS)
                .category(ATTRIBUTE, ATTACK_SPEED, ATTACK_DAMAGE, CRITICAL_STRIKE_PROBABILITY, KNOCKBACK, EFFICIENCY, REACH)
                .min(0.8, ATTACK_SPEED).min(6, ATTACK_DAMAGE).min(6, REACH).build();
        this.enchantments = new EnchantmentStorage((final Enchantment enchantment) -> {
            final String name = enchantment.getName(1).asString().toLowerCase();

            return !CollectionUtil.hashSet(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                    && (enchantment == IMPACT || !name.contains("soulbound")) && !name.contains("holding")
                    && !name.contains("mending");
        });
        this.skillStorage = new SkillStorage(new SkillLeeching(), new SkillLeaping(), new SkillFreezing());
                this.cannotFreeze = new CompoundTag();
    }

    @Nonnull
    @Override
    public ComponentType<GreatswordComponent> getComponentType() {
        return GREATSWORD_COMPONENT;
    }

    @Override
    public double getLeapForce() {
        return this.leapForce;
    }

    @Override
    public void setLeapForce(final double force) {
        this.resetLeapForce();
        this.leapForce = force;
    }

    @Override
    public void resetLeapForce() {
        this.leapForce = 0;
        this.leapDuration = 0;

        NBTUtil.clear(this.cannotFreeze);
    }

    @Override
    public int getLeapDuration() {
        return leapDuration;
    }

    @Override
    public void setLeapDuration(final int ticks) {
        this.leapDuration = ticks;
    }

    @Override
    public void freeze(final Entity entity, final int ticks, final double damage) {
        final Optional<IEntityData> component = IEntityData.maybeGet(entity);
        final UUID id = entity.getUuid();
        final String key = id.toString();

        if (!this.cannotFreeze.contains(key) && entity.isAlive() && component.isPresent()) {
            component.get().freeze(this.getPlayer(), ticks, (float) damage);

            this.cannotFreeze.putUuid(key, id);
        }
    }

    @Override
    public double getIncrease(final StatisticType statistic) {
        return statistic == ATTACK_SPEED
                ? 0.02
                : statistic == ATTACK_DAMAGE
                ? 0.1
                : statistic == CRITICAL_STRIKE_PROBABILITY
                ? 0.01
                : statistic == KNOCKBACK
                ? 0.6
                : statistic == EFFICIENCY
                ? 0.3
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

        this.cannotFreeze = tag.getCompound("cannotFreeze");
    }

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull CompoundTag tag) {
        tag = super.toTag(tag);

        tag.put("cannotFreeze", this.cannotFreeze);

        return tag;
    }
}
